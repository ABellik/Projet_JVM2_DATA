package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.Modification;
import com.projet_JVM2_DATA.entity.Patch;
import com.projet_JVM2_DATA.entity.TypeModif;
import com.projet_JVM2_DATA.repository.ModificationRepository;

import java.util.List;

public class ModificationService extends MainService {

    public void creer(Patch patch, TypeModif modif) {
        Modification modification = new Modification(patch, modif);
        executeInTransaction(em -> {
            ModificationRepository repository = new ModificationRepository(em);
            repository.save(modification);
        });
    }

    public void supprimer(long idPatch) {
        executeInTransaction(em ->{
            ModificationRepository repository = new ModificationRepository(em);
            List<Modification> list = repository.findByPatch(idPatch);
            for(Modification modification : list){
                em.remove(modification);
            }
        });
    }

    public void supprimer(Long idModification) {
        executeInTransaction(em -> {
            ModificationRepository repository = new ModificationRepository(em);
            Modification modification = repository.findById(idModification);
            repository.delete(modification);
        });
    }
}
