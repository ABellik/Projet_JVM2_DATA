package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Wishlist;
import com.projet_JVM2_DATA.entity.WishlistId;
import jakarta.persistence.EntityManager;

import java.util.List;

public class WishlistRepository {
    private final EntityManager em;

    // On lui passe l'EntityManager via le constructeur
    public WishlistRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Wishlist wishlist) {
        em.persist(wishlist);
    }

    public Wishlist findById(Long idUtilisateur, Long idPlateforme, Long idJeu) {
        // 1. On construit l'objet Clé Composite
        WishlistId id = new WishlistId(idUtilisateur, idPlateforme, idJeu);

        // 2. On passe cet OBJET clé à l'EntityManager
        return em.find(Wishlist.class, id);
    }

    public List<Wishlist> findAll() {
        return em.createQuery("SELECT w FROM Wishlist w", Wishlist.class).getResultList();
    }

    public List<Long> findByIdUtilisateur(Long idUtilisateur) {
        return em.createQuery("SELECT w.idJeu.id FROM Wishlist w WHERE w.idUtilisateur.id = :idUtilisateur",Long.class).setParameter("idUtilisateur", idUtilisateur).getResultList();
    }

    public void delete(Wishlist wishlist) {
        em.remove(wishlist);
    }
}
