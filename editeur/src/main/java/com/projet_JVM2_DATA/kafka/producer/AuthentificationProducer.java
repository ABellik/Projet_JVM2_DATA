package com.projet_JVM2_DATA.kafka.producer;

import com.example.events.PublicationJeuOuDLC;
import com.example.events.RequeteAuthentificationEditeur;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.SQLException;
import java.util.Properties;

/*
 *Cette classe permet de produire des évènements contenant les pseudo et mots
 * de passe d'un editeur pour faire une demande de connexion à la plateforme
 *
 * */

public class AuthentificationProducer {

    private final Producer<String, RequeteAuthentificationEditeur> producer;
    private final String topic;

    public AuthentificationProducer(String bootstrapServers, String schemaRegistryUrl, String topic) {
        this.topic = topic;
        //Configuration pour la Sérialisation + vérification de conformité des données par rapport au schéma avro
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // Configuration pour éviter de perdre des messages en cas de kill brutal
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        producer = new KafkaProducer<>(props);
    }

    public void envoyer(String []args) {

            RequeteAuthentificationEditeur requete = new RequeteAuthentificationEditeur(args[0], args[1]);

            ProducerRecord<String, RequeteAuthentificationEditeur> record = new ProducerRecord<>("nouvelle-connexion-editeur", null, requete);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf(" requête publiée : pseudo=%s , mdp=%d ",
                            requete.getPseudo(),
                            requete.getMotDePasse());
                } else {
                    System.err.println("❌ Erreur d'envoi : " + exception.getMessage());
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    public void close() {
        producer.flush();
        producer.close();
    }


        }





