package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.repository.BibliothequeRepository;
import com.projet_JVM2_DATA.repository.SessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class SessionService {

    public void enregistrementSession(Long idUtilisateur, Long idPlateforme, Long idJeu, Long idDLC, long duree, TypeSession type) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            //instanciation des répository
            SessionRepository sessionRepository = new SessionRepository(em);
            BibliothequeRepository bibliothequeRepository = new BibliothequeRepository(em);

            // getReference crée un "Proxy" (une coquille vide).
            // Il ne fait AUCUNE requête SQL en base.
            // Il crée juste un faux objet Java qui contient l'ID.
            Utilisateur userRef = em.getReference(Utilisateur.class, idUtilisateur);
            Plateforme platRef = em.getReference(Plateforme.class, idPlateforme);
            Jeu jeuRef = em.getReference(Jeu.class, idJeu);
            Jeu dlcRef = em.getReference(Jeu.class, idDLC);

            //Enregistrement de la nouvelle session
            Session session = new Session(userRef, platRef, jeuRef, dlcRef, duree, type);
            sessionRepository.save(session);

            //Modification de la durée de jeu totale enregistrée dans la bibliothèque
            Bibliothèque bibliothèque = bibliothequeRepository.findById(idUtilisateur, idPlateforme, idJeu);
            if(bibliothèque != null){
                bibliothèque.setTempsJeu(bibliothèque.getTempsJeu() + duree);
            }

            // Validation
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
