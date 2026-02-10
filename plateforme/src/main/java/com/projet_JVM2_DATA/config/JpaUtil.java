package com.projet_JVM2_DATA.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            // "plateformePU" doit correspondre au nom dans persistence.xml
            emf = Persistence.createEntityManagerFactory("plateformePU");
        } catch (Throwable ex) {
            System.err.println("Erreur d'initialisation JPA : " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
