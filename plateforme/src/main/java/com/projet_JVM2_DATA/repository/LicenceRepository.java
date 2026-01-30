package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Licence;
import jakarta.persistence.EntityManager;

import java.util.List;

public class LicenceRepository {
    private final EntityManager em;

    public LicenceRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Licence licence) {
        this.em.persist(licence);
    }

    public Licence findById(Long id) {
        return em.find(Licence.class, id);
    }

    public List<String> findNomByIdJeu(Long idJeu){
        return em.createQuery("SELECT l.idPlateforme.nom FROM Licence l WHERE l.idJeu.id = :idJeu", String.class).setParameter("idJeu", idJeu).getResultList();
    }

    public List<Licence> findAll() {
        return em.createQuery("select l from Licence l", Licence.class).getResultList();
    }

    public void delete(Licence licence) {
        this.em.remove(licence);
    }

}
