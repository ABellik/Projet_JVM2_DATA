package com.projet_JVM2_DATA.repository;

import com.example.events.EvaluationJeu;
import com.projet_JVM2_DATA.entity.Editeur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class EvaluationRepository {
    private final EntityManager em;

    // On lui passe l'EntityManager via le constructeur
    public EvaluationRepository(EntityManager em) {
        this.em = em;
    }

    public void save(EvaluationJeu evaluation){
        this.em.persist(evaluation);
    }

    public EvaluationJeu findById(Long id){
        return this.em.find(EvaluationJeu.class, id);
    }

    public List<EvaluationJeu> findAll(){
        return this.em.createQuery("select e from Evaluation e", EvaluationJeu.class).getResultList();
    }


    public void delete(Editeur editeur){
        this.em.remove(editeur);
    }
}
