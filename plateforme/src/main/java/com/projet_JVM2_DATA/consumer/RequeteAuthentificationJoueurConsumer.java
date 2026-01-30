/*package com.projet_JVM2_DATA.consumer;

import com.example.events.ReponseAuthentificationJoueur;
import com.example.events.RequeteAuthentificationJoueur; // Ta classe générée par Avro
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
import java.util.Collections;
import java.util.Properties;

public class RequeteAuthentificationJoueurConsumer implements Runnable {
    private final KafkaConsumer<String, RequeteAuthentificationJoueur> consumer;
    private final UtilisateurService utilisateurService;

    public RequeteAuthentificationJoueurConsumer(UtilisateurService service) {
        this.utilisateurService = service; // On injecte le service ici

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
            consumer.subscribe(Collections.singletonList("requete-authentification-joueur"));

            while (true) {
                ConsumerRecords<String, RequeteAuthentificationJoueur> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, RequeteAuthentificationJoueur> record : records) {
                    RequeteAuthentificationJoueur event = record.value();

                    // APPEL DU SERVICE
                    Utilisateur u = utilisateurService.getUtilisateurByPseudo(event.getPseudo());
                    if(u == null){
                        ReponseAuthentificationJoueur rep = new ReponseAuthentificationJoueur(null,null,null,null,null,null,null,null,null,null);
                        ReponseAuthentificationJoueurProducer rajp = new ReponseAuthentificationJoueurProducer();
                        rajp.envoyerReponseAuthentificationJoueur(rep);
                    }
                    else{
                        ReponseAuthentificationJoueur rep = new ReponseAuthentificationJoueur(u.getId(),u.getNom(),u.getPrenom(), u.getPseudo(), u.getMail(), u.getMdp(), u.getDateNaissance().toString(), u.getDateInscription().atStartOfDay(ZoneId.systemDefault()).toInstant(),new JeuService().getJeuByPseudo(u.getPseudo()), new WishlistService().getWishlistByIdUtilisateur(u.getId()));
                        ReponseAuthentificationJoueurProducer rajp = new ReponseAuthentificationJoueurProducer();
                        rajp.envoyerReponseAuthentificationJoueur(rep);
                    }


                }
            }
        } finally {
            consumer.close();
        }
    }
}*/


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
import java.util.ArrayList; // <--- IMPORT INDISPENSABLE
import java.util.Collections;
import java.util.List;      // <--- IMPORT INDISPENSABLE
import java.util.Properties;
import java.util.UUID;

public class RequeteAuthentificationJoueurConsumer implements Runnable {
    private final KafkaConsumer<String, RequeteAuthentificationJoueur> consumer;
    private final UtilisateurService utilisateurService;
    // On peut instancier le producer une seule fois pour optimiser
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

                    // APPEL DU SERVICE
                    Utilisateur u = utilisateurService.getUtilisateurByPseudo(event.getPseudo());

                    // Préparation des listes vides par sécurité pour Avro
                    List<Long> gamesList = new ArrayList<>();
                    List<Long> wishlistList = new ArrayList<>();

                    ReponseAuthentificationJoueur rep;

                    if (u == null) {
                        // CAS 1 : UTILISATEUR INCONNU
                        // On doit quand même envoyer des listes vides [] et pas null pour les tableaux
                        rep = new ReponseAuthentificationJoueur(
                                null, null, null, null, null, null, null, null,
                                gamesList,    // IMPORTANT : Pas null !
                                wishlistList  // IMPORTANT : Pas null !
                        );
                    } else {
                        // CAS 2 : UTILISATEUR TROUVÉ

                        // Sécurisation de la liste de Jeux
                        List<Long> foundGames = new JeuService().getJeuByPseudo(u.getPseudo());
                        if (foundGames != null) {
                            gamesList = foundGames;
                        }

                        // Sécurisation de la Wishlist
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
                                gamesList,    // Liste sécurisée (soit remplie, soit vide, jamais null)
                                wishlistList  // Liste sécurisée
                        );
                    }

                    // Envoi
                    producer.envoyerReponseAuthentificationJoueur(rep);
                }
            }
        } finally {
            consumer.close();
        }
    }
}
