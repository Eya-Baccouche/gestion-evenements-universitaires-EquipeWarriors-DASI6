package com.warriors.fidelite.config;

import com.warriors.fidelite.model.ActionFidelite;
import com.warriors.fidelite.model.Recompense;
import com.warriors.fidelite.repository.RecompenseRepository;
import com.warriors.fidelite.service.FidelityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class DataInitializer {

    /** Catalogue de depart (si la collection est vide). */
    @Bean
    CommandLineRunner initRecompenses(RecompenseRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.saveAll(List.of(
                    Recompense.builder().nom("Stylo universitaire").coutPoints(20).stock(100).build(),
                    Recompense.builder().nom("Café offert").coutPoints(50).stock(50).build(),
                    Recompense.builder().nom("T-shirt Warriors").coutPoints(150).stock(20).build()));
        };
    }

    /** Profil dev uniquement : quelques participants avec des points, pour tester le classement. */
    @Bean
    @Profile("dev")
    CommandLineRunner initDemo(FidelityService service) {
        return args -> {
            // user-123 : 2 evenements complets (inscription + presence + avis) = 70 points, BRONZE
            for (long e = 1; e <= 2; e++) {
                service.gagnerPoints("user-123", e, ActionFidelite.INSCRIPTION);
                service.gagnerPoints("user-123", e, ActionFidelite.PRESENCE);
                service.gagnerPoints("user-123", e, ActionFidelite.AVIS);
            }
            // etudiant-ali : 5 evenements (inscription + presence) = 125 points, ARGENT
            for (long e = 1; e <= 5; e++) {
                service.gagnerPoints("etudiant-ali", e, ActionFidelite.INSCRIPTION);
                service.gagnerPoints("etudiant-ali", e, ActionFidelite.PRESENCE);
            }
            // etudiant-sarra : 14 evenements (inscription + presence) = 350 points, OR
            for (long e = 1; e <= 14; e++) {
                service.gagnerPoints("etudiant-sarra", e, ActionFidelite.INSCRIPTION);
                service.gagnerPoints("etudiant-sarra", e, ActionFidelite.PRESENCE);
            }
        };
    }
}
