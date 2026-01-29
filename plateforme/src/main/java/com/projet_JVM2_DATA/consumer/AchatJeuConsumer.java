package com.projet_JVM2_DATA.consumer;

import com.example.events.AchatJeu;
import com.example.events.CreationCompteJoueur; // Ta classe générée par Avro
import com.projet_JVM2_DATA.service.BibliothèqueService;
import com.projet_JVM2_DATA.service.LicenceService;
import com.projet_JVM2_DATA.service.PlateformeService;
import com.projet_JVM2_DATA.service.UtilisateurService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import org.postgresql.shaded.com.ongres.scram.common.util.AbstractCharAttributeValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;

public class AchatJeuConsumer implements Runnable {
    private final KafkaConsumer<String, AchatJeu> consumer;
    private final LicenceService licenceService;
    private final BibliothèqueService bibliothèqueService;
    private final PlateformeService plateformeService;

    public AchatJeuConsumer(UtilisateurService service) {
        this.licenceService = new LicenceService();
        this.bibliothèqueService = new BibliothèqueService();
        this.plateformeService = new PlateformeService();

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
            consumer.subscribe(Collections.singletonList("achat-jeu"));

            while (true) {
                ConsumerRecords<String, AchatJeu> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, AchatJeu> record : records) {
                    AchatJeu event = record.value();

                    java.time.Instant instantRecu = event.getDateAchat();
                    LocalDate dateAchat = instantRecu.atZone(ZoneId.systemDefault()).toLocalDate();

                    bibliothèqueService.ajouterJeu(
                            event.getIdJoueur(),
                            plateformeService.getPlateformeByNom(event.getSupport()).getId(),
                            event.getIdJeu(),
                            dateAchat,
                            event.getPrixPaye()
                    );
                }
            }
        } finally {
            consumer.close();
        }
    }
}