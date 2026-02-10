package com.projet_JVM2_DATA.consumer;

import com.example.events.AjoutWishlist;
import com.projet_JVM2_DATA.service.*;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class AjoutWishlistConsumer implements Runnable {
    private final KafkaConsumer<String, AjoutWishlist> consumer;

    private final WishlistService wishlistService;
    private final LicenceService  licenceService;

    public AjoutWishlistConsumer(WishlistService wishlistService) {

        this.wishlistService = wishlistService;
        this.licenceService = new LicenceService();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "wishlist-group"+ UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("ajout-wishlist"));

            while (true) {
                ConsumerRecords<String, AjoutWishlist> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, AjoutWishlist> record : records) {
                    AjoutWishlist event = record.value();

                    java.time.Instant instantRecu = Instant.now();
                    LocalDate dateAjoutWishlist = instantRecu.atZone(ZoneId.systemDefault()).toLocalDate();

                    wishlistService.ajouterJeu(event.getIdJoueur(), licenceService.getIdPlateformeByIdJeu(event.getIdJeu()).getFirst(), event.getIdJeu(), dateAjoutWishlist);
                }
            }
        } finally {
            consumer.close();
        }
    }
}