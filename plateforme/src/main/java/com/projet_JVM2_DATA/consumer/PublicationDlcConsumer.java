package com.projet_JVM2_DATA.consumer;

import com.example.events.PublicationJeuOuDLC;
import com.projet_JVM2_DATA.entity.TypeJeu;
import com.projet_JVM2_DATA.service.JeuService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PublicationDlcConsumer implements Runnable {
    private final KafkaConsumer<String, PublicationJeuOuDLC> consumer;
    private final JeuService jeuService;

    public PublicationDlcConsumer(JeuService service){
        this.jeuService = service; // On injecte le service ici

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inscription-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("nouveau-dlc"));

            while (true) {
                ConsumerRecords<String, PublicationJeuOuDLC> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, PublicationJeuOuDLC> record : records) {
                    PublicationJeuOuDLC event = record.value();

                    //Enregistrement du jeu dans la table Jeu (genres inclus)
                    jeuService.publier2(event.getIdEditeur(), event.getNom(), event.getVersionActuelle(), (long) event.getPrixEditeur(), event.getIdParent(), TypeJeu.DLC ,event.getGenre());
                }
            }
        } finally {
            consumer.close();
        }
    }
}
