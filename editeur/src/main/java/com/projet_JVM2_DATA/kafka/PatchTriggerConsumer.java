package com.projet_JVM2_DATA.kafka;

import com.example.events.typeModification;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PatchTriggerConsumer {

    private final Consumer<String, Long> consumer;
    private final PatchProducer patchProducer;

    public PatchTriggerConsumer(String bootstrapServers,
                                String schemaRegistryUrl,
                                String patchTopic) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "editeur-patch-trigger");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.consumer = new KafkaConsumer<>(props);
        this.patchProducer = new PatchProducer(
                bootstrapServers,
                schemaRegistryUrl,
                patchTopic
        );
    }

    public void start(String triggerTopic) {
        consumer.subscribe(Collections.singletonList(triggerTopic));
        System.out.println("PatchTriggerConsumer démarré, en attente de triggers...");

        try {
            while (true) {
                ConsumerRecords<String, Long> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, Long> record : records) {

                    String key = record.key();      // idJeu:version:support
                    Long count = record.value();    // 10, 20, 30...

                    if (count == null || count % 10 != 0) continue;

                    String[] parts = key.split(":");
                    long idJeu = Long.parseLong(parts[0]);
                    String versionProblematique = parts[1];
                    String support = parts[2];

                    String nouvelleVersion = bumpVersion(versionProblematique, count);

                    String commentaire = "Patch automatique après "
                            + count + " crashs sur support " + support;

                    System.out.println("Création patch jeu=" + idJeu
                            + " version=" + versionProblematique
                            + " -> " + nouvelleVersion
                            + " support=" + support
                            + " crashs=" + count);

                    patchProducer.sendPatch(
                            idJeu,
                            versionProblematique,
                            nouvelleVersion,
                            support,
                            commentaire,
                            typeModification.CORRECTION
                    );
                }
            }
        } finally {
            consumer.close();
            patchProducer.close();
        }
    }

    // Incrémente version selon le nombre de patchs
    // ex: 1.0.0 -> 1.0.1 -> 1.0.2 ...
    private String bumpVersion(String base, long count) {
        try {
            String[] parts = base.split("\\.");
            int last = Integer.parseInt(parts[parts.length - 1]);
            int patchNumber = (int) (count / 10);
            parts[parts.length - 1] = String.valueOf(last + patchNumber);
            return String.join(".", parts);
        } catch (Exception e) {
            return base + "." + (count / 10);
        }
    }
}
