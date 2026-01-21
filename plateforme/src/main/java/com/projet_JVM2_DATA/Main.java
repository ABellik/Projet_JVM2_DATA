package com.projet_JVM2_DATA;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Utilisateur; // Importez votre entité
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //Obtenir l'EntityManager
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 2. Créer un objet (Entité)
            Utilisateur nouveauUtilisateur = new Utilisateur("test","nomtest","prenomtest", LocalDate.of(2004,9,3), LocalDate.now());
            Utilisateur u2 = new Utilisateur("test2","nomtest2","prenomtest2", LocalDate.now(), LocalDate.now());

            nouveauUtilisateur.ajouterAmi(u2);
            // 3. Persister (Sauvegarder)
            em.persist(nouveauUtilisateur);
            em.persist(u2);

            tx.commit();
            System.out.println("✅ Utilisateur sauvegardé avec l'ID : " + nouveauUtilisateur.getId());

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
