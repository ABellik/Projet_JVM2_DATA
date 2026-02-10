package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Jeu;
import jakarta.persistence.EntityManager;
import java.util.List;

public class JeuRepository {
    private final EntityManager em;

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
        return em.createQuery("select j from Jeu j JOIN FETCH j.idEditeur", Jeu.class).getResultList();
    }

    public List<Jeu> findByIdEditeur(Long idEditeur) {
        return em.createQuery("select j from Jeu j where j.idEditeur=:idEditeur", Jeu.class).setParameter("idEditeur", idEditeur).getResultList();
    }

    public List<Long> findByPseudo(String pseudo){
        return em.createQuery(
                "select b.jeu.id from Bibliothèque b where b.utilisateur.pseudo = :pseudo",
                Long.class).setParameter("pseudo", pseudo).getResultList();
    }

    public void delete(Jeu jeu) {
        em.remove(jeu);
    }
}
