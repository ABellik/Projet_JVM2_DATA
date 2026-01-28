package com.projet_JVM2_DATA.repository;

import jakarta.persistence.EntityManager;
import com.projet_JVM2_DATA.entity.Utilisateur;
import java.util.List;

public class UtilisateurRepository {
    private final EntityManager em;

    // On lui passe l'EntityManager via le constructeur
    public UtilisateurRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    public Utilisateur findById(Long id) {
        return em.find(Utilisateur.class, id);
    }

    public List<Utilisateur> findAll() {
        return em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
    }

    public void delete(Utilisateur utilisateur) {
        em.remove(utilisateur);
    }
}
