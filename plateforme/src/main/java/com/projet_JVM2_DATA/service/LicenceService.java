package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Licence;
import com.projet_JVM2_DATA.entity.Plateforme;
import com.projet_JVM2_DATA.repository.LicenceRepository;

public class LicenceService extends MainService {

    public void creer(Plateforme plateforme, Jeu jeu) {
        Licence licence = new Licence(plateforme,jeu);
        executeInTransaction(em -> {
            LicenceRepository repository = new LicenceRepository(em);
            repository.save(licence);
        });
    }

    public void supprimer(Long idLicence) {
        executeInTransaction(em -> {
            LicenceRepository repository = new LicenceRepository(em);
            Licence licence = repository.findById(idLicence);
            repository.delete(licence);
        });
    }
}

