package com.universite.evenements.entity;

import com.universite.evenements.enums.NiveauPublic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "details_evenement")
@Getter
@Setter
@NoArgsConstructor
public class DetailEvenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String langue;

    @Enumerated(EnumType.STRING)
    private NiveauPublic niveau;

    private Integer nbStands;

    @ElementCollection
    @CollectionTable(name = "evenement_materiels_demandes", joinColumns = @JoinColumn(name = "detail_id"))
    @Column(name = "materiel")
    private List<String> materielsDemandes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "evenement_hashtags", joinColumns = @JoinColumn(name = "detail_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();
}
