package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Patch;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PatchRepository {
    private EntityManager em;

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

    public void delete(Patch patch) {
        em.remove(patch);
    }
}
