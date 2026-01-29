package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Licence;
import com.projet_JVM2_DATA.entity.Plateforme;
import com.projet_JVM2_DATA.repository.JeuRepository;
import com.projet_JVM2_DATA.repository.LicenceRepository;
import jakarta.persistence.EntityManager;

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

    public void supprimer(Long idLicence) {
        executeInTransaction(em -> {
            LicenceRepository repository = new LicenceRepository(em);
            Licence licence = repository.findById(idLicence);
            repository.delete(licence);
        });
    }
}

