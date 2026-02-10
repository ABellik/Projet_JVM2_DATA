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
            ModificationService modificationService = new ModificationService();
            for (Patch patch : list) {
                modificationService.supprimer(patch.getId());
                repository.delete(patch);
            }
        });
    }


}
