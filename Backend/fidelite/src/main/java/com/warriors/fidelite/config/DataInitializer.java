package com.warriors.fidelite.config;

import com.warriors.fidelite.model.ActionFidelite;
import com.warriors.fidelite.model.CategorieRecompense;
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

    /** Catalogue de depart (si la collection est vide) : sweats, goodies, bons de certification. */
    @Bean
    CommandLineRunner initRecompenses(RecompenseRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.saveAll(List.of(
                    Recompense.builder().nom("Goodies exclusif : stylo de l'école")
                            .categorie(CategorieRecompense.GOODIES).coutPoints(80).stock(100).build(),
                    Recompense.builder().nom("Sweat officiel de l'école")
                            .categorie(CategorieRecompense.SWEAT).coutPoints(300).stock(20).build(),
                    Recompense.builder().nom("Bon de certification technologique")
                            .categorie(CategorieRecompense.CERTIFICATION).partenaire("Entreprise partenaire")
                            .coutPoints(500).stock(10).build()));
        };
    }

    /** Profil dev uniquement : quelques participants avec des points, pour tester le classement. */
    @Bean
    @Profile("dev")
    CommandLineRunner initDemo(FidelityService service) {
        return args -> {
            // user-123 : 2 evenements (participation + avis) = 140 points, BRONZE
            for (long e = 1; e <= 2; e++) {
                service.gagnerPoints("user-123", e, ActionFidelite.PARTICIPATION);
                service.gagnerPoints("user-123", e, ActionFidelite.AVIS);
            }
            // etudiant-ali : 4 participations + 2 avis = 240 points, SILVER
            for (long e = 1; e <= 4; e++) {
                service.gagnerPoints("etudiant-ali", e, ActionFidelite.PARTICIPATION);
            }
            for (long e = 1; e <= 2; e++) {
                service.gagnerPoints("etudiant-ali", e, ActionFidelite.AVIS);
            }
            // etudiant-sarra : 3 participations + 3 avis + 1 victoire hackathon = 510 points, GOLD
            for (long e = 1; e <= 3; e++) {
                service.gagnerPoints("etudiant-sarra", e, ActionFidelite.PARTICIPATION);
                service.gagnerPoints("etudiant-sarra", e, ActionFidelite.AVIS);
            }
            service.gagnerPoints("etudiant-sarra", 10L, ActionFidelite.VICTOIRE_HACKATHON);
        };
    }
}
