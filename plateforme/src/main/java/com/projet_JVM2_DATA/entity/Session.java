package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Session\"")
public class Session {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Crash crash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUtilisateur\"", nullable = false)
    private Bibliothèque idUtilisateur;

    @Column(name = "\"idPlateforme\"", nullable = false)
    private Long idPlateforme;

    @Column(name = "\"idJeu (nullable)\"")
    private Long idJeuNullable;

    @Column(name = "\"idDLC (nullable)\"")
    private Long idDLCNullable;

    @Column(name = "\"durée\"", nullable = false)
    private Long durée;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Crash getCrash() {
        return crash;
    }

    public void setCrash(Crash crash) {
        this.crash = crash;
    }

    public Bibliothèque getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Bibliothèque idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Long getIdPlateforme() {
        return idPlateforme;
    }

    public void setIdPlateforme(Long idPlateforme) {
        this.idPlateforme = idPlateforme;
    }

    public Long getIdJeuNullable() {
        return idJeuNullable;
    }

    public void setIdJeuNullable(Long idJeuNullable) {
        this.idJeuNullable = idJeuNullable;
    }

    public Long getIdDLCNullable() {
        return idDLCNullable;
    }

    public void setIdDLCNullable(Long idDLCNullable) {
        this.idDLCNullable = idDLCNullable;
    }

    public Long getDurée() {
        return durée;
    }

    public void setDurée(Long durée) {
        this.durée = durée;
    }

}