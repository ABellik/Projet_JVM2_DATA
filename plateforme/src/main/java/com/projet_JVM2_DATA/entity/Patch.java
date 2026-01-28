package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Patch\"")
public class Patch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idJeu\"", nullable = false)
    private Jeu idJeu;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "commentaire_editeur", nullable = false)
    private String commentaireEditeur;

    public Patch() {}
    public Patch(Jeu jeu, String version, String commentaireEditeur) {
        this.idJeu = jeu;
        this.version = version;
        this.commentaireEditeur = commentaireEditeur;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommentaireEditeur() {
        return commentaireEditeur;
    }

    public void setCommentaireEditeur(String commentaireEditeur) {
        this.commentaireEditeur = commentaireEditeur;
    }

}