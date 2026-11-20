package com.warriors.fidelite.repository;

import com.warriors.fidelite.model.Mouvement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MouvementRepository extends MongoRepository<Mouvement, String> {

    List<Mouvement> findByParticipantIdOrderByDateDesc(String participantId);
}
