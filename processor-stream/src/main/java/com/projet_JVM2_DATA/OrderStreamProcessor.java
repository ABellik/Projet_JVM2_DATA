package com.projet_JVM2_DATA;

import com.example.events.OrderPlaced;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class OrderStreamProcessor {

    public static void main(String[] args) {
        Properties props = new Properties();
        // ID unique de l'application (sert de Consumer Group ID interne)
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-filter-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Configuration par défaut : Clés en String, mais pour les Valeurs on spécifie plus bas
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // On ne met pas le Serde Avro par défaut ici pour garder la main,
        // mais on pourrait le faire via DEFAULT_VALUE_SERDE_CLASS_CONFIG.

        final String schemaRegistryUrl = "http://localhost:8081";

        // --- 1. Configuration du SerDe Avro Spécifique ---
        // C'est l'objet qui sait comment lire/écrire OrderPlaced
        final Serde<OrderPlaced> orderPlacedSerde = new SpecificAvroSerde<>();

        // On doit lui fournir l'URL du registry et dire "C'est un objet spécifique, pas générique"
        Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);
        orderPlacedSerde.configure(serdeConfig, false); // false = ce n'est pas une clé, c'est une valeur

        // --- 2. Construction de la Topologie ---
        StreamsBuilder builder = new StreamsBuilder();

        // Lecture du topic source
        // On force l'utilisation du Serde Avro créé juste au-dessus
        KStream<String, OrderPlaced> ordersStream = builder.stream(
                "orders",
                org.apache.kafka.streams.kstream.Consumed.with(Serdes.String(), orderPlacedSerde)
        );

        // Traitement : Filtrage (Montant > 100)
        KStream<String, OrderPlaced> highValueOrders = ordersStream
                .filter((key, order) -> {
                    System.out.println("Processing order: " + order.getOrderId() + " amount: " + order.getAmount());
                    return order.getAmount() > 100.0;
                });

        // Écriture vers le topic de sortie
        highValueOrders.to(
                "high-value-orders",
                Produced.with(Serdes.String(), orderPlacedSerde)
        );

        // --- 3. Démarrage ---
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);

        // --- CORRECTION : Le verrou pour empêcher l'arrêt ---
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        // Attache le hook pour éteindre proprement avec Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown(); // Libère le verrou
            }
        });

        try {
            streams.start();
            System.out.println("🚀 Stream démarré ! (En attente de messages...)");
            // Le programme va bloquer ICI indéfiniment jusqu'à l'arrêt manuel
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
