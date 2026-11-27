package com.warriors.fidelite.service;

import com.warriors.fidelite.dto.RecompenseRequest;
import com.warriors.fidelite.exception.NotFoundException;
import com.warriors.fidelite.model.CategorieRecompense;
import com.warriors.fidelite.model.Recompense;
import com.warriors.fidelite.repository.RecompenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecompenseService {

    private final RecompenseRepository repository;

    /** Catalogue complet, ou filtre par categorie (SWEAT, GOODIES, CERTIFICATION). */
    public List<Recompense> lister(CategorieRecompense categorie) {
        return categorie == null ? repository.findAll() : repository.findByCategorie(categorie);
    }

    public Recompense creer(RecompenseRequest request) {
        return repository.save(Recompense.builder()
                .nom(request.nom())
                .categorie(request.categorie())
                .partenaire(request.partenaire())
                .coutPoints(request.coutPoints())
                .stock(request.stock())
                .build());
    }

    public Recompense modifier(String id, RecompenseRequest request) {
        Recompense r = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recompense introuvable : " + id));
        r.setNom(request.nom());
        r.setCategorie(request.categorie());
        r.setPartenaire(request.partenaire());
        r.setCoutPoints(request.coutPoints());
        r.setStock(request.stock());
        return repository.save(r);
    }
}
