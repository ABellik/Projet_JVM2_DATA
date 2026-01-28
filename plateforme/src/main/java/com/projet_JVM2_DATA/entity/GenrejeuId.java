package com.projet_JVM2_DATA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GenrejeuId implements Serializable {
    private static final long serialVersionUID = 5563536100977327603L;
    @Column(name = "idjeu", nullable = false)
    private Long idjeu;

    @Column(name = "genre", nullable = false)
    private String genre;

    public GenrejeuId() {}
    public GenrejeuId(Long idjeu, String genre) {
        this.idjeu = idjeu;
        this.genre = genre;
    }

    public Long getIdjeu() {
        return idjeu;
    }

    public void setIdjeu(Long idjeu) {
        this.idjeu = idjeu;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GenrejeuId entity = (GenrejeuId) o;
        return Objects.equals(this.idjeu, entity.idjeu) &&
                Objects.equals(this.genre, entity.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idjeu, genre);
    }

}