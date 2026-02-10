package com.projet_JVM2_DATA.consumer;

import com.example.events.EvaluationJeu;

import com.projet_JVM2_DATA.service.BibliothèqueService;
import com.projet_JVM2_DATA.service.LicenceService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class EvaluationJeuConsumer implements Runnable {
    private final KafkaConsumer<String, EvaluationJeu> consumer;
    private final BibliothèqueService evaluationService;

    public EvaluationJeuConsumer(BibliothèqueService service) {
        this.evaluationService = service;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "evaluation-jeu-group"+ UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("evaluation-jeu"));

            while (true) {
                ConsumerRecords<String, EvaluationJeu> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, EvaluationJeu> record : records) {
                    EvaluationJeu event = record.value();

                    evaluationService.evaluationJeu(event.getIdJoueur(),new LicenceService().getIdPlateformeByIdJeu(event.getIdJeu()).getFirst(),event.getIdJeu(), event.getNote(),event.getCommentaire());

                    //Long idUtilisateur, Long idPlateforme, Long idJeu, int note, String commentaire
                    /*evaluationService.evaluationJeu(
                            null,null, event.getIdJeu(), event.getVersionJeu(), event.getIdJeu(), event.getNote(), event.getCommentaire()
                    );*/

                }
            }
        } finally {
            consumer.close();
        }
    }
}
