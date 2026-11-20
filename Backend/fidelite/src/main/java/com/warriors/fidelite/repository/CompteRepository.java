package com.warriors.fidelite.repository;

import com.warriors.fidelite.model.Compte;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompteRepository extends MongoRepository<Compte, String> {
}
