package com.projet_JVM2_DATA.kafka.consumer;

import com.example.events.Session;
import com.projet_JVM2_DATA.dao.CrashDao;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/*
* Consomme les evenements de types crashs envoyés depuis la plateforme et les stocke dans la base de données
* Il permet globalement à l'editeur de conserver un historique de crashs par jeu.
* */

public class CrashConsumer {

    private final Consumer<String, Session> consumer;
    private final CrashDao crashDao;

    public CrashConsumer(String bootstrapServers, String schemaRegistryUrl) {
        this.crashDao = new CrashDao();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "editeur-crash-processor");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

   // methode aui lance la boucle de consommation des crashs
    public void start(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        System.out.println("Editeur prêt à recevoir les crashs ...");

        try {
            while (true) {
                ConsumerRecords<String, Session> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, Session> record : records) {
                    try {
                        Session crash = record.value();
                        System.out.println("Crash reçu key=" + record.key() + " idJeu=" + crash.getIdJeu());
                        crashDao.insert(crash);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }

}