package com.projet_JVM2_DATA.kafka.consumer;

import com.example.events.PublicationJeuOuDLC;
//import com.projet_JVM2_DATA.dao.JeuOuDlcDao;
import com.projet_JVM2_DATA.dao.JeuOuDlcDao;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


public class DLCConsumer {
    public static void main(String[] args) {
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


        //Permet de récupérer les dlc par id de jeu, en établissant une connexion à la base au préalable
        JeuOuDlcDao jeuOuDLCDAO = new JeuOuDlcDao(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        );
        try (KafkaConsumer<String, PublicationJeuOuDLC > consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("demandes-creation-dlc"));

            System.out.println("🎧 En attente d'évènements Avro...");

            while (true) {
                ConsumerRecords<String, PublicationJeuOuDLC > records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, PublicationJeuOuDLC > record : records) {

                    //on récupère le dlc associé à l'id du jeu donné
                    jeuOuDLCDAO.getDLCByID(record.value().getId());

                    PublicationJeuOuDLC event = record.value();

                    System.out.printf("Message reçu ! User: %s, Montant: %.2f%n");
                }
            }
        }
    }
}
