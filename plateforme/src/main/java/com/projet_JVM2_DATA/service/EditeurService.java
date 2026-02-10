package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Editeur;
import com.projet_JVM2_DATA.repository.EditeurRepository;
import jakarta.persistence.EntityManager;

public class EditeurService extends MainService {

    public Editeur getEditeur(String nom) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EditeurRepository editeurRepository = new EditeurRepository(em);
        return  editeurRepository.findByNom(nom);
    }

    public void creer(String type, String nom, String mdp, String email) {

        Editeur editeur = new Editeur(type, nom, mdp, email);

        executeInTransaction(em -> {
            EditeurRepository repository = new EditeurRepository(em);
            repository.save(editeur);
        });
    }

    public void supprimer(Long idEditeur) {
        executeInTransaction(em -> {
            EditeurRepository repository = new EditeurRepository(em);
            Editeur editeur = repository.findById(idEditeur);
            repository.delete(editeur);
        });
    }
}
