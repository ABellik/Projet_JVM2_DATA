package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Plateforme;
import com.projet_JVM2_DATA.entity.Utilisateur;
import com.projet_JVM2_DATA.entity.Wishlist;
import com.projet_JVM2_DATA.repository.WishlistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WishlistService {

    public void ajouterJeu(Long idUtilisateur, Long idPlateforme, Long idJeu, LocalDate date_ajout) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            //instanciation du repository
            WishlistRepository wishlistRepository = new WishlistRepository(em);

            //Ajout du jeu dans la liste de souhaits
            Utilisateur userRef = em.getReference(Utilisateur.class, idUtilisateur);
            Plateforme platRef = em.getReference(Plateforme.class, idPlateforme);
            Jeu jeuRef = em.getReference(Jeu.class, idJeu);

            Wishlist wishlist = new Wishlist(userRef, platRef, jeuRef, date_ajout);
            wishlistRepository.save(wishlist);

            tx.commit();
        }
        catch (Exception ex) {
            if(tx.isActive())
                tx.rollback();
            ex.printStackTrace();
        }
        finally{
            em.close();
        }
    }

    public List<Long> getWishlistByIdUtilisateur(Long idUtilisateur) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            WishlistRepository repository = new WishlistRepository(em);

            List<Long> wishlist = repository.findByIdUtilisateur(idUtilisateur);

            if(wishlist == null || wishlist.isEmpty()){
                return new ArrayList<>();
            }

            return wishlist;
        } finally {
            em.close();
        }

    }
}
