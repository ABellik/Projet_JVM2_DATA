package com.projet_JVM2_DATA.repository;

import jakarta.persistence.EntityManager;
import com.projet_JVM2_DATA.entity.Plateforme;
import jakarta.persistence.NoResultException;

import java.util.List;

public class PlateformeRepository {
    private final EntityManager em;

    // On lui passe l'EntityManager via le constructeur
    public PlateformeRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Plateforme plateforme) {
        em.persist(plateforme);
    }

    public Plateforme findById(Long id) {
        return em.find(Plateforme.class, id);
    }

    public Plateforme findByNom(String nom) {
        try {
            return this.em.createQuery("SELECT p FROM Plateforme p WHERE p.nom = :pNom", Plateforme.class)
                    .setParameter("pNom", nom)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Retourne null si aucune plateforme n'est trouvé
        }
    }

    public List<Plateforme> findAll() {
        return em.createQuery("SELECT p FROM Plateforme p", Plateforme.class).getResultList();
    }

    public void delete(Plateforme plateforme) {
        em.remove(plateforme);
    }
}
