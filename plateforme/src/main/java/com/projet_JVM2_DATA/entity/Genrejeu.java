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

    //deuxième option, supprimer cette partie car déjà présente dans id
    @Column(name="genre", nullable = false, insertable = false, updatable = false)
    private String genre;

    public Genrejeu() {}
    public Genrejeu(Jeu jeu, String genre) {
        this.id = new GenrejeuId(jeu.getId(), genre);
        this.idjeu = jeu;
        this.genre = genre;
    }

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}