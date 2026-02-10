package com.projet_JVM2_DATA.kafka.producer;

import com.example.events.PublicationJeuOuDLC;
//import com.projet_JVM2_DATA.dao.JeuOuDlcDao;
import com.projet_JVM2_DATA.dao.JeuOuDlcDao;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.*;
import java.util.List;
import java.util.Properties;


/*
 *Cette classe permet de produire des dlcs
 *
 * */


public class DLCProducer {

    private final Producer<String, PublicationJeuOuDLC> producer;
    private final String topic;

    public DLCProducer(String bootstrapServers, String schemaRegistryUrl, String topic) {

        this.topic =topic;

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

    public void envoyer() {


        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        //permettre d'effectuer les requêtes à la base pour récupérer le dlc associé au jeu
        JeuOuDlcDao jeuOuDLCDAO = new JeuOuDlcDao(url, username, password);

        List<PublicationJeuOuDLC> dlcs = jeuOuDLCDAO.recuperationDLCOuJeu();
        for (PublicationJeuOuDLC dlc : dlcs) {


            //Eviter qu'on publie le même DLC plusieurs fois
            jeuOuDLCDAO.retirerDLCEnPublication(dlc.getId());


            //Record généré associé à l'évènement
            ProducerRecord<String, PublicationJeuOuDLC> record = new ProducerRecord<>("nouveau-dlc", null, dlc);
            //la clé représente la partition du topic dans laquelle se trouvera le message


            //Envoi asynchrone
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf(" DLC publié : id=%d , Nom=%s , Date=%s ",
                            dlc.getId(),
                            dlc.getNom(),
                            dlc.getDate().toString());
                } else {
                    System.err.println("Erreur d'envoi : " + exception.getMessage());
                }
            });

            // 4) Petite pause pour voir les logs défiler proprement (facultatif)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    public void close()
    {
        producer.flush();
        producer.close();
    }

    }



