package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Consumer;

public abstract class MainService {

    protected void executeInTransaction(Consumer<EntityManager> action) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}