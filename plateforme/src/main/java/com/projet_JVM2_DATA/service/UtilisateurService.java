package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Utilisateur;
import com.projet_JVM2_DATA.repository.UtilisateurRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService {

    public void inscription(String prenom, String nom, String pseudo, LocalDate date_naissance, String mdp) {
        // 1. On ouvre l'EntityManager
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 2. On instancie le Repository avec cet EM
            UtilisateurRepository repository = new UtilisateurRepository(em);

            // 3. Logique Métier
            Utilisateur utilisateur = new Utilisateur(prenom, nom, pseudo, mdp, LocalDate.now(),date_naissance);

            // Appel propre au repository
            repository.save(utilisateur);

            // 4. Validation
            tx.commit();
            System.out.println("Service : Utilisateur sauvegardé avec succès.");

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Utilisateur> ListeUtilisateurs(){
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try{
            UtilisateurRepository repository = new UtilisateurRepository(JpaUtil.getEntityManagerFactory().createEntityManager());
            utilisateurs = repository.findAll();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return utilisateurs;
    }
}
