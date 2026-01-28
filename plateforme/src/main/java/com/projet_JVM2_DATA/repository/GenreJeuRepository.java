package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Genrejeu;
import com.projet_JVM2_DATA.entity.Jeu;
import jakarta.persistence.EntityManager;

import java.util.List;

public class GenreJeuRepository {
    private final EntityManager em;
    public GenreJeuRepository(EntityManager em) {
        this.em = em;
    }
    public void save(Genrejeu genrejeu){
        em.persist(genrejeu);
    }

    public Genrejeu findById(long id){
        return em.find(Genrejeu.class, id);
    }

    public List<Genrejeu> findAll(){
        return em.createQuery("SELECT g from Genrejeu g",Genrejeu.class).getResultList();
    }

    public List<Genrejeu> findByJeu(Jeu jeu){
        return em.createQuery("select g from  Genrejeu g where g.idjeu=:jeu", Genrejeu.class)
                .setParameter("jeu", jeu)
                .getResultList();
    }

    public void delete(Genrejeu genrejeu){
        em.remove(genrejeu);
    }
}
