package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.*;
import com.projet_JVM2_DATA.repository.PlateformeRepository;
import jakarta.persistence.EntityManager;


public class PlateformeService {
    public Plateforme getPlateformeByNom(String nom){
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {

            PlateformeRepository plateformeRepository = new PlateformeRepository(em);
            return plateformeRepository.findByNom(nom);
        }
        finally {
            em.close();
        }
    }
}
