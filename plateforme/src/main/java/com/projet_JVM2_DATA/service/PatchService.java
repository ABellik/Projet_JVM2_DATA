package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.entity.Patch;
import com.projet_JVM2_DATA.repository.PatchRepository;

import java.util.List;

public class PatchService extends MainService{

    public void creer(
            Jeu jeu,
            String version,
            String commentaireEditeur
    ){
        Patch patch = new Patch(jeu, version, commentaireEditeur);
        executeInTransaction(em -> {
            PatchRepository repository = new PatchRepository(em);
            repository.save(patch);
        });
    }

    public void supprimerJeu(Jeu jeu) {
        executeInTransaction(em -> {
            PatchRepository repository = new PatchRepository(em);
            List<Patch> list = repository.findByJeu(jeu);
            for (Patch patch : list) {
                // TODO : supprimer les modifications
                repository.delete(patch);
            }
        });
    }


}
