package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;
import com.projet_JVM2_DATA.repository.JeuRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

public class JeuService {

    public void publier(
            Long idEditeur,
            String nomJeu,
            String versionActuelle,
            Long prixEditeur,
            Long idJeuParent,
            TypeJeu type,
            List<String> genres
    ){
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            //instanciation des répository
            JeuRepository jeuRepository = new JeuRepository(em);
            GenreJeuRepository genreJeuRepository = new GenreJeuRepository(em);

            //création des proxys
            Editeur editeurProxy = em.getReference(Editeur.class, idEditeur);
            Jeu parentProxy = (idJeuParent != null) ? em.getReference(Jeu.class, idJeuParent) : null;

            //création du jeu
            Jeu jeu = new Jeu(editeurProxy, nomJeu, versionActuelle, prixEditeur, parentProxy, type);
            //enregistrement
            jeuRepository.save(jeu);

            //enregistrements des genres associés au jeu
            for(String genre : genres){
                Genrejeu genrejeu = new Genrejeu(jeu, genre);
                genreJeuRepository.save(genrejeu);
            }

            tx.commit();
        }
        catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        finally {
            em.close();
        }
    }

    public List<Long> getJeuByPseudo(String pseudo) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            JeuRepository repository = new JeuRepository(em);

            List<Long> jeux = repository.findByPseudo(pseudo);

            if (jeux == null || jeux.isEmpty()) {
                return new ArrayList<>();
            }
            return jeux;

        } finally {
            em.close();
        }
    }

    public List<Jeu> getAllJeu(){
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            JeuRepository repository = new JeuRepository(em);

            List<Jeu> jeux = repository.findAll();

            if (jeux == null || jeux.isEmpty()) {
                return new ArrayList<>();
            }
            return jeux;

        } finally {
            em.close();
        }
    }

    public void supprimer(Long idJeu) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            //instanciation du repository
            JeuRepository repository = new JeuRepository(em);

            // Suppression de l'historique des prix
            HistoriquePrixService historiquePrixService = new HistoriquePrixService();
            historiquePrixService.supprimerJeu(idJeu);

            // Suppression en cascade
            /*
             * Patch (+modification)
             * Licence
             * Genre
             * Bibliothèque (+Session)
             * Wishlist
             * */

            // Et enfin suppression du jeu
            Jeu jeu = repository.findById(idJeu);
            repository.delete(jeu);

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
