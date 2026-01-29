package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.producer.EvaluationProducer;
import com.projet_JVM2_DATA.repository.BibliothequeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;

public class BibliothèqueService {

    private final EvaluationProducer producer;

    public BibliothèqueService(EvaluationProducer producer) {
        this.producer = producer;
    }


    public void ajouterJeu(Long idUtilisateur, Long idPlateforme, Long idJeu, LocalDate dateAchat, long prixAchat) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            //instanciation des repository
            BibliothequeRepository bibliothequeRepository = new BibliothequeRepository(em);

            //Ajout du jeu dans la bibliothèque
            Utilisateur userRef = em.getReference(Utilisateur.class, idUtilisateur);
            Plateforme platRef = em.getReference(Plateforme.class, idPlateforme);
            Jeu jeuRef = em.getReference(Jeu.class, idJeu);

            Bibliothèque bibliothèque = new Bibliothèque(userRef, platRef, jeuRef, dateAchat, prixAchat);
            bibliothequeRepository.save(bibliothèque);

            tx.commit();
        }
        catch (Exception ex) {
            if(tx.isActive()) tx.rollback();
            ex.printStackTrace();
        }
        finally{
            em.close();
        }
    }

    public void modifCommentaireJeu(Long idUtilisateur, Long idPlateforme, Long idJeu, String commentaire) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            //instanciation du repository
            BibliothequeRepository bibliothequeRepository = new BibliothequeRepository(em);

            //modification du commentaire
            Bibliothèque bibliotheque = bibliothequeRepository.findById(idUtilisateur, idPlateforme, idJeu);

            if(bibliotheque != null) {
                bibliotheque.setCommentaireJoueur(commentaire);
            }

            tx.commit();
        }
        catch (Exception ex) {
            if(tx.isActive()) tx.rollback();
            ex.printStackTrace();
        }
        finally{
            em.close();
        }
    }

    public void evaluationJeu(Long idUtilisateur, Long idPlateforme, Long idJeu, int note, String commentaire) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            //instanciation du repository
            BibliothequeRepository bibliothequeRepository = new BibliothequeRepository(em);

            //Récupération de la ligne à modifier
            Bibliothèque bibliotheque = bibliothequeRepository.findById(idUtilisateur, idPlateforme, idJeu);

            //modification de l'évaluation et possiblement du commentaire
            if(bibliotheque != null) {
                if(commentaire != null && !commentaire.isEmpty()) {
                    bibliotheque.setCommentaireJoueur(commentaire);
                }
                bibliotheque.setNoteJoueur((long) note);
                producer.envoyer(bibliotheque);
            }

            tx.commit();
        }
        catch (Exception ex) {
            if(tx.isActive()) tx.rollback();
            ex.printStackTrace();
        }
        finally{
            em.close();
        }
    }

}
