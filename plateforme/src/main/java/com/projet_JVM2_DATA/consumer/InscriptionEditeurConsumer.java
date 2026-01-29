package com.projet_JVM2_DATA.consumer;

import com.example.events.CompteEditeur;
import com.example.events.CreationCompteJoueur;
import com.projet_JVM2_DATA.entity.Editeur;
import com.projet_JVM2_DATA.service.EditeurService;
import com.projet_JVM2_DATA.service.UtilisateurService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;

public class InscriptionEditeurConsumer {

    private final KafkaConsumer<String, CompteEditeur> consumer;
    private final EditeurService editeurService;

    public InscriptionEditeurConsumer(EditeurService service) {
        this.editeurService = service; // On injecte le dao ici

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inscription-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);
    }


    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("creation-compte-joueur"));

            while (true) {
                ConsumerRecords<String, CompteEditeur> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, CompteEditeur> record : records) {
                    CompteEditeur event = record.value();

                    String type;
                    if (event.getDateDeNaissance()==null)
                    {
                        type="independant";
                    }
                    else{
                        type="entreprise";
                    }

                    // APPEL DU SERVICE
                    editeurService.creer(
                            type, event.getNom(), event.getMotDePasse(), event.getEmail()
                    );
                }
            }
        } finally {
            consumer.close();
        }
    }
}
