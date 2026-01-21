package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Session\"")
public class Session {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUtilisateur\"", nullable = false)
    private Utilisateur idUtilisateur;

    @Column(name = "\"idPlateforme\"", nullable = false)
    private Long idPlateforme;

    @Column(name = "idjeu")
    private Long idjeu;

    @Column(name = "iddlc")
    private Long iddlc;

    @Column(name = "\"durée\"", nullable = false)
    private Long durée;

    @Column(name = "type", nullable = false)
    private TypeSession type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Utilisateur idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Long getIdPlateforme() {
        return idPlateforme;
    }

    public void setIdPlateforme(Long idPlateforme) {
        this.idPlateforme = idPlateforme;
    }

    public Long getIdjeu() {
        return idjeu;
    }

    public void setIdjeu(Long idjeu) {
        this.idjeu = idjeu;
    }

    public Long getIddlc() {
        return iddlc;
    }

    public void setIddlc(Long iddlc) {
        this.iddlc = iddlc;
    }

    public Long getDurée() {
        return durée;
    }

    public void setDurée(Long durée) {
        this.durée = durée;
    }

}