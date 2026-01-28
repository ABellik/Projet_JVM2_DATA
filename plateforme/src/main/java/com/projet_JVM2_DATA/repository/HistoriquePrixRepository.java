package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.HistoriquePrix;
import jakarta.persistence.EntityManager;

import java.util.List;

public class HistoriquePrixRepository {
    private final EntityManager em;

    public HistoriquePrixRepository(EntityManager em) {
        this.em = em;
    }

    public void save(HistoriquePrix historiquePrix) {
        em.persist(historiquePrix);
    }

    public HistoriquePrix findById(Long id) {
        return em.find(HistoriquePrix.class, id);
    }

    public List<HistoriquePrix> findAll() {
        return em.createQuery("select h from  HistoriquePrix h", HistoriquePrix.class).getResultList();
    }

    public void delete(HistoriquePrix historiquePrix) {
        em.remove(historiquePrix);
    }

}
