package com.projet_JVM2_DATA.editeur;

import com.example.events.CalculateurMoyenne;
import com.example.events.EvaluationJeu;
import com.projet_JVM2_DATA.editeur.service.JeuOuDLCDAO;
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

public class EvaluationJeuStreamProcessor {

    public static void main(String[] args)
    {

        Logger logger = LoggerFactory.getLogger(" !!!!!!!!!!!  KafkaStreamApp");
        Properties props = new Properties();
        // ID unique de l'application (sert de Consumer Group ID interne)
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "evaluation-filter-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Configuration par défaut : Clés en String, mais pour les Valeurs on spécifie plus bas
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // On ne met pas le Serde Avro par défaut ici pour garder la main,
        // mais on pourrait le faire via DEFAULT_VALUE_SERDE_CLASS_CONFIG.

        final String schemaRegistryUrl =  "http://localhost:8081";

        // --- 1. Configuration du SerDe Avro Spécifique ---
        // C'est notre propre Serde Avro pour notre cas pour la valeur
        final Serde<EvaluationJeu> evaluationSerde = new SpecificAvroSerde<>();

        // On doit lui fournir l'URL du registry et dire "C'est un objet spécifique, pas générique"
        evaluationSerde.configure(Map.of("schema.registry.url", schemaRegistryUrl), false);


        // --- 2. Construction de la Topologie ---
        StreamsBuilder builder = new StreamsBuilder();
        JeuOuDLCDAO service = new JeuOuDLCDAO(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        );

        System.out.println("!!!!!!!!!!!  Toutes les configuration de base valides");

        // Lecture du topic source
        // On force l'utilisation du Serde Avro créé juste au-dessus
        KStream<String, EvaluationJeu> evaluationStream = builder.stream(
                "evaluations-jeu",
                org.apache.kafka.streams.kstream.Consumed.with(Serdes.String(), evaluationSerde)
        );

        // On crée un Serde pour stocker temporairement le nombre de d'avaluations et leur somme
        //pour un calcul de moyenne ensuite
        final Serde<CalculateurMoyenne> calculateurMoyenneSerde = new SpecificAvroSerde<>();
        calculateurMoyenneSerde.configure(Map.of("schema.registry.url", schemaRegistryUrl), false);


        // Stockage : Toutes les évaluations en base de données
        evaluationStream.peek((key, evaluation) -> {
            service.stockerEvaluationsJeu(
                    evaluation.getIdJeu(), evaluation.getNote(),
                    evaluation.getVersionJeu(), evaluation.getCommentaire(),
                    evaluation.getDateEvaluationJeu().toEpochMilli()
            );
        });

        System.out.println("!!!!!!!!!!!  Début de la création de la KTable de calcul de moyenne");

        //Filtrage : KTable avec la moyenne des notes vallant plus de 3 étoiles
        KTable<String, CalculateurMoyenne> tableDecisionnelleNotesJeu = evaluationStream
                .selectKey((cleKafka, eval) -> String.valueOf(eval.getIdJeu()))
                .groupByKey(Grouped.with(Serdes.String(), evaluationSerde))
                .aggregate(
                        CalculateurMoyenne::new,
                        (cle, eval, calculateurMoyenne) -> {


                            calculateurMoyenne.setNbValeurs(calculateurMoyenne.getNbValeurs() + 1);
                            System.out.println("\n Nombre de valeur : "+ calculateurMoyenne.getNbValeurs()
                            + "pour le jeu : "+ eval.getIdJeu()+"\n");

                            calculateurMoyenne.setSomme(calculateurMoyenne.getSomme() + eval.getNote());
                            System.out.println("Somme : "+ calculateurMoyenne.getSomme()+"\n");

                            //Si le nombre de valeurs est strictement positif on peut entamer
                            //le calcul de la moyenne sinon on définit la moyenne à 0
                            double moyenne = (calculateurMoyenne.getNbValeurs() > 0) ?
                                    (double) calculateurMoyenne.getSomme() / calculateurMoyenne.getNbValeurs() : 0.0;

                            logger.info("!!!!!!!!!!! Moyenne calculée de : {} , sur le jeu : {}",moyenne, eval.getIdJeu());

                            // Condition de bascule : si on a plus de 300 évaluations et une moyenne>=3 étoile, on publie un DLC:
                            //on met un marqueur de publication de DLC à vrai
                            if (calculateurMoyenne.getNbValeurs() % 300 == 0 && moyenne >= 3.0) {
                                calculateurMoyenne.setDlcPublie(true);
                                System.out.println(
                                        "Un dlc sera publié pour le jeu : "+ eval.getIdJeu()
                                );

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
                        Materialized.<String, CalculateurMoyenne, KeyValueStore<Bytes, byte[]>>as("stats-decision-publication-DLC")//stocker localement le choix de publier ou non
                                .withKeySerde(Serdes.String())//serde de la clé
                                .withValueSerde(calculateurMoyenneSerde)//serde de la valeur
                );


        //map permet de transformer chaque record d'un stream
        // Publication d'un DLC si : isPublie à true
        tableDecisionnelleNotesJeu.toStream()

                .filter((jeuId, stats) -> stats.getDlcPublie())
                .foreach((jeuId, stats) -> {
                            try {

                                long idJeuLong = Long.parseLong(jeuId);


                                service.mettreDLCEnPublication(idJeuLong);


                                logger.info("!!!!!!!!! BDD mise à jour : DLC pour le jeu {} mis à 'enPublication = true'", jeuId);
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
