package com.projet_JVM2_DATA.consumer;

import com.example.events.CreationCompteJoueur; // Ta classe générée par Avro
import com.projet_JVM2_DATA.service.UtilisateurService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;

public class InscriptionJoueurConsumer implements Runnable {
    private final KafkaConsumer<String, CreationCompteJoueur> consumer;
    private final UtilisateurService utilisateurService;

    public InscriptionJoueurConsumer(UtilisateurService service) {
        this.utilisateurService = service; // On injecte le service ici

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
            consumer.subscribe(Collections.singletonList("creation-compte-joueur"));

            while (true) {
                ConsumerRecords<String, CreationCompteJoueur> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, CreationCompteJoueur> record : records) {
                    CreationCompteJoueur event = record.value();

                    LocalDate dateNais = LocalDate.parse(event.getDateDeNaissance());

                    java.time.Instant instantRecu = event.getDateDeCreationDuCompte();
                    LocalDate dateCreationCompte = instantRecu.atZone(ZoneId.systemDefault()).toLocalDate();

                    utilisateurService.inscription(
                            event.getPrenom(),
                            event.getNom(),
                            event.getPseudo(),
                            event.getEmail(),
                            dateNais,
                            event.getMotDePasse(),
                            dateCreationCompte
                    );
                }
            }
        } finally {
            consumer.close();
        }
    }
}