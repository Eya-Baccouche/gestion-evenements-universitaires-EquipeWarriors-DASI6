package com.warriors.feedback.feedback.service;

import com.warriors.feedback.config.RabbitMQConfig;
import com.warriors.feedback.feedback.client.EvenementsClient;
import com.warriors.feedback.feedback.event.AvisDeposeEvent;
import com.warriors.feedback.feedback.model.Avis;
import com.warriors.feedback.feedback.repository.AvisRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AvisService {

    private final AvisRepository avisRepository;
    private final EvenementsClient evenementsClient;
    private final RabbitTemplate rabbitTemplate;

    public AvisService(AvisRepository avisRepository, EvenementsClient evenementsClient, RabbitTemplate rabbitTemplate) {
        this.avisRepository = avisRepository;
        this.evenementsClient = evenementsClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Avis deposerAvis(Long evenementId, Avis avis) {
        // shortcut: no real presence check yet (Check-in service not wired), only duplicate check
        if (avisRepository.existsByEvenementIdAndParticipantId(evenementId, avis.getParticipantId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Avis deja depose pour cet evenement");
        }
        avis.setEvenementId(evenementId);
        Avis saved = avisRepository.save(avis);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_AVIS,
                new AvisDeposeEvent(saved.getId(), evenementId, saved.getParticipantId(), saved.getNote(), saved.getDate())
        );

        return saved;
    }

    public List<Avis> getAvisForEvenement(Long evenementId) {
        return avisRepository.findByEvenementId(evenementId);
    }

    public Avis updateAvis(Long avisId, Avis updates) {
        Avis existing = avisRepository.findById(avisId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avis introuvable"));
        existing.setNote(updates.getNote());
        existing.setCommentaire(updates.getCommentaire());
        return avisRepository.save(existing);
    }

    public void deleteAvis(Long avisId) {
        if (!avisRepository.existsById(avisId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Avis introuvable");
        avisRepository.deleteById(avisId);
    }
}
