package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Editeur;
import com.projet_JVM2_DATA.repository.EditeurRepository;
import jakarta.persistence.EntityManager;

public class EditeurService {

    public Editeur getEditeur(String nom) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EditeurRepository editeurRepository = new EditeurRepository(em);
        return  editeurRepository.findByNom(nom);
    }
    // TODO : à compléter
}
