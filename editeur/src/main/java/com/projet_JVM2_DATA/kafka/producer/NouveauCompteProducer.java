package com.projet_JVM2_DATA.kafka.producer;

import com.example.events.CompteEditeur;
import com.projet_JVM2_DATA.dao.JeuOuDlcDao;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Properties;

public class NouveauCompteProducer {

    public static void main(String[] args) throws SQLException
    {
        //Configuration pour la Sérialisation + vérification de conformité des données par rapport au schéma avro
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // Configuration pour éviter de perdre des messages en cas de kill brutal
        props.put(ProducerConfig.ACKS_CONFIG, "all");


        try (KafkaProducer<String, CompteEditeur> producer = new KafkaProducer<>(props)) {

            String url = System.getenv("DB_URL");
            String username = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            //permettre d'effectuer les requêtes à la base pour récupérer le dlc associé au jeu
            JeuOuDlcDao jeuOuDLCDAO = new JeuOuDlcDao(url, username,password);


            CompteEditeur compte= new CompteEditeur(
                    Long.parseLong(args[0]), args[1], args[2], args[3], args[4], args[5], args[6],Instant.now()
            );

            ProducerRecord<String, CompteEditeur> record = new ProducerRecord<>("nouveau-compte-editeur", null,compte );

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf(" Nouveau compte : Prenom=%s, Nom=%s , Date=%d%n ",
                            compte.getPrenom(),
                            compte.getNom(),
                            compte.getDateDeCreationDuCompte());

                    } else {
                        System.err.println("❌ Erreur d'envoi : " + exception.getMessage());
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                producer.flush();
                System.out.println("🏁 Fin de l'envoi des données du nouveau compte.");

            }

        }



    }
