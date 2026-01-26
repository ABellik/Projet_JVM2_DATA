package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Jeu\"")
public class Jeu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idEditeur\"", nullable = false)
    private Editeur idEditeur;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "version_actuelle", nullable = false)
    private String versionActuelle;

    @Column(name = "prix_editeur", nullable = false)
    private Long prixEditeur;

    @Column(name = "prix_actuel", nullable = false)
    private Long prixActuel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idjeuparent")
    private Jeu idjeuparent;

    @Enumerated(EnumType.STRING)
    /*Colonne disant si c'est un jeu de base (BASE) ou un DLC (DLC)*/
    @Column(name = "type",  nullable = false)
    private TypeJeu type;

    public Jeu() {}
    public Jeu(Editeur editeur, String nom, String versionActuelle, Long prixEditeur, Jeu idJeuParent, TypeJeu type) {
        this.idEditeur = editeur;
        this.nom = nom;
        this.versionActuelle = versionActuelle;
        this.prixEditeur = prixEditeur;
        this.prixActuel = prixEditeur;
        this.idjeuparent = idJeuParent;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Editeur getIdEditeur() {
        return idEditeur;
    }

    public void setIdEditeur(Editeur idEditeur) {
        this.idEditeur = idEditeur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getVersionActuelle() {
        return versionActuelle;
    }

    public void setVersionActuelle(String versionActuelle) {
        this.versionActuelle = versionActuelle;
    }

    public Long getPrixEditeur() {
        return prixEditeur;
    }

    public void setPrixEditeur(Long prixEditeur) {
        this.prixEditeur = prixEditeur;
    }

    public Long getPrixActuel() {
        return prixActuel;
    }

    public void setPrixActuel(Long prixActuel) {
        this.prixActuel = prixActuel;
    }

    public Jeu getIdjeuparent() {
        return idjeuparent;
    }

    public void setIdjeuparent(Jeu idjeuparent) {
        this.idjeuparent = idjeuparent;
    }

}