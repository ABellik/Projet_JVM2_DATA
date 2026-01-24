package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Jeu;
import jakarta.persistence.EntityManager;
import java.util.List;

public class JeuRepository {
    private EntityManager em;

    public JeuRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Jeu jeu) {
        em.persist(jeu);
    }

    public Jeu findById(Long id) {
        return em.find(Jeu.class, id);
    }

    public List<Jeu> findAll() {
        return em.createQuery("select j from Jeu j", Jeu.class).getResultList();
    }

    public List<Jeu> findByIdEditeur(Long idEditeur) {
        return em.createQuery("select j from Jeu j where j.idEditeur=:idEditeur", Jeu.class).setParameter("idEditeur", idEditeur).getResultList();
    }

    public void delete(Jeu jeu) {
        em.remove(jeu);
    }
}
