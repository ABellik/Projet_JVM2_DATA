package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Session;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SessionRepository {
    private final EntityManager em;

    public SessionRepository(EntityManager em) {
        this.em = em;
    }
    public Session findById(Integer id) {
        return em.find(Session.class, id);
    }

    public List<Session> findAll() {
        return em.createQuery("SELECT s FROM Session s", Session.class).getResultList();
    }

    public void save(Session session) {
        em.persist(session);
    }

    public void delete(Session session) {
        em.remove(session);
    }
}
