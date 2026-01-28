package com.projet_JVM2_DATA.kafka.producer;

import com.example.events.RequeteAuthentificationEditeur;
import com.example.events.SuppressionCompteEditeur;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;

public class SuppressionCompteProducer {

    public static void main(String[] args) throws SQLException
    {
        //Configuration pour la Sérialisation + vérification de conformité des données par rapport au schéma avro
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // Configuration pour éviter de perdre des messages en cas de kill brutal
        props.put(ProducerConfig.ACKS_CONFIG, "all");


        try (KafkaProducer<String, SuppressionCompteEditeur> producer = new KafkaProducer<>(props)) {

            SuppressionCompteEditeur suppressionCompte = new SuppressionCompteEditeur(Long.parseLong(args[0]), LocalDate.now());

            ProducerRecord<String, SuppressionCompteEditeur> record = new ProducerRecord<>("suppression-compte-editeur", null, suppressionCompte);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf(" Suppression du compte publiée : id=%s , date=%d%n ",
                            suppressionCompte.getId(),
                            suppressionCompte.getDate());
                } else {
                    System.err.println("❌ Erreur d'envoi : " + exception.getMessage());
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            producer.flush();
            System.out.println("🏁 Fin de l'envoi des notifications de suppression de compte.");

        }


    }


}
