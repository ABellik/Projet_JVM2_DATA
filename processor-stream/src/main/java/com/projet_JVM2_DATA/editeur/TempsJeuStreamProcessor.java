package com.projet_JVM2_DATA.editeur;

import com.example.events.CalculateurMoyenne;
import com.example.events.Session;
import com.projet_JVM2_DATA.editeur.dao.JeuOuDLCDAO;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/*
Dans cette classe, on calule la moyenne des temps de jeu globaux sur 300 sessions par jeu.
Une fois cette moyenne calculée, on regarde le nombre de sessions dont le temps
de jeu est supérieur à cette celle-ci.
Si plus de la moitié des sessions ont été repertoriées, on publie les jeux
possédant un genre commun avec celui dont on a calculé la moyenne au dévut
*/

public class TempsJeuStreamProcessor {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(" !!!!!!!!!!!  KafkaStreamApp");
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "temps-jeu-filter-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        final String schemaRegistryUrl = "http://localhost:8081";


        final Serde<Session> sessionSerde = new SpecificAvroSerde<>();


        sessionSerde.configure(Map.of("schema.registry.url", schemaRegistryUrl), false);



        StreamsBuilder builder = new StreamsBuilder();
        JeuOuDLCDAO service = new JeuOuDLCDAO(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        );

        System.out.println("!!!!!!!!!!!  Toutes les configuration de base valides");

        KStream<String, Session> sessionStream = builder.stream(
                "nouvelles-session",
                org.apache.kafka.streams.kstream.Consumed.with(Serdes.String(), sessionSerde)
        );


        final Serde<CalculateurMoyenne> calculateurMoyenneSerde = new SpecificAvroSerde<>();
        calculateurMoyenneSerde.configure(Map.of("schema.registry.url", schemaRegistryUrl), false);



        System.out.println("!!!!!!!!!!!  Début de la création de la KTable de calcul de moyenne");


        KTable<String, CalculateurMoyenne> tableDecisionnelleNotesJeu = sessionStream
                .selectKey((cleKafka, session) -> String.valueOf(session.getIdJeu()))
                .groupByKey(Grouped.with(Serdes.String(), sessionSerde))
                .aggregate(
                        CalculateurMoyenne::new,
                        (cle, session, calculateurMoyenne) -> {


                            calculateurMoyenne.setNbValeurs(calculateurMoyenne.getNbValeurs() + 1);
                            System.out.println("\n Nombre de session : " + calculateurMoyenne.getNbValeurs()
                                    + "pour le jeu : " + session.getIdJeu() + "\n");


                            long tpsJeu = session.getHeureDeFin().toEpochMilli()-session.getHeureDeDebut().toEpochMilli();
                            calculateurMoyenne.setSomme(calculateurMoyenne.getSomme()+tpsJeu);
                            System.out.println("Somme : " + calculateurMoyenne.getSomme() + "\n");

                            calculateurMoyenne.getListeTempsTemporaire().add(tpsJeu);


                            if (calculateurMoyenne.getNbValeurs() % 300 == 0 ) {

                                double moyenne = (calculateurMoyenne.getNbValeurs() > 0) ?
                                        (double) calculateurMoyenne.getSomme() / calculateurMoyenne.getNbValeurs() : 0.0;

                                logger.info("!!!!!!!!!!! Moyenne calculée de : {} , sur le jeu : {}", moyenne, session.getIdJeu());

                                long nbSupMoyenne = calculateurMoyenne.getListeTempsTemporaire().stream()
                                        .filter(t -> t > moyenne)
                                        .count();

                                if(nbSupMoyenne> calculateurMoyenne.getNbValeurs()/2)
                                {
                                    calculateurMoyenne.setDlcPublie(true);
                                    System.out.println(
                                            "Un jeu du même genre sera publié : " + session.getIdJeu()
                                    );
                                }
                                //Nettoyage pour éviter les conflits dans les caluls
                                calculateurMoyenne.setNbValeurs(0);
                                calculateurMoyenne.setSomme(0);



                            } else {
                                // on remet à false si on n'est pas pile sur le palier
                                // pour que le filtre du Stream ne laisse passer l'info qu'une seule fois
                                calculateurMoyenne.setDlcPublie(false);



                            }


                            return calculateurMoyenne;
                        },
                        Materialized.<String, CalculateurMoyenne, KeyValueStore<Bytes, byte[]>>as("stats-decision-publication-DLC")//stocker localement un état : ici c'est l choix de publier ou non
                                .withKeySerde(Serdes.String())//serde de la clé
                                .withValueSerde(calculateurMoyenneSerde)//serde de la valeur
                );




        //map permet de transformer chaque record d'un stream
        // Publication d'un DLC si : isPublie à true
        tableDecisionnelleNotesJeu.toStream()

                .filter((jeuId, stats) -> stats.getDlcPublie())
                .foreach((jeuId, stats) -> {
                    try {

                        if(stats.getNbValeursSupMoyenne()>stats.getNbValeurs()/2)
                        {
                            long idJeuLong = Long.parseLong(jeuId);
                            //récupère les genres associés au jeu dont l'id est : idJeuLong
                            //puis parcours la base de données et mets le champs enPublication à true
                            //pour tous les jeux dont possédant un genre en commun avec celui-ci
                            service.mettreAutresJeuEnPublication(idJeuLong);
                        }

                        logger.info("!!!!!!!!! BDD mise à jour : Ce jeu :  {} mis à 'enPublication = true'", jeuId);
                    } catch (Exception e) {
                        logger.error("!!!!!!!Erreur lors de la mise à jour BDD pour le jeu {} : {}", jeuId, e.getMessage());
                    }
                });


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
            streams.cleanUp();
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


