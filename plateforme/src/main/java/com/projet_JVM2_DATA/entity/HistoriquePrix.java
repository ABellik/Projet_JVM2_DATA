package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Historique_Prix\"")
public class HistoriquePrix {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idJeu\"", nullable = false)
    private Jeu idJeu;

    @Column(name = "ancien_prix", nullable = false)
    private Long ancienPrix;

    @Column(name = "nouveau_prix", nullable = false)
    private Long nouveauPrix;

    @Column(name = "commentaire", nullable = false)
    private String commentaire;

    //TODO Constructeur

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jeu getIdJeu() {
        return idJeu;
    }

    public void setIdJeu(Jeu idJeu) {
        this.idJeu = idJeu;
    }

    public Long getAncienPrix() {
        return ancienPrix;
    }

    public void setAncienPrix(Long ancienPrix) {
        this.ancienPrix = ancienPrix;
    }

    public Long getNouveauPrix() {
        return nouveauPrix;
    }

    public void setNouveauPrix(Long nouveauPrix) {
        this.nouveauPrix = nouveauPrix;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}