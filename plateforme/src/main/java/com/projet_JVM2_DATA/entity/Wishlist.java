package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "\"Wishlist\"")
public class Wishlist {
    @EmbeddedId
    private WishlistId id;

    @MapsId("idUtilisateur")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUtilisateur\"", nullable = false)
    private Utilisateur idUtilisateur;

    @Column(name = "date_ajout", nullable = false)
    private LocalDate dateAjout;

    public WishlistId getId() {
        return id;
    }

    public void setId(WishlistId id) {
        this.id = id;
    }

    public Utilisateur getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Utilisateur idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }

}