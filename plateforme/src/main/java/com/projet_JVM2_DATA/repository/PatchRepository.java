package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.HistoriquePrix;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Patch;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PatchRepository {
    private final EntityManager em;

    public PatchRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Patch patch) {
        em.persist(patch);
    }

    public Patch findById(Long id) {
        return em.find(Patch.class, id);
    }

    public List<Patch> findAll() {
        return em.createQuery("select p from Patch p", Patch.class).getResultList();
    }

    public List<Patch> findByJeu(Jeu jeu) {
        return em.createQuery("select p from  Patch p where p.idJeu=:jeu", Patch.class)
                .setParameter("jeu", jeu)
                .getResultList();
    }

    public void delete(Patch patch) {
        em.remove(patch);
    }
}
