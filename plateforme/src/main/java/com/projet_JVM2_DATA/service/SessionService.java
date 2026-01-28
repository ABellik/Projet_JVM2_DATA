package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.repository.BibliothequeRepository;
import com.projet_JVM2_DATA.repository.PlateformeRepository;
import com.projet_JVM2_DATA.repository.SessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {

    // Cache mémoire pour éviter de re-chercher l'ID du support à chaque message
    private final Map<String, Long> plateformeCache = new ConcurrentHashMap<>();

    public void enregistrementSession(Long idUtilisateur, String nomPlateforme, Long idJeu, long duree, TypeSession type) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // --- 1. RÉCUPÉRATION DE L'ID PLATEFORME ---
            Long idPlateforme = plateformeCache.get(nomPlateforme);
            if (idPlateforme == null) {
                PlateformeRepository platRepo = new PlateformeRepository(em);
                Plateforme p = platRepo.findByNom(nomPlateforme);
                if (p == null) throw new RuntimeException("Plateforme inconnue : " + nomPlateforme);
                idPlateforme = p.getId();
                plateformeCache.put(nomPlateforme, idPlateforme); // Mise en cache
            }

            //instanciation des répository
            SessionRepository sessionRepository = new SessionRepository(em);
            BibliothequeRepository bibliothequeRepository = new BibliothequeRepository(em);

            // --- 2. RÉCUPÉRATION DES RÉFÉRENCES (PROXIES) ---
            // getReference crée un "Proxy" (une coquille vide).
            // Il ne fait AUCUNE requête SQL en base.
            // Il crée juste un faux objet Java qui contient l'ID.
            Utilisateur userRef = em.getReference(Utilisateur.class, idUtilisateur);
            Plateforme platRef = em.getReference(Plateforme.class, idPlateforme);
            Jeu jeuRef = em.getReference(Jeu.class, idJeu);

            // ---3. Enregistrement de la nouvelle session
            Session session = new Session(userRef, platRef, jeuRef, duree, type);
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
