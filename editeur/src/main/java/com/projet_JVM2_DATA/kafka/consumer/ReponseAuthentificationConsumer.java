package com.projet_JVM2_DATA.kafka.consumer;

import com.example.events.ReponseAuthentificationEditeur;
import com.example.events.Session;
import com.projet_JVM2_DATA.dao.CrashDao;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public class ReponseAuthentificationConsumer {

    // VOLATILE est obligatoire pour que le changement soit visible entre Threads
    public static volatile long idEditeur;
    private final Consumer<String, ReponseAuthentificationEditeur> consumer;

    public ReponseAuthentificationConsumer(String bootstrapServers, String schemaRegistryUrl) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-dao-group");

        // Désérialisation
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        // CONFIG CRUCIALE : Dit au désérialiseur de créer l'objet spécifique (OrderPlaced)
        // et non un GenericRecord générique.
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);

    }

    public void demarrerEcoute() {
            consumer.subscribe(Collections.singletonList("reponse-requete-authentification"));

            System.out.println("🎧 En attente d'évènements Avro...");

            while (true) {
                ConsumerRecords<String, ReponseAuthentificationEditeur> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, ReponseAuthentificationEditeur > record : records) {

                    ReponseAuthentificationEditeur event = record.value();
                    idEditeur= event.getIdEditeur();

                    System.out.printf("Message reçu ! User: %s, Montant: %.2f%n");
                }
            }
        }

    public static long getIdEditeur() {
        return idEditeur;
    }

    public static long resetId() {
        return idEditeur=0;
    }
}

