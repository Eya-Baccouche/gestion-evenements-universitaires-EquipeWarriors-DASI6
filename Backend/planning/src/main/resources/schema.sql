-- ============================================================
-- Microservice Planning des Salles — Schéma PostgreSQL
-- Projet AWD 2026-2027 — Gestion des événements universitaires
-- ============================================================

DROP TABLE IF EXISTS reservation_salle CASCADE;
DROP TABLE IF EXISTS table_salle CASCADE;
DROP TABLE IF EXISTS salle CASCADE;
DROP TABLE IF EXISTS batiment CASCADE;

-- ============================================================
-- Table : batiment
-- ============================================================
CREATE TABLE batiment (
    id          BIGSERIAL       PRIMARY KEY,
    nom         VARCHAR(100)    NOT NULL,
    adresse     VARCHAR(255)    NOT NULL,
    nb_etages   INT             NOT NULL CHECK (nb_etages > 0),

    CONSTRAINT uk_batiment_nom UNIQUE (nom)
);

-- ============================================================
-- Table : salle
-- ============================================================
CREATE TABLE salle (
    id           BIGSERIAL       PRIMARY KEY,
    nom          VARCHAR(100)    NOT NULL,
    type_salle   VARCHAR(30)     NOT NULL,
    capacite     INT            NOT NULL CHECK (capacite > 0),
    etage        INT            NOT NULL CHECK (etage >= 0),
    batiment_id  BIGINT         NOT NULL,
    equipements  VARCHAR(500),
    statut       VARCHAR(20)    NOT NULL DEFAULT 'DISPONIBLE',
    description  TEXT,

    CONSTRAINT uk_salle_nom_batiment UNIQUE (nom, batiment_id),
    CONSTRAINT chk_salle_type   CHECK (type_salle IN ('AMPHI', 'SALLE_COURS', 'SALLE_TP', 'LABORATOIRE', 'SALLE_REUNION')),
    CONSTRAINT chk_salle_statut CHECK (statut       IN ('DISPONIBLE', 'EN_MAINTENANCE', 'FERME')),

    CONSTRAINT fk_salle_batiment FOREIGN KEY (batiment_id)
        REFERENCES batiment (id) ON DELETE RESTRICT
);

-- ============================================================
-- Table : table_salle (tables individuelles dans une salle)
-- ============================================================
CREATE TABLE table_salle (
    id           BIGSERIAL       PRIMARY KEY,
    numero       VARCHAR(20)     NOT NULL,
    capacite     INT            NOT NULL CHECK (capacite > 0),
    salle_id     BIGINT         NOT NULL,
    statut       VARCHAR(20)    NOT NULL DEFAULT 'DISPONIBLE',
    description  TEXT,

    CONSTRAINT uk_table_salle_numero UNIQUE (numero, salle_id),
    CONSTRAINT chk_table_statut CHECK (statut IN ('DISPONIBLE', 'EN_MAINTENANCE', 'FERME')),

    CONSTRAINT fk_table_salle FOREIGN KEY (salle_id)
        REFERENCES salle (id) ON DELETE CASCADE
);

-- ============================================================
-- Table : reservation_salle
-- ============================================================
CREATE TABLE reservation_salle (
    id                BIGSERIAL     PRIMARY KEY,
    salle_id          BIGINT        NOT NULL,
    table_id          BIGINT,
    titre             VARCHAR(200)  NOT NULL,
    description       TEXT,
    date_reservation  DATE         NOT NULL,
    heure_debut       TIME         NOT NULL,
    heure_fin         TIME         NOT NULL,
    statut            VARCHAR(20)  NOT NULL DEFAULT 'EN_ATTENTE',
    demande_par       VARCHAR(150) NOT NULL,
    role_demandeur    VARCHAR(20)  NOT NULL,
    evenement_id      BIGINT,
    nombre_personnes  INT          NOT NULL CHECK (nombre_personnes > 0),
    motif_refus       TEXT,
    date_creation     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,

    CONSTRAINT chk_res_heure_fin   CHECK (heure_fin > heure_debut),
    CONSTRAINT chk_res_statut     CHECK (statut         IN ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'REFUSEE')),
    CONSTRAINT chk_res_role       CHECK (role_demandeur  IN ('ADMIN', 'ORGANISATEUR')),
    CONSTRAINT chk_res_date_future CHECK (date_reservation >= CURRENT_DATE),

    CONSTRAINT fk_reservation_salle FOREIGN KEY (salle_id)
        REFERENCES salle (id) ON DELETE RESTRICT,

    CONSTRAINT fk_reservation_table FOREIGN KEY (table_id)
        REFERENCES table_salle (id) ON DELETE SET NULL
);

-- ============================================================
-- Contraintes anti-chevauchement (R-S1)
-- Niveau salle : deux réservations de salle entière ne se chevauchent pas
-- Niveau table : deux réservations de la même table ne se chevauchent pas
-- ============================================================
CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE reservation_salle
    DROP CONSTRAINT IF EXISTS excl_chevauchement_salle;

ALTER TABLE reservation_salle
    DROP CONSTRAINT IF EXISTS excl_chevauchement_table;

-- Chevauchement au niveau salle (table_id IS NULL)
ALTER TABLE reservation_salle
    ADD CONSTRAINT excl_chevauchement_salle
    EXCLUDE USING gist (
        salle_id          WITH =,
        date_reservation  WITH =,
        tstzrange(
            (date_reservation::timestamp + heure_debut::interval),
            (date_reservation::timestamp + heure_fin::interval)
        ) WITH &&
    )
    WHERE (statut IN ('EN_ATTENTE', 'CONFIRMEE') AND table_id IS NULL);

-- Chevauchement au niveau table (table_id IS NOT NULL)
ALTER TABLE reservation_salle
    ADD CONSTRAINT excl_chevauchement_table
    EXCLUDE USING gist (
        table_id          WITH =,
        date_reservation  WITH =,
        tstzrange(
            (date_reservation::timestamp + heure_debut::interval),
            (date_reservation::timestamp + heure_fin::interval)
        ) WITH &&
    )
    WHERE (statut IN ('EN_ATTENTE', 'CONFIRMEE') AND table_id IS NOT NULL);

-- ============================================================
-- Index
-- ============================================================
CREATE INDEX idx_salle_batiment        ON salle (batiment_id);
CREATE INDEX idx_salle_type_statut     ON salle (type_salle, statut);
CREATE INDEX idx_table_salle_salle      ON table_salle (salle_id);
CREATE INDEX idx_table_salle_statut     ON table_salle (statut);
CREATE INDEX idx_reservation_salle      ON reservation_salle (salle_id);
CREATE INDEX idx_reservation_table      ON reservation_salle (table_id);
CREATE INDEX idx_reservation_date       ON reservation_salle (date_reservation);
CREATE INDEX idx_reservation_statut     ON reservation_salle (statut);
CREATE INDEX idx_reservation_demandeur  ON reservation_salle (demande_par);
