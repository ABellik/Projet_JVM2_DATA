package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Licence\"")
public class Licence {
    @EmbeddedId
    private LicenceId id;

    @MapsId("idPlateforme")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idPlateforme\"", nullable = false)
    private Plateforme idPlateforme;

    @MapsId("idJeu")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idJeu\"", nullable = false)
    private Jeu idJeu;

    //TODO Constructeur

    public LicenceId getId() {
        return id;
    }

    public void setId(LicenceId id) {
        this.id = id;
    }

    public Plateforme getIdPlateforme() {
        return idPlateforme;
    }

    public void setIdPlateforme(Plateforme idPlateforme) {
        this.idPlateforme = idPlateforme;
    }

    public Jeu getIdJeu() {
        return idJeu;
    }

    public void setIdJeu(Jeu idJeu) {
        this.idJeu = idJeu;
    }

}