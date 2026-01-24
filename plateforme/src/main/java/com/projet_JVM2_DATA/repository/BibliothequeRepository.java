package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.BibliothèqueId;
import com.projet_JVM2_DATA.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import com.projet_JVM2_DATA.entity.Bibliothèque;
import java.util.List;

public class BibliothequeRepository {
    private final EntityManager em;

    // On lui passe l'EntityManager via le constructeur
    public BibliothequeRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Bibliothèque bibliothèque) {
        em.persist(bibliothèque);
    }

    public Bibliothèque findById(Long idUtilisateur, Long idPlateforme, Long idJeu) {
        // 1. On construit l'objet Clé Composite
        BibliothèqueId id = new BibliothèqueId(idUtilisateur, idPlateforme, idJeu);

        // 2. On passe cet OBJET clé à l'EntityManager
        return em.find(Bibliothèque.class, id);
    }

    public List<Bibliothèque> findAll() {
        return em.createQuery("SELECT b FROM Bibliothèque b", Bibliothèque.class).getResultList();
    }

    public void delete(Bibliothèque bibliothèque) {
        em.remove(bibliothèque);
    }

}
