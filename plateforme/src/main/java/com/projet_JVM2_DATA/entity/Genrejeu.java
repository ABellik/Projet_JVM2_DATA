package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "genrejeu")
public class Genrejeu {
    @EmbeddedId
    private GenrejeuId id;

    @MapsId("idjeu")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idjeu", nullable = false)
    private Jeu idjeu;

    @Column(name="genre", nullable = false)
    private TypeGenre genre;

    //TODO : constructeur

    public GenrejeuId getId() {
        return id;
    }

    public void setId(GenrejeuId id) {
        this.id = id;
    }

    public Jeu getIdjeu() {
        return idjeu;
    }

    public void setIdjeu(Jeu idjeu) {
        this.idjeu = idjeu;
    }

    public TypeGenre getGenre() {
        return genre;
    }

    public void setGenre(TypeGenre genre) {
        this.genre = genre;
    }

}