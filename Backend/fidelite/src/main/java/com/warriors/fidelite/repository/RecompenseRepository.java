package com.warriors.fidelite.repository;

import com.warriors.fidelite.model.CategorieRecompense;
import com.warriors.fidelite.model.Recompense;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecompenseRepository extends MongoRepository<Recompense, String> {

    List<Recompense> findByCategorie(CategorieRecompense categorie);
}
