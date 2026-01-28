package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Editeur\"")
public class Editeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "mdp", nullable = false)
    private String mdp;

    public Editeur() {}
    public Editeur(String type, String nom, String mdp) {
        this.type = type;
        this.nom = nom;
        this.mdp = mdp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}