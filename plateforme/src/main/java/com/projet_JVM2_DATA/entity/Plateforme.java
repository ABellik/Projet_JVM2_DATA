package com.projet_JVM2_DATA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Plateforme\"")
public class Plateforme {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

}