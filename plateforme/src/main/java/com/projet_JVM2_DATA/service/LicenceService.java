package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Licence;
import com.projet_JVM2_DATA.entity.Plateforme;
import com.projet_JVM2_DATA.repository.LicenceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class LicenceService extends MainService {

    public void creer(Plateforme plateforme, Jeu jeu) {
        Licence licence = new Licence(plateforme,jeu);
        executeInTransaction(em -> {
            LicenceRepository repository = new LicenceRepository(em);
            repository.save(licence);
        });
    }

    public void creerLicencesPourJeu(Long idJeu, List<String> nomsSupports) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        try {
            Jeu jeu = em.find(Jeu.class, idJeu);
            if (jeu == null) throw new RuntimeException("Jeu introuvable avec l'ID " + idJeu);

            for (String nomSupport : nomsSupports) {


                TypedQuery<Plateforme> query = em.createQuery(
                        "SELECT p FROM Plateforme p WHERE p.nom = :nom", Plateforme.class);
                query.setParameter("nom", nomSupport);

                List<Plateforme> resultats = query.getResultList();

                if (!resultats.isEmpty()) {
                    Plateforme plateforme = resultats.getFirst();

                    Licence nouvelleLicence = new Licence();
                    nouvelleLicence.setIdJeu(jeu);
                    nouvelleLicence.setIdPlateforme(plateforme);

                    em.persist(nouvelleLicence);
                } else {
                    System.err.println("Attention : Support inconnu reçu dans le message Kafka : " + nomSupport);
                }
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<String> getNomLicencesByIdJeu(Long idJeu) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            LicenceRepository repository = new LicenceRepository(em);

            List<String> licences = repository.findNomByIdJeu(idJeu);

            if (licences == null || licences.isEmpty()) {
                return new ArrayList<>();
            }
            return licences;

        } finally {
            em.close();
        }
    }

    public List<Long> getIdPlateformeByIdJeu(Long idJeu) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            LicenceRepository repository = new LicenceRepository(em);

            List<Long> licences = repository.findIdPlateformeByIdJeu(idJeu);

            if (licences == null || licences.isEmpty()) {
                return new ArrayList<>();
            }
            return licences;

        } finally {
            em.close();
        }
    }



    public void supprimer(Long idLicence) {
        executeInTransaction(em -> {
            LicenceRepository repository = new LicenceRepository(em);
            Licence licence = repository.findById(idLicence);
            repository.delete(licence);
        });
    }
}

