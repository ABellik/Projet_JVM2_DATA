package com.projet_JVM2_DATA;

import com.example.events.EvaluationJeu;
import com.projet_JVM2_DATA.editeur.dao.JeuOuDLCDAO;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.sql.SQLException;
import java.util.List;

public class TestEvaluationJeuProducer {

    public static void main(String[] args) throws SQLException {

        // 1) Configuration pour la Sérialisation Avro et Schema Registry
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        // Configuration de fiabilité : on attend que tous les brokers confirment la réception
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, EvaluationJeu> producer = new KafkaProducer<>(props)) {

            // Récupération des accès base de données via variables d'environnement
            String url = System.getenv("DB_URL");
            String username = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // Service pour interroger la table 'evaluation'
            JeuOuDLCDAO service = new JeuOuDLCDAO(url, username, password);

            // 2) Récupération des nouvelles évaluations (méthode à créer dans ton Service)
            List<EvaluationJeu> evaluations = service.recuperationEvaluations();

            System.out.println("📊 Analyse de la base : " + evaluations.size() + " nouvelles évaluations trouvées.");

            for (EvaluationJeu eval : evaluations) {

                // 3) Préparation du Record
                // Le topic est "evaluations-jeu"
                // On peut mettre l'ID du joueur en clé pour assurer l'ordre par utilisateur
                String key = String.valueOf(eval.getIdJeu());
                ProducerRecord<String, EvaluationJeu> record = new ProducerRecord<>("evaluations-jeu", key, eval);

                // 4) Envoi asynchrone vers Kafka
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        // Attention : respecte la casse de ton schéma (Note avec N majuscule)
                        System.out.printf(" ⭐ Évaluation produite : JeuID=%d | Note=%d | Topic=%s | Partition=%d%n",
                                eval.getIdJeu(),
                                eval.getNote(),
                                metadata.topic(),
                                metadata.partition());
                    } else {
                        System.err.println("❌ Erreur lors de la production de l'évaluation : " + exception.getMessage());
                    }
                });

                // Petite pause pour la stabilité du flux
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 5) On force l'envoi des derniers messages avant de fermer
            producer.flush();
            System.out.println("🏁 Toutes les évaluations ont été envoyées sur le topic 'evaluations-jeu'.");
        }
    }
}