package com.projet_JVM2_DATA.producer;



import com.example.events.ReponseAuthentificationEditeur;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class TestAuthEditeurProducer {

    public static void main(String[] args) {

        // 1) Configuration identique à ton DLCProducer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, ReponseAuthentificationEditeur> producer = new KafkaProducer<>(props)) {

            // 2) Génération d'une liste de faux comptes
            List<ReponseAuthentificationEditeur> comptesFictifs = genererComptesDeTest(5);

            System.out.println("🚀 Lancement du test : Envoi de " + comptesFictifs.size() + " comptes fictifs...");

            for (ReponseAuthentificationEditeur compte : comptesFictifs) {

                // Utilisation du pseudo comme clé pour le partitionnement
                String key = compte.getPseudo();

                ProducerRecord<String, ReponseAuthentificationEditeur> record =
                        new ProducerRecord<>("reponse-requete-authentification", key, compte);

                // 3) Envoi asynchrone avec Callback
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf(" ✅ Compte Mock envoyé : ID=%d | Pseudo=%s | Topic=%s | Partition=%d%n",
                                compte.getIdEditeur(),
                                compte.getPseudo(),
                                metadata.topic(),
                                metadata.partition());
                    } else {
                        System.err.println("❌ Erreur de production : " + exception.getMessage());
                    }
                });

                // Petite pause pour simuler un flux
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            producer.flush();
            System.out.println("🏁 Fin de l'envoi des données de test.");
        }
    }

    /**
     * Crée des objets factices basés sur ton schéma Avro
     */
    private static List<ReponseAuthentificationEditeur> genererComptesDeTest(int nombre) {
        List<ReponseAuthentificationEditeur> list = new ArrayList<>();
        Random random = new Random();

        String[] noms = {"Durand", "Lefebvre", "Moreau", "Petit", "Rousseau"};
        String[] pseudos = {"ShadowDev", "PixelMaster", "GameMaker99", "IndieHero", "BugHunter"};

        for (int i = 0; i < nombre; i++) {
            ReponseAuthentificationEditeur auth = ReponseAuthentificationEditeur.newBuilder()
                    .setIdEditeur((long) 1000 + i)
                    .setNom(noms[i % noms.length])
                    .setPrenom("TestUser" + i)
                    .setEmail("user" + i + "@test-editeur.com")
                    .setPseudo(pseudos[i % pseudos.length])
                    .setMotDePasse("hash_secret_" + random.nextInt(10000))
                    .setDateDeNaissance("1990-05-12")
                    .setDateDeCreationDuCompte(Instant.now()) // Gère le timestamp-millis
                    .build();
            list.add(auth);
        }
        return list;
    }
}
