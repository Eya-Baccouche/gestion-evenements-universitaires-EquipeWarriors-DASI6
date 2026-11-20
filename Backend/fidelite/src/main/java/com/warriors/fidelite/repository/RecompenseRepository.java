package com.warriors.fidelite.repository;

import com.warriors.fidelite.model.Recompense;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecompenseRepository extends MongoRepository<Recompense, String> {
}
