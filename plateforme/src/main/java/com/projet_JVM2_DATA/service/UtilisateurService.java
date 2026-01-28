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

    public void creationAmitie(Long idUtilisateur1, Long idUtilisateur2){
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            UtilisateurRepository repository = new UtilisateurRepository(em);

            Utilisateur utilisateur1 = repository.findById(idUtilisateur1);
            Utilisateur utilisateur2 = repository.findById(idUtilisateur2);

            // Vérification simple
            if (utilisateur1 == null || utilisateur2 == null) {
                throw new IllegalArgumentException("Un des joueurs n'existe pas !");
            }

            utilisateur1.ajouterAmi(utilisateur2);

            tx.commit();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            em.close();
        }
    }

    public void supprimerAmitie(Long idUtilisateur1, Long idUtilisateur2){
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            //instanciation du repository
            UtilisateurRepository repository = new UtilisateurRepository(em);

            Utilisateur utilisateur1 = repository.findById(idUtilisateur1);
            Utilisateur utilisateur2 = repository.findById(idUtilisateur2);

            if (utilisateur1 == null || utilisateur2 == null) {
                throw new IllegalArgumentException("Un des joueurs n'existe pas !");
            }

            utilisateur1.retirerAmi(utilisateur2);

            tx.commit();
        }
        catch(Exception e){
            if(tx.isActive()){
                tx.rollback();
            }
            e.printStackTrace();
        }
        finally{
            em.close();
        }
    }

    public void supprimerCompte(Long idUtilisateur){
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            //instanciation du repository
            UtilisateurRepository repository = new UtilisateurRepository(em);

            //récupération de l'utilisateur
            Utilisateur utilisateur = repository.findById(idUtilisateur);

            repository.delete(utilisateur);

            tx.commit();
        }
        catch(Exception e){
            if(tx.isActive()){
                tx.rollback();
            }
            e.printStackTrace();
        }
        finally{
            em.close();
        }
    }
}
