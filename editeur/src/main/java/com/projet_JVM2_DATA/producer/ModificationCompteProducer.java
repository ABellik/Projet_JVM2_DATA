package com.projet_JVM2_DATA.producer;

import com.example.events.ModificationCompteEditeur;
import com.example.events.SuppressionCompteEditeur;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ModificationCompteProducer {
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


        try (KafkaProducer<String, ModificationCompteEditeur> producer = new KafkaProducer<>(props)) {


            String nomDuChamp = args[0];
            String nouvelleValeur = args[1];

            // 3. On construit la Map
            Map<String, String> modifications = new HashMap<>();
            modifications.put(nomDuChamp, nouvelleValeur);

            ModificationCompteEditeur modificationCompteEditeur = new ModificationCompteEditeur(Long.parseLong(args[2]), LocalDate.now(),modifications );

            ProducerRecord<String, ModificationCompteEditeur> record = new ProducerRecord<>("modification-compte-editeur", null, modificationCompteEditeur);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
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
            System.out.println("🏁 Fin de l'envoi des donnéees d'authentification.");

        }


    }
}
