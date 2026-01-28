package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;
import com.projet_JVM2_DATA.repository.JeuRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class JeuService {

    public void publier(
            Editeur editeur,
            String nomJeu,
            String versionActuelle,
            Long prixEditeur,
            Jeu jeuParent,
            TypeJeu type
            ) {
        // 1. On ouvre l'EntityManager
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 2. On instancie le Repository avec cet EM
            JeuRepository repository = new JeuRepository(em);

            // 3. Logique Métier
            Jeu jeu = new Jeu(editeur, nomJeu, versionActuelle, prixEditeur, jeuParent, type);

            // Appel propre au repository
            repository.save(jeu);

            // 4. Validation
            tx.commit();
            System.out.println("Service : Jeu sauvegardé avec succès.");

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void publier2(
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

    public void supprimer(Long idJeu) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            //instanciation du repository
            JeuRepository repository = new JeuRepository(em);

            //récupération de l'utilisateur
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
