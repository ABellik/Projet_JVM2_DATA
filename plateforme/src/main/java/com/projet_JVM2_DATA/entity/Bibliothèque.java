package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "\"Bibliothèque\"")
public class Bibliothèque {
    @EmbeddedId
    private BibliothèqueId id;

    @MapsId("idUtilisateur")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUtilisateur\"", nullable = false)
    private Utilisateur idUtilisateur;

    @MapsId("idPlateforme")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idPlateforme\"", nullable = false)
    private Plateforme idPlateforme;

    @MapsId("idJeu")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idJeu\"", nullable = false)
    private Jeu idJeu;

    @Column(name = "commentaire_joueur")
    private String commentaireJoueur;

    @Column(name = "note_joueur")
    private Long noteJoueur;

    @Column(name = "temps_jeu", nullable = false)
    private Long tempsJeu;

    @Column(name = "date_achat", nullable = false)
    private LocalDate dateAchat;

    @Column(name = "prix_achat", nullable = false)
    private Long prixAchat;

    //TODO Constructeur

    public BibliothèqueId getId() {
        return id;
    }

    public void setId(BibliothèqueId id) {
        this.id = id;
    }

    public Utilisateur getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Utilisateur idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
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

    public String getCommentaireJoueur() {
        return commentaireJoueur;
    }

    public void setCommentaireJoueur(String commentaireJoueur) {
        this.commentaireJoueur = commentaireJoueur;
    }

    public Long getNoteJoueur() {
        return noteJoueur;
    }

    public void setNoteJoueur(Long noteJoueur) {
        this.noteJoueur = noteJoueur;
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