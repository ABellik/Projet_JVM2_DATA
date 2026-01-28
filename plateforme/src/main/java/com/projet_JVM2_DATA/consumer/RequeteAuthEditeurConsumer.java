package com.projet_JVM2_DATA.consumer;

import com.example.events.RequeteAuthentificationEditeur;
import com.projet_JVM2_DATA.entity.Editeur;
import com.projet_JVM2_DATA.producer.RepAuthEditeurProducer;
import com.projet_JVM2_DATA.service.EditeurService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class RequeteAuthEditeurConsumer implements Runnable {

    private final KafkaConsumer<String, RequeteAuthentificationEditeur> consumer;
    private final EditeurService editeurService;

    public RequeteAuthEditeurConsumer(EditeurService service) {
        this.editeurService = service; // On injecte le service ici

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
        RepAuthEditeurProducer repAuthEditeurProducer = new RepAuthEditeurProducer();
        try {
            consumer.subscribe(Collections.singletonList("nouvelle-connexion-editeur"));

            while (true) {
                ConsumerRecords<String, RequeteAuthentificationEditeur> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, RequeteAuthentificationEditeur> record : records) {
                    RequeteAuthentificationEditeur event = record.value();

                    //on récupère l'éditeur demandé
                    Editeur editeur = editeurService.getEditeur(event.getPseudo());
                    if(editeur != null && editeur.getMdp().equals(event.getMotDePasse())){
                        repAuthEditeurProducer.envoyerAuthentificationEditeur(editeur.getId());
                    }
                    else repAuthEditeurProducer.envoyerAuthentificationEditeur(-1);
                }
            }
        } finally {
            consumer.close();
        }
    }

}
