/*package com.projet_JVM2_DATA.consumer;


import com.example.events.Session; // Ta classe générée par Avro
import com.projet_JVM2_DATA.service.SessionService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class SessionConsumer implements Runnable{
    private final KafkaConsumer<String, Session> consumer;
    private final SessionService sessionService;

    public SessionConsumer(SessionService service) {
        this.sessionService = service; // On injecte le dao ici

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "session-group" + UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("session-launched"));
            while (true) {
                ConsumerRecords<String, Session> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, Session> record : records) {
                    Session event = record.value();

                    // Récupération des timestamps en millisecondes
                    Instant debut = event.getHeureDeDebut();
                    Instant fin = event.getHeureDeFin();

                    // Calculer l'écart
                    Duration ecart = Duration.between(debut, fin);

                    // Récupérer la valeur en minutes
                    long minutes = ecart.toMinutes();

                    // Conversion de l'Enum Avro vers l'Enum JPA
                    // On utilise .name() pour faire le pont via le texte
                    com.projet_JVM2_DATA.entity.TypeSession typeSession =
                            com.projet_JVM2_DATA.entity.TypeSession.valueOf(event.getCauseFermeture().name());

                    // APPEL DU SERVICE
                    sessionService.enregistrementSession(
                            event.getIdJoueur(),
                            event.getSupport(),
                            event.getIdJeu(),
                            minutes,
                            typeSession
                    );
                }
            }
        } finally {
            consumer.close();
        }
    }
}*/

package com.projet_JVM2_DATA.consumer;

import com.example.events.Session;
import com.projet_JVM2_DATA.service.SessionService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class SessionConsumer implements Runnable {
    private final KafkaConsumer<String, Session> consumer;
    private final SessionService sessionService;

    public SessionConsumer(SessionService service) {
        this.sessionService = service;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "session-group"+ UUID.randomUUID().toString());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("session-launched"));

            System.out.println("SessionConsumer démarré et en écoute...");

            while (true) {
                ConsumerRecords<String, Session> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, Session> record : records) {
                    try {
                        System.out.println("Message de session reçu ! Traitement...");

                        Session event = record.value();

                        Instant debut = event.getHeureDeDebut();
                        Instant fin = event.getHeureDeFin();

                        Duration ecart = Duration.between(debut, fin);
                        long minutes = ecart.toMinutes();

                        com.projet_JVM2_DATA.entity.TypeSession typeSession;
                        try {
                            typeSession = com.projet_JVM2_DATA.entity.TypeSession.valueOf(event.getCauseFermeture().name());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Erreur Enum : Le type '" + event.getCauseFermeture().name() + "' n'existe pas dans l'entité JPA.");
                            continue; // On passe au suivant
                        }

                        sessionService.enregistrementSession(
                                event.getIdJoueur(),
                                event.getSupport(),
                                event.getIdJeu(),
                                minutes,
                                typeSession
                        );

                        System.out.println("Session enregistrée avec succès.");

                    } catch (Exception e) {
                        System.err.println("Erreur lors du traitement d'une session : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur fatale du consumer Session : " + e.getMessage());
        } finally {
            consumer.close();
        }
    }
}
