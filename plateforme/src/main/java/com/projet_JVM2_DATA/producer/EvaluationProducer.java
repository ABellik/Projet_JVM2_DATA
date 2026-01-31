package com.projet_JVM2_DATA.producer;

import com.example.events.EvaluationJeu;
import com.example.events.ReponseAuthentificationEditeur;
import com.projet_JVM2_DATA.entity.Bibliothèque;
import com.projet_JVM2_DATA.service.BibliothèqueService;
import com.projet_JVM2_DATA.service.JeuService;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;

public class EvaluationProducer {

    private final KafkaProducer<String, EvaluationJeu> producer;

    public EvaluationProducer() {
        Properties props = new Properties();
        // On utilise tes ports Docker (localhost:9092)
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // C'est ici qu'on utilise le Serializer Avro
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        this.producer = new KafkaProducer<>(props);
    }

    public void envoyer(Bibliothèque eval) {

        ProducerRecord<String, EvaluationJeu> record =
                new ProducerRecord<>("evaluations-jeu", null,
                        new EvaluationJeu(eval.getId().getIdUtilisateur(),eval.getIdJeu().getId(), eval.getNoteJoueur().intValue(), eval.getIdJeu().getVersionActuelle(), eval.getCommentaireJoueur(), Instant.now()
                                /*Long.parseLong(eval.getId().toString()), Integer.parseInt(eval.getNoteJoueur().toString()), "1.0", eval.getCommentaireJoueur(), Instant.parse(eval.getDateAchat().toString())*/
                        )
                );

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("✅ Message envoyé au topic " + metadata.topic() + " à l'offset " + metadata.offset());
            } else {
                System.err.println("❌ Erreur d'envoi : " + exception.getMessage());
            }
        });
    }
}
