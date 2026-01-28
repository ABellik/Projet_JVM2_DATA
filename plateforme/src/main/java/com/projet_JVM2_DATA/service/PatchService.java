package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Patch;
import com.projet_JVM2_DATA.repository.PatchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class PatchService {

    public void creer(
            Jeu jeu,
            String version,
            String commentaireEditeur
    ){
        // 1. On ouvre l'EntityManager
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 2. On instancie le Repository avec cet EM
            PatchRepository repository = new PatchRepository(em);

            // 3. Logique Métier
            Patch patch = new Patch(jeu, version, commentaireEditeur);

            // Appel propre au repository
            repository.save(patch);

            // 4. Validation
            tx.commit();
            System.out.println("Service : Patch sauvegardé avec succès.");

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
