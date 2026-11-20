package com.warriors.fidelite.service;

import com.warriors.fidelite.dto.ClassementDto;
import com.warriors.fidelite.dto.EchangeResponse;
import com.warriors.fidelite.exception.ConflictException;
import com.warriors.fidelite.exception.NotFoundException;
import com.warriors.fidelite.model.ActionFidelite;
import com.warriors.fidelite.model.Compte;
import com.warriors.fidelite.model.Mouvement;
import com.warriors.fidelite.model.NiveauFidelite;
import com.warriors.fidelite.model.Recompense;
import com.warriors.fidelite.repository.CompteRepository;
import com.warriors.fidelite.repository.MouvementRepository;
import com.warriors.fidelite.repository.RecompenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FidelityService {

    private final CompteRepository compteRepository;
    private final MouvementRepository mouvementRepository;
    private final RecompenseRepository recompenseRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${fidelite.points.inscription:5}")
    private int pointsInscription;
    @Value("${fidelite.points.presence:20}")
    private int pointsPresence;
    @Value("${fidelite.points.avis:10}")
    private int pointsAvis;
    @Value("${fidelite.niveaux.seuil-argent:100}")
    private int seuilArgent;
    @Value("${fidelite.niveaux.seuil-or:300}")
    private int seuilOr;

    // ------------------------------------------------------------------
    // Regles
    // ------------------------------------------------------------------

    public int pointsPour(ActionFidelite action) {
        return switch (action) {
            case INSCRIPTION -> pointsInscription;
            case PRESENCE -> pointsPresence;
            case AVIS -> pointsAvis;
            case ECHANGE -> throw new IllegalArgumentException("ECHANGE n'a pas de bareme fixe");
        };
    }

    public NiveauFidelite niveauPour(int points) {
        if (points >= seuilOr) return NiveauFidelite.OR;
        if (points >= seuilArgent) return NiveauFidelite.ARGENT;
        return NiveauFidelite.BRONZE;
    }

    // ------------------------------------------------------------------
    // Gain de points (appele par le listener RabbitMQ)
    // ------------------------------------------------------------------

    /**
     * Ajoute les points d'une action pour un evenement.
     * Un meme gain (participant + evenement + action) n'est attribue qu'une seule fois.
     *
     * @return true si les points ont ete ajoutes, false s'ils avaient deja ete attribues
     */
    public boolean gagnerPoints(String participantId, Long evenementId, ActionFidelite action) {
        String id = participantId + "_" + evenementId + "_" + action.name();
        if (mouvementRepository.existsById(id)) {
            log.info("Points deja attribues ({}), message ignore", id);
            return false;
        }
        int points = pointsPour(action);
        try {
            mouvementRepository.insert(Mouvement.builder()
                    .id(id)
                    .participantId(participantId)
                    .evenementId(evenementId)
                    .action(action)
                    .points(points)
                    .date(LocalDateTime.now())
                    .build());
        } catch (DuplicateKeyException e) {
            log.info("Points deja attribues ({}), message ignore", id);
            return false;
        }
        Compte compte = appliquerPoints(participantId, points);
        log.info("+{} points ({}) pour {} -> solde {} ({})", points, action, participantId,
                compte.getPoints(), compte.getNiveau());
        return true;
    }

    /** Ajoute (ou retire) des points de facon atomique, cree le compte au besoin, recalcule le niveau. */
    private Compte appliquerPoints(String participantId, int delta) {
        Query query = Query.query(Criteria.where("_id").is(participantId));
        Compte compte = mongoTemplate.findAndModify(query, new Update().inc("points", delta),
                FindAndModifyOptions.options().upsert(true).returnNew(true), Compte.class);
        return recalculerNiveau(compte);
    }

    private Compte recalculerNiveau(Compte compte) {
        NiveauFidelite niveau = niveauPour(compte.getPoints());
        if (compte.getNiveau() != niveau) {
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(compte.getParticipantId())),
                    Update.update("niveau", niveau), Compte.class);
            compte.setNiveau(niveau);
        }
        return compte;
    }

    // ------------------------------------------------------------------
    // Echange contre une recompense
    // ------------------------------------------------------------------

    public EchangeResponse echanger(String participantId, String recompenseId) {
        Recompense recompense = recompenseRepository.findById(recompenseId)
                .orElseThrow(() -> new NotFoundException("Recompense introuvable : " + recompenseId));

        // 1. decrementer le stock de facon atomique (refuse si stock = 0)
        Recompense apresStock = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(recompenseId).and("stock").gt(0)),
                new Update().inc("stock", -1),
                FindAndModifyOptions.options().returnNew(true), Recompense.class);
        if (apresStock == null) {
            throw new ConflictException("Stock epuise pour la recompense : " + recompense.getNom());
        }

        // 2. debiter les points de facon atomique (refuse si solde insuffisant)
        int cout = recompense.getCoutPoints();
        Compte compte = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(participantId).and("points").gte(cout)),
                new Update().inc("points", -cout),
                FindAndModifyOptions.options().returnNew(true), Compte.class);
        if (compte == null) {
            // le stock est remis en place
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(recompenseId)),
                    new Update().inc("stock", 1), Recompense.class);
            throw new ConflictException("Points insuffisants : il faut " + cout + " points");
        }
        compte = recalculerNiveau(compte);

        mouvementRepository.insert(Mouvement.builder()
                .participantId(participantId)
                .action(ActionFidelite.ECHANGE)
                .points(-cout)
                .date(LocalDateTime.now())
                .build());

        return new EchangeResponse(recompense.getNom(), cout, compte.getPoints(), compte.getNiveau());
    }

    // ------------------------------------------------------------------
    // Consultation
    // ------------------------------------------------------------------

    /** Mon compte : s'il n'existe pas encore, on renvoie un compte vide (0 point, BRONZE) sans l'enregistrer. */
    public Compte monCompte(String participantId) {
        return compteRepository.findById(participantId)
                .orElseGet(() -> Compte.builder()
                        .participantId(participantId).points(0).niveau(NiveauFidelite.BRONZE).build());
    }

    public Compte compte(String participantId) {
        return compteRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException("Compte introuvable : " + participantId));
    }

    public List<Mouvement> historique(String participantId) {
        return mouvementRepository.findByParticipantIdOrderByDateDesc(participantId);
    }

    public List<ClassementDto> classement(int limit) {
        int n = Math.max(1, Math.min(limit, 100));
        List<Compte> top = compteRepository
                .findAll(PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "points")))
                .getContent();
        List<ClassementDto> resultat = new ArrayList<>();
        int rang = 1;
        for (Compte c : top) {
            resultat.add(new ClassementDto(rang++, c.getParticipantId(), c.getPoints(), c.getNiveau()));
        }
        return resultat;
    }
}
