package com.projet_JVM2_DATA.consumer;

import com.example.events.ReponseAuthentificationJoueur;
import com.example.events.RequeteAuthentificationJoueur;
import com.projet_JVM2_DATA.entity.Utilisateur;
import com.projet_JVM2_DATA.producer.ReponseAuthentificationJoueurProducer;
import com.projet_JVM2_DATA.service.JeuService;
import com.projet_JVM2_DATA.service.UtilisateurService;
import com.projet_JVM2_DATA.service.WishlistService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class RequeteAuthentificationJoueurConsumer implements Runnable {
    private final KafkaConsumer<String, RequeteAuthentificationJoueur> consumer;
    private final UtilisateurService utilisateurService;

    private final ReponseAuthentificationJoueurProducer producer = new ReponseAuthentificationJoueurProducer();

    public RequeteAuthentificationJoueurConsumer(UtilisateurService service) {
        this.utilisateurService = service;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "requete-authentification-joueur-group"+UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList("requete-authentification-joueur"));

            while (true) {
                ConsumerRecords<String, RequeteAuthentificationJoueur> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, RequeteAuthentificationJoueur> record : records) {
                    RequeteAuthentificationJoueur event = record.value();

                    Utilisateur u = utilisateurService.getUtilisateurByPseudo(event.getPseudo());

                    List<Long> gamesList = new ArrayList<>();
                    List<Long> wishlistList = new ArrayList<>();

                    ReponseAuthentificationJoueur rep;

                    if (u == null) {
                        rep = new ReponseAuthentificationJoueur(
                                null, null, null, null, event.getPseudo(), null, null, null,
                                gamesList,
                                wishlistList
                        );
                    } else {
                        List<Long> foundGames = new JeuService().getJeuByPseudo(u.getPseudo());
                        if (foundGames != null) {
                            gamesList = foundGames;
                        }

                        List<Long> foundWishlist = new WishlistService().getWishlistByIdUtilisateur(u.getId());
                        if (foundWishlist != null) {
                            wishlistList = foundWishlist;
                        }

                        rep = new ReponseAuthentificationJoueur(
                                u.getId(),
                                u.getNom(),
                                u.getPrenom(),
                                u.getPseudo(),
                                u.getMail(),
                                u.getMdp(),
                                u.getDateNaissance().toString(),
                                u.getDateInscription().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                gamesList,
                                wishlistList
                        );
                    }

                    producer.envoyerReponseAuthentificationJoueur(rep);
                }
            }
        } finally {
            consumer.close();
        }
    }
}
