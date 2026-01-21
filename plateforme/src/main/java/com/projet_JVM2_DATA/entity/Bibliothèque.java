package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "\"Bibliothèque\"")
public class Bibliothèque {
    @Id
    @Column(name = "\"idUtilisateur\"", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUtilisateur\"", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "\"idPlateforme\"", nullable = false)
    private Long idPlateforme;

    @Column(name = "\"idJeu\"", nullable = false)
    private Long idJeu;

    @Column(name = "\"commentaire_joueur\"")
    private String commentaireJoueurNullable;

    @Column(name = "\"note_joueur\"")
    private Long noteJoueurNullable;

    @Column(name = "temps_jeu", nullable = false)
    private Long tempsJeu;

    @Column(name = "date_achat", nullable = false)
    private LocalDate dateAchat;

    @Column(name = "prix_achat", nullable = false)
    private Long prixAchat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Long getIdPlateforme() {
        return idPlateforme;
    }

    public void setIdPlateforme(Long idPlateforme) {
        this.idPlateforme = idPlateforme;
    }

    public Long getIdJeu() {
        return idJeu;
    }

    public void setIdJeu(Long idJeu) {
        this.idJeu = idJeu;
    }

    public String getCommentaireJoueurNullable() {
        return commentaireJoueurNullable;
    }

    public void setCommentaireJoueurNullable(String commentaireJoueurNullable) {
        this.commentaireJoueurNullable = commentaireJoueurNullable;
    }

    public Long getNoteJoueurNullable() {
        return noteJoueurNullable;
    }

    public void setNoteJoueurNullable(Long noteJoueurNullable) {
        this.noteJoueurNullable = noteJoueurNullable;
    }

    public Long getTempsJeu() {
        return tempsJeu;
    }

    public void setTempsJeu(Long tempsJeu) {
        this.tempsJeu = tempsJeu;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public Long getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(Long prixAchat) {
        this.prixAchat = prixAchat;
    }

}