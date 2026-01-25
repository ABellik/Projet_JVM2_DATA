package com.projet_JVM2_DATA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ModificationId implements Serializable {
    private static final long serialVersionUID = 1558107064761966958L;
    @Column(name = "idpatch", nullable = false)
    private Long idpatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "modif", nullable = false)
    private TypeModif modif;

    public ModificationId(){}
    public ModificationId(Long idpatch, TypeModif modif) {
        this.idpatch = idpatch;
        this.modif = modif;
    }

    public Long getIdpatch() {
        return idpatch;
    }

    public void setIdpatch(Long idpatch) {
        this.idpatch = idpatch;
    }

    public TypeModif getModif() {
        return modif;
    }

    public void setModif(TypeModif modif) {
        this.modif = modif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ModificationId entity = (ModificationId) o;
        return Objects.equals(this.idpatch, entity.idpatch) &&
                Objects.equals(this.modif, entity.modif);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpatch, modif);
    }

}