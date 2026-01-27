package com.projet_JVM2_DATA.producer;

import com.example.events.PublicationJeuOuDLC;
import com.projet_JVM2_DATA.service.JeuOuDLCDAO;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class JeuProducer {



        public static void main() throws SQLException
        {
            //Configuration pour la Sérialisation + vérification de conformité des données par rapport au schéma avro
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
            // Configuration pour éviter de perdre des messages en cas de kill brutal
            props.put(ProducerConfig.ACKS_CONFIG, "all");


            try (KafkaProducer<String, PublicationJeuOuDLC> producer = new KafkaProducer<>(props)) {

                String url = System.getenv("DB_URL");
                String username = System.getenv("DB_USER");
                String password = System.getenv("DB_PASSWORD");

                //permettre d'effectuer les requêtes à la base pour récupérer le dlc associé au jeu
                JeuOuDLCDAO jeuOuDLCDAO = new JeuOuDLCDAO(url, username,password);

                List<PublicationJeuOuDLC> jeux = jeuOuDLCDAO.recuperationDLCOuJeu();
                for (PublicationJeuOuDLC jeu : jeux)
                {

                    jeuOuDLCDAO.retirerJeuEnPublication(jeu.getId());


                    ProducerRecord<String, PublicationJeuOuDLC> record = new ProducerRecord<>("nouveau-jeu", null, jeu);


                    producer.send(record, (metadata, exception) -> {
                        if (exception == null) {
                            System.out.printf(" Jeu publié : id=%d , Nom=%s , Date=%s ",
                                    jeu.getId(),
                                    jeu.getNom(),
                                    jeu.getDate().toString());
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


                //5) Envoie de tous les messages en attente dans le buffer
                //Vide l'ensemble des messages vers Kafka, mais garde le Producer ouvert pour d'autres messages alors qu'avec close il aurait été fermé
                producer.flush();
                System.out.println("🏁 Fin de l'envoi des jeux publiés.");
            }



        }
    }





