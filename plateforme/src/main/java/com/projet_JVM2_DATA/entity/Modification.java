package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "modification")
public class Modification {
    @EmbeddedId
    private ModificationId id;

    @MapsId("idpatch")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpatch", nullable = false)
    private Patch idpatch;

    @Column(name = "modif", nullable = false, insertable = false, updatable = false)
    private TypeModif modif;

    //TODO : constructeur

    public ModificationId getId() {
        return id;
    }

    public void setId(ModificationId id) {
        this.id = id;
    }

    public Patch getIdpatch() {
        return idpatch;
    }

    public void setIdpatch(Patch idpatch) {
        this.idpatch = idpatch;
    }

    public TypeModif getModif() {
        return modif;
    }

    public void setModif(TypeModif modif) {
        this.modif = modif;
    }

}