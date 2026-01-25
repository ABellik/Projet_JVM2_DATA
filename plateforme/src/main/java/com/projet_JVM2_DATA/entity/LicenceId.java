package com.projet_JVM2_DATA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LicenceId implements Serializable {
    private static final long serialVersionUID = 8003042400361667544L;
    @Column(name = "\"idPlateforme\"", nullable = false)
    private Long idPlateforme;

    @Column(name = "\"idJeu\"", nullable = false)
    private Long idJeu;

    public LicenceId(){}

    public LicenceId(Long idPlateforme, Long idJeu) {
        this.idPlateforme = idPlateforme;
        this.idJeu = idJeu;
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
        LicenceId entity = (LicenceId) o;
        return Objects.equals(this.idPlateforme, entity.idPlateforme) &&
                Objects.equals(this.idJeu, entity.idJeu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlateforme, idJeu);
    }

}