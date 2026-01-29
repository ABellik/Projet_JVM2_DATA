package com.projet_JVM2_DATA.service;

import com.example.events.EvaluationJeu;
import com.projet_JVM2_DATA.entity.Editeur;
import com.projet_JVM2_DATA.producer.EvaluationProducer;
import com.projet_JVM2_DATA.repository.EditeurRepository;
import com.projet_JVM2_DATA.repository.EvaluationRepository;
import jakarta.persistence.EntityManager;

import java.time.Instant;

public class EvaluationService {

    private final EvaluationProducer producer;
    private final EntityManager em;
    private final EvaluationRepository repository;

    public EvaluationService(EntityManager em, EvaluationProducer producer) {
        this.em = em;
        this.repository = new EvaluationRepository(em);
        this.producer = producer;
    }

    public void creerEvaluation(long idJeu, int note, String version, String commentaire, Instant date) {
        EvaluationJeu eval = new EvaluationJeu(idJeu, note, version, commentaire, date);

        try {
            em.getTransaction().begin();

            repository.save(eval);

            em.getTransaction().commit();


            producer.envoyer(eval);

            System.out.println("✅ Évaluation enregistrée et notifiée !");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("❌ Erreur : Annulation de l'opération.");
            throw e;
        } finally {
            em.close();
        }
    }
}


