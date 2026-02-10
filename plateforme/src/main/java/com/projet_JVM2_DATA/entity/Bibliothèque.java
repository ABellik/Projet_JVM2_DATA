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
    private Utilisateur utilisateur;

    @MapsId("idPlateforme")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idPlateforme\"", nullable = false)
    private Plateforme plateforme;

    @MapsId("idJeu")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idJeu\"", nullable = false)
    private Jeu jeu;

    @Column(name = "commentaire_joueur")
    private String commentaireJoueur;

    @Column(name = "note_joueur")
    private Long noteJoueur;

    @Column(name = "temps_jeu", nullable = false)
    private Long tempsJeu;

    @Column(name = "date_achat", nullable = false)
    private LocalDate dateAchat;

    @Column(name = "prix_achat", nullable = false)
    private Double prixAchat;

    public Bibliothèque(){}
    public Bibliothèque(Utilisateur utilisateur, Plateforme plateforme, Jeu jeu, LocalDate dateAchat, double prixAchat) {
        this.id = new BibliothèqueId(utilisateur.getId(), plateforme.getId(), jeu.getId());

        this.utilisateur = utilisateur;
        this.plateforme = plateforme;
        this.jeu = jeu;

        this.prixAchat = prixAchat;
        this.noteJoueur = null;
        this.tempsJeu = 0L;
        this.dateAchat = dateAchat;
        this.commentaireJoueur = null;
    }

    public BibliothèqueId getId() {
        return id;
    }

    public void setId(BibliothèqueId id) {
        this.id = id;
    }

    public Utilisateur getIdUtilisateur() {
        return utilisateur;
    }

    public void setIdUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Plateforme getIdPlateforme() {
        return plateforme;
    }

    public void setIdPlateforme(Plateforme plateforme) {
        this.plateforme = plateforme;
    }

    public Jeu getIdJeu() {
        return jeu;
    }

    public void setIdJeu(Jeu jeu) {
        this.jeu = jeu;
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

    public Double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(Double prixAchat) {
        this.prixAchat = prixAchat;
    }
}