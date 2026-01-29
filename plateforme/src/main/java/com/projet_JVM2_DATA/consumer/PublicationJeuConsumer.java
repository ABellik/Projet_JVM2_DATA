package com.projet_JVM2_DATA.consumer;

import com.example.events.PublicationJeuOuDLC;
import com.projet_JVM2_DATA.entity.Plateforme;
import com.projet_JVM2_DATA.entity.TypeJeu;
import com.projet_JVM2_DATA.service.JeuService;
import com.projet_JVM2_DATA.service.LicenceService;
import com.projet_JVM2_DATA.service.PlateformeService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PublicationJeuConsumer implements Runnable{
    private final KafkaConsumer<String, PublicationJeuOuDLC> consumer;
    private final JeuService jeuService;
    private final LicenceService licenseService;
    private final PlateformeService plateformeService;

    public PublicationJeuConsumer(JeuService service){
        this.jeuService = service; // On injecte le dao ici
        this.licenseService = new LicenceService();
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
            consumer.subscribe(Collections.singletonList("nouveau-jeu"));

            while (true) {
                ConsumerRecords<String, PublicationJeuOuDLC> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, PublicationJeuOuDLC> record : records) {
                    PublicationJeuOuDLC event = record.value();

                    //Enregistrement du jeu dans la table Jeu (genres inclus)
                    Long idJeu = jeuService.publier(
                            event.getIdEditeur(),
                            event.getNom(),
                            event.getVersionActuelle(),
                            (long) event.getPrixEditeur(),
                            event.getIdParent(),
                            TypeJeu.BASE ,
                            event.getGenre());
                    if(idJeu != null){
                        licenseService.creerLicencesPourJeu(idJeu, event.getSupport());
                    }

                    if (event.getSupport() != null && !event.getSupport().isEmpty()) {
                        licenseService.creerLicencesPourJeu(idJeu, event.getSupport());
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }
}
