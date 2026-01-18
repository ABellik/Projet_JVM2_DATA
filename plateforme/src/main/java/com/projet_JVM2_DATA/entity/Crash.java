package com.projet_JVM2_DATA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Crash\"")
public class Crash {
    @Id
    @Column(name = "\"idSession\"", nullable = false)
    private Long id;

    @Column(name = "code_erreur", nullable = false)
    private Long codeErreur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodeErreur() {
        return codeErreur;
    }

    public void setCodeErreur(Long codeErreur) {
        this.codeErreur = codeErreur;
    }

}