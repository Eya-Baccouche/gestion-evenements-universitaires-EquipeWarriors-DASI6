package com.warriors.fidelite.listener;

import com.warriors.fidelite.config.RabbitConfig;
import com.warriors.fidelite.model.ActionFidelite;
import com.warriors.fidelite.service.FidelityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvenementListener {

    private final FidelityService service;

    /** Le payload est lu comme une Map : aucune dependance de classes avec les autres equipes. */
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onEvent(Map<String, Object> payload,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        log.info("Evenement recu : {} {}", routingKey, payload);

        ActionFidelite action = switch (routingKey) {
            case "inscription.confirmee" -> ActionFidelite.INSCRIPTION;
            case "presence.enregistree" -> ActionFidelite.PRESENCE;
            case "avis.envoye" -> ActionFidelite.AVIS;
            default -> null;
        };
        if (action == null) {
            log.warn("Cle de routage ignoree : {}", routingKey);
            return;
        }

        try {
            String participantId = texte(payload, "participantId");
            Long evenementId = Long.valueOf(texte(payload, "evenementId"));
            service.gagnerPoints(participantId, evenementId, action);
        } catch (RuntimeException e) {
            // On ne relance pas l'exception : un message invalide ne doit pas boucler dans la file.
            log.error("Message ignore ({}) : {}", routingKey, e.getMessage());
        }
    }

    private String texte(Map<String, Object> payload, String cle) {
        Object valeur = payload.get(cle);
        if (valeur == null || String.valueOf(valeur).isBlank()) {
            throw new IllegalArgumentException("Champ manquant : " + cle);
        }
        return String.valueOf(valeur);
    }
}
