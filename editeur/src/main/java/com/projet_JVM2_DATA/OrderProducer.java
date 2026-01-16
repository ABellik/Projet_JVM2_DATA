package com.projet_JVM2_DATA;

import com.example.events.OrderPlaced;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class OrderProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        // Configuration pour éviter de perdre des messages en cas de kill brutal
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, OrderPlaced> producer = new KafkaProducer<>(props)) {

            Random random = new Random();

            // --- BOUCLE DE GÉNÉRATION ---
            // On va produire 10 commandes différentes
            for (int i = 0; i < 10; i++) {

                // 1. Générer des données variables
                // Montant aléatoire entre 10.0 et 200.0 (pour avoir des cas < 100 et > 100)
                double amount = 10 + (190 * random.nextDouble());
                String userId = "user_" + random.nextInt(5); // Simule 5 utilisateurs différents

                // 2. Création de l'évènement
                OrderPlaced event = OrderPlaced.newBuilder()
                        .setOrderId(UUID.randomUUID().toString())
                        .setUserId(userId)
                        .setAmount(amount)
                        .setTimestamp(Instant.now().toEpochMilli())
                        .build();

                // 3. Création du record
                ProducerRecord<String, OrderPlaced> record = new ProducerRecord<>("orders", event.getOrderId(), event);

                // 4. Envoi asynchrone
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf("✅ Envoyé : User=%s | Montant=%.2f€ | Partition=%d%n",
                                event.getUserId(), event.getAmount(), metadata.partition());
                    } else {
                        System.err.println("❌ Erreur d'envoi : " + exception.getMessage());
                    }
                });

                // Petite pause pour voir les logs défiler proprement (facultatif)
                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            }

            producer.flush();
            System.out.println("🏁 Fin de l'envoi du batch de commandes.");
        }
    }
}