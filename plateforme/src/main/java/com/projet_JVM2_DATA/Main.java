package com.projet_JVM2_DATA;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.consumer.InscriptionJoueurConsumer;
import com.projet_JVM2_DATA.producer.EvaluationProducer;
import com.projet_JVM2_DATA.service.EvaluationService;
import com.projet_JVM2_DATA.service.UtilisateurService;
import jakarta.persistence.EntityManager;

import java.time.Instant;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        // 1. Instancier le dao
        UtilisateurService utilisateurService = new UtilisateurService();
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();


        //partie évaluation
        EvaluationProducer producer = new EvaluationProducer();

        EvaluationService service =
                new EvaluationService(em, producer);

        service.creerEvaluation(1,2,"1.6", "Super", Instant.now());

        // 2. Créer le consumer en lui donnant le dao
        InscriptionJoueurConsumer consumerTask = new InscriptionJoueurConsumer(utilisateurService);

        // 3. Lancer le consumer dans un thread dédié pour ne pas bloquer le Main
        Thread kafkaThread = new Thread(consumerTask);
        kafkaThread.start();

        System.out.println("🚀 Plateforme démarrée. En attente d'inscriptions via Kafka...");

    }
}
