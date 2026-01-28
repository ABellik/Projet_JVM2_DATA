package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Editeur;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EditeurRepository {
    private final EntityManager em;

    // On lui passe l'EntityManager via le constructeur
    public EditeurRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Editeur editeur){
        this.em.persist(editeur);
    }

    public Editeur findById(Long id){
        return this.em.find(Editeur.class, id);
    }

    public List<Editeur> findAll(){
        return this.em.createQuery("select e from Editeur e", Editeur.class).getResultList();
    }

    public void delete(Editeur editeur){
        this.em.remove(editeur);
    }
}
