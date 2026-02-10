package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Jeu;
import jakarta.persistence.EntityManager;
import com.projet_JVM2_DATA.entity.Utilisateur;
import jakarta.persistence.NoResultException;

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

    public Utilisateur findByPseudo(String pseudo) {
        try {
            return em.createQuery("SELECT u FROM Utilisateur u WHERE u.pseudo = :pseudo", Utilisateur.class)
                    .setParameter("pseudo", pseudo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Utilisateur> findAll() {
        return em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
    }

    public void delete(Utilisateur utilisateur) {
        em.remove(utilisateur);
    }
}
