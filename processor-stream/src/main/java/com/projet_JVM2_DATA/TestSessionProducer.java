package com.projet_JVM2_DATA;

import com.example.events.Session;
import com.example.events.CauseFermetureSession;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class TestSessionProducer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put("schema.registry.request.timeout.ms", "30000");

        try (KafkaProducer<String, Session> producer = new KafkaProducer<>(props)) {

            // Génération de 15 sessions ciblées sur les IDs 1, 2, 3
            List<Session> sessions = generateTargetedMockSessions();

            System.out.println("🚀 Envoi de sessions pour les jeux 1, 2 et 3...");

            for (Session session : sessions) {
                // Utilisation de l'idJeu comme clé pour le groupement futur dans le Stream
                String key = String.valueOf(session.getIdJeu());

                ProducerRecord<String, Session> record = new ProducerRecord<>("nouvelles-session", key, session);

                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf(" 🎮 [Jeu %d] Session Joueur %d | Fin: %s | Partition: %d%n",
                                session.getIdJeu(), session.getIdJoueur(), session.getCauseFermeture(), metadata.partition());
                    } else {
                        System.err.println("❌ Erreur : " + exception.getMessage());
                    }
                });
            }

            producer.flush();
            System.out.println("🏁 Fin de l'envoi des sessions.");
        }
    }

    private static List<Session> generateTargetedMockSessions() {
        List<Session> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 15; i++) {
            // On tourne sur les IDs 1, 2, 3 (le modulo 3 + 1 donne 1, 2 ou 3)
            long idJeu = (i % 3) + 1;

            Instant debut = Instant.now().minus(random.nextInt(60), ChronoUnit.MINUTES);
            Instant fin = debut.plus(random.nextInt(30), ChronoUnit.MINUTES);

            CauseFermetureSession cause;
            String codeErreur = null;

            // Logique de test : le Jeu 2 crash souvent
            if (idJeu == 2 && random.nextBoolean()) {
                cause = CauseFermetureSession.CRASH;
                codeErreur = "FATAL_ERROR_JEU_2";
            } else if (idJeu == 3) {
                // Le Jeu 3 est souvent quitté de force
                cause = random.nextBoolean() ? CauseFermetureSession.FORCED_EXIT : CauseFermetureSession.NORMAL;
            } else {
                // Le Jeu 1 est stable
                cause = CauseFermetureSession.NORMAL;
            }

            Session s = Session.newBuilder()
                    .setIdJoueur((long) 100 + i)
                    .setIdJeu(idJeu)
                    .setVersionJeu("1.0." + i)
                    .setHeureDeDebut(debut)
                    .setHeureDeFin(fin)
                    .setCauseFermeture(cause)
                    .setCodeErreur(codeErreur)
                    .setSupport("PC")
                    .build();
            list.add(s);
        }
        return list;
    }
}