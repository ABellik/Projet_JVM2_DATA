package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Jeu\"")
public class Jeu {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idEditeur\"", nullable = false)
    private Editeur idEditeur;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "version_actuelle", nullable = false)
    private String versionActuelle;

    @Column(name = "\"genre Genre\"", nullable = false)
    private String genreGenre;

    @Column(name = "prix_editeur", nullable = false)
    private Long prixEditeur;

    @Column(name = "prix_actuel", nullable = false)
    private Long prixActuel;

    @Column(name = "\"type Type\"", nullable = false)
    private String typeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idJeuParent (nullable)\"")
    private Jeu idJeuParentNullable;

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

    public String getGenreGenre() {
        return genreGenre;
    }

    public void setGenreGenre(String genreGenre) {
        this.genreGenre = genreGenre;
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

    public String getTypeType() {
        return typeType;
    }

    public void setTypeType(String typeType) {
        this.typeType = typeType;
    }

    public Jeu getIdJeuParentNullable() {
        return idJeuParentNullable;
    }

    public void setIdJeuParentNullable(Jeu idJeuParentNullable) {
        this.idJeuParentNullable = idJeuParentNullable;
    }

}