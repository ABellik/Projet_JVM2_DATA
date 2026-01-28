package com.projet_JVM2_DATA.kafka;

import com.example.events.CreationPatch;
import com.example.events.typeModification;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;

import java.time.Instant;
import java.util.Properties;

public class PatchProducer {

    private final Producer<String, CreationPatch> producer;
    private final String topic;

    public PatchProducer(String bootstrapServers, String schemaRegistryUrl, String topic) {
        this.topic = topic;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);

        this.producer = new KafkaProducer<>(props);
    }

    public void sendPatch(long idJeu,
                          String versionProblematique,
                          String nouvelleVersion,
                          String support,
                          String commentaire,
                          typeModification modification) {

        long now = System.currentTimeMillis();

        CreationPatch patch = CreationPatch.newBuilder()
                .setId(now)  // id unique
                .setIdJeu(idJeu)
                .setVersionProblematique(versionProblematique)
                .setNouvelleVersion(nouvelleVersion)          // ⚠️ correspond à "NouvelleVersion"
                .setCommentaireEditeur(commentaire)
                .setModification(modification)
                .setSupport(support)
                .setDatePublication(Instant.now())                      // ⚠️ correspond à "DatePublication"
                .build();

        ProducerRecord<String, CreationPatch> record =
                new ProducerRecord<>(topic, String.valueOf(idJeu), patch);

        producer.send(record, (meta, ex) -> {
            if (ex != null) ex.printStackTrace();
            else System.out.println("Patch publié: topic=" + meta.topic()
                    + " partition=" + meta.partition()
                    + " offset=" + meta.offset());
        });
    }

    public void close() {
        producer.flush();
        producer.close();
    }
}
