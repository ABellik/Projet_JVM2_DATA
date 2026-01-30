package com.projet_JVM2_DATA.service;

import com.example.events.InfoJeu;
import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.producer.InfoJeuProducer;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;
import com.projet_JVM2_DATA.repository.JeuRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

public class JeuService {

    public Long publier(
            Long idEditeur,
            String nomJeu,
            String versionActuelle,
            Long prixEditeur,
            Long idJeuParent,
            TypeJeu type,
            List<String> genres
    ){
        // TODO : Création des Licences

        // Vérification prix éditeur
        if (prixEditeur<0){
            prixEditeur = 0L;
        }

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

            // Création Historique des prix
            HistoriquePrixService historiquePrixService = new HistoriquePrixService();
            historiquePrixService.creer(jeu, prixEditeur, prixEditeur, "Soit la première personne à essayer le jeu !");

            tx.commit();

            return jeu.getId();
        }
        catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
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
            Jeu jeu = repository.findById(idJeu);

            // Suppression des genres du jeu
            GenreJeuService genreJeuService = new GenreJeuService();
            genreJeuService.supprimerJeu(jeu);

            // Suppression de l'historique des prix
            HistoriquePrixService historiquePrixService = new HistoriquePrixService();
            historiquePrixService.supprimerJeu(jeu);

            // Suppression des patchs
            PatchService patchService = new PatchService();
            patchService.supprimerJeu(jeu);

            // TODO : Suppression en cascade
            /*
             * Patch (+modification)
             * Licence
             * Bibliothèque (+Session)
             * Wishlist
             * */

            // Et enfin suppression du jeu
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
