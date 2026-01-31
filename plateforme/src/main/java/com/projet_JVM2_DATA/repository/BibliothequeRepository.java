package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.BibliothèqueId;
import com.projet_JVM2_DATA.entity.Jeu;
import jakarta.persistence.EntityManager;
import com.projet_JVM2_DATA.entity.Bibliothèque;

import java.time.LocalDate;
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

    public long countAllJeuSemaine(Jeu jeu, LocalDate dateAuj) {

        LocalDate dateDebut = dateAuj.minusDays(7);

        return em.createQuery(
                        "SELECT COUNT(b) " +
                                "FROM Bibliothèque b " +
                                "WHERE b.jeu = :jeu " +
                                "AND b.dateAchat BETWEEN :dateDebut AND :dateFin",
                        Long.class
                )
                .setParameter("jeu", jeu)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateAuj)
                .getSingleResult();
    }

    public void delete(Bibliothèque bibliothèque) {
        em.remove(bibliothèque);
    }

}
