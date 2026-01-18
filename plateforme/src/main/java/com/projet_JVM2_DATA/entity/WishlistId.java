package com.projet_JVM2_DATA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WishlistId implements Serializable {
    private static final long serialVersionUID = 8114901887322069340L;
    @Column(name = "\"idUtilisateur\"", nullable = false)
    private Long idUtilisateur;

    @Column(name = "\"idPlateforme\"", nullable = false)
    private Long idPlateforme;

    @Column(name = "\"idJeu\"", nullable = false)
    private Long idJeu;

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WishlistId entity = (WishlistId) o;
        return Objects.equals(this.idPlateforme, entity.idPlateforme) &&
                Objects.equals(this.idJeu, entity.idJeu) &&
                Objects.equals(this.idUtilisateur, entity.idUtilisateur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlateforme, idJeu, idUtilisateur);
    }

}