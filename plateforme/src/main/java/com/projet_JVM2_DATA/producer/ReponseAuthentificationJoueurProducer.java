package com.projet_JVM2_DATA.producer;

import com.example.events.ReponseAuthentificationJoueur;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Producer d'inscription pour tester si le consumer fonctionne
 */
public class ReponseAuthentificationJoueurProducer {
    private final KafkaProducer<String, ReponseAuthentificationJoueur> producer;

    public ReponseAuthentificationJoueurProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        this.producer = new KafkaProducer<>(props);
    }

    public void envoyerReponseAuthentificationJoueur(ReponseAuthentificationJoueur event) {
        ProducerRecord<String, ReponseAuthentificationJoueur> record =
                new ProducerRecord<>("reponse-authentification-joueur", event.getPseudo(), event);

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Message envoyé au topic " + metadata.topic() + " à l'offset " + metadata.offset());
            } else {
                System.err.println("Erreur d'envoi : " + exception.getMessage());
            }
        });
    }

    public void close() {
        producer.close();
    }
}
