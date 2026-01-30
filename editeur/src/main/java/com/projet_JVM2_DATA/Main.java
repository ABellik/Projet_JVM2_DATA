
package com.projet_JVM2_DATA;
import com.example.events.PublicationJeuOuDLC;
import com.projet_JVM2_DATA.editeur.CrashAggregationStream;
import com.projet_JVM2_DATA.kafka.consumer.CrashConsumer;
import com.projet_JVM2_DATA.kafka.consumer.PatchTriggerConsumer;
import com.projet_JVM2_DATA.kafka.consumer.ReponseAuthentificationConsumer;
import com.projet_JVM2_DATA.kafka.producer.*;
import com.projet_JVM2_DATA.dao.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static java.lang.System.getenv;


public class Main {

    public static void main(String[] args) throws SQLException, InterruptedException {

        //Ce qui permet de faire des requêtes à la base de données

        String url = getenv("DB_URL");
        String username = getenv("DB_USER");
        String password = getenv("DB_PASSWORD");

        //Parametres de connexion
        String bootstrap = "localhost:9092";
        String schemaRegistry = "http://localhost:8081";
        String topicCrash = "detected-crashs";
        String topicTriggers = "potential-patches";
        String topicPatches = "published-patches";


        // Demarrage des services
        startBackgroundServices(bootstrap, schemaRegistry,topicCrash,topicTriggers,topicPatches);

        JeuOuDlcDao jeuOuDLCDAO = new JeuOuDlcDao(url, username, password);

        //Tous les producers nécessaires pour la suite
        AuthentificationProducer auth = new AuthentificationProducer(getenv("KAFKA_BOOTSTRAP_SERVERS"), getenv("SCHEMA_REGISTRY_URL"),"nouvelle-connexion-editeur");
        JeuProducer jeuProducer = new JeuProducer(getenv("KAFKA_BOOTSTRAP_SERVERS"),   getenv("SCHEMA_REGISTRY_URL"), "nouveau-jeu");
        NouveauCompteProducer nouveauCompteProducer = new NouveauCompteProducer(getenv("KAFKA_BOOTSTRAP_SERVERS"), getenv("SCHEMA_REGISTRY_URL"),"nouveau-compte-editeur");
        ModificationCompteProducer modificationCompteProducer = new ModificationCompteProducer(getenv("KAFKA_BOOTSTRAP_SERVERS"), getenv("SCHEMA_REGISTRY_URL"),"modification-compte-editeur");
        SuppressionCompteProducer suppressionCompteProducer = new SuppressionCompteProducer(getenv("KAFKA_BOOTSTRAP_SERVERS"), getenv("SCHEMA_REGISTRY_URL"),"suppression-compte-editeur");
        SuppressionJeuOuDLCProducer suppressionJeuProducer = new SuppressionJeuOuDLCProducer(getenv("KAFKA_BOOTSTRAP_SERVERS"), getenv("SCHEMA_REGISTRY_URL"),"suppression-jeu");


        //Tous les consumers necessaires pour la suite
        ReponseAuthentificationConsumer authCons = new ReponseAuthentificationConsumer(getenv("KAFKA_BOOTSTRAP_SERVERS"), getenv("SCHEMA_REGISTRY_URL"));

        //Thread qui sera actif sur toute la durée de l'application
        Thread authThread = new Thread(authCons::demarrerEcoute);
        authThread.setDaemon(true);//fait en sorte de couper le thread quand le principal est coupé
        authThread.start();


        //Ce qui permettra une entrée en console
        Scanner scanner = new Scanner(System.in);


        boolean sessionActive = true;
        int valeurChoisie = -1;

        while (sessionActive) {
            if (valeurChoisie == -1) {
                System.out.println("\nEntrez 0 pour créer un compte ou 1 pour se connecter (autre pour quitter)");
                String input = scanner.next();
                if (input.equals("0")) valeurChoisie = 0;
                else if (input.equals("1")) valeurChoisie = 1;
                else sessionActive = false;
            }

            if (valeurChoisie == 0) {

                System.out.println("Editeur INDEPENDANT : Saisir 1 \n Editeur en entreprise : Saisir 2");
                int val = scanner.nextInt();

                if(val==1) {


                    System.out.println("Entrez votre nom");
                    String nom = scanner.next();

                    System.out.println("Entrez votre prenom");
                    String prenom = scanner.next();

                    System.out.println("Entrez votre email");
                    String email = scanner.next();

                    System.out.println("Entrez un pseudo");
                    String pseudo = scanner.next();

                    System.out.println("Entrez votre mot de passe");
                    String mdp = scanner.next();

                    System.out.println("Entrez votre date de naissance sous ce format : XX/XX/XXXX");
                    String dateNaissance = scanner.next();

                    auth.envoyer(new String[]{pseudo, mdp});

                    // 4. Attente de la réponse
                    long idEditeur = -1;
                    int tentatives = 0;
                    while (idEditeur <= 0 && tentatives < 5) {
                        Thread.sleep(1000);
                        idEditeur = authCons.getIdEditeur();
                        tentatives++;
                    }

                    if (idEditeur <= 0) {
                        System.out.println("problème avec id editeur");
                        valeurChoisie = -1;
                        continue;
                    }

                    String[] producerArgs = new String[8];

                    producerArgs[0] = String.valueOf(jeuOuDLCDAO.getMaxIDEditeur() + 1);
                    producerArgs[1] = nom;
                    producerArgs[2] = prenom;
                    producerArgs[3] = email;
                    producerArgs[4] = pseudo;
                    producerArgs[5] = mdp;
                    producerArgs[6] = dateNaissance;


                    nouveauCompteProducer.envoyer(producerArgs);

                    System.out.println("Votre compte est créé !  Redirection vers la connexion");

                    valeurChoisie = 1; //passage au bloc connexion
                    continue; // On remonte au début du while
                }
                else if(val==2)
                {
                    System.out.println("Entrez le nom de l'entreprise");
                    String nom = scanner.next();

                    String[] producerArgs = new String[8];

                    producerArgs[0] = String.valueOf(jeuOuDLCDAO.getMaxIDEditeur() + 1);
                    producerArgs[1] = nom;
                    producerArgs[2] = "";
                    producerArgs[3] = "";
                    producerArgs[4] = "";
                    producerArgs[5] = "";
                    producerArgs[6] = "";


                    nouveauCompteProducer.envoyer(producerArgs);

                    System.out.println("Votre compte est créé !  Redirection vers la connexion");

                    valeurChoisie = 1; //passage au bloc connexion
                    continue; // On remonte au début du while
                }

            } else if (valeurChoisie == 1) {

                System.out.print("Pseudo : ");
                String pseudo = scanner.next();
                System.out.print("Mot de passe : ");
                String mdp = scanner.next();

                long idEditeur = -1;

                // Demande de l'id
                authCons.resetId();

                //envoie un évènement à la plateforme pour demander la connexion
                auth.envoyer(new String[]{pseudo, mdp});

                int tentatives = 0;
                while (idEditeur <= 0 && tentatives < 5) {
                    Thread.sleep(1000);
                    idEditeur = authCons.getIdEditeur(); // Mise à jour de l'id editeur
                    tentatives++;
                }

                //produit les jeux en grâce aux calculs faits dans le stream
                jeuProducer.envoyer();

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                }


                if (idEditeur <= 0)
                {
                    System.out.println("\n Connexion impossible !  Pseudo ou mot de passe incorrect.");
                    valeurChoisie = -1; // Retour au menu principal dans ce cas
                    continue;
                }

                System.out.println(" Vous êtes connecté ! Voici votre id editeur : " + idEditeur);

                // Affichage des jeux de l'editeur connecté
                System.out.println("Vos jeux en base :");
                List<PublicationJeuOuDLC> jeux = jeuOuDLCDAO.getJeuByEditeur(idEditeur);

                if (jeux.isEmpty()) {
                    System.out.println("Vous n'avez aucun jeu enregistré.");
                }
                System.out.println("->>>Liste de tous vos jeux en base<<<-");

                for (PublicationJeuOuDLC jeu : jeux)
                {
                    System.out.println(jeu.toString());
                }


                //On ne propose pas de publier de jeu car nous avons décidé la publication selon certains
                //critères pour maximiser les ventes donc elle se fait automatiquement. De même pour les DLC
                System.out.println("\n1: Supprimer Jeu \n 2: Modifier Compte \n 3: Supprimer Compte \n 4: Déconnexion \n 5: Ajouter un jeu");
                int valeur = Integer.parseInt(scanner.next());

                switch (valeur) {
                    case 1:
                        System.out.println("Entrez l'id du jeu à supprimer :");
                        long idJeu = Long.parseLong(scanner.next());
                        suppressionJeuProducer.envoyer(new String[]{String.valueOf(idJeu), String.valueOf(idEditeur)});
                        break;

                    case 2:
                        System.out.println("1. Mot de passe \n 2. Nom \n 3. Prénom\n 4. Pseudo \n 5. Email");
                        int choix = scanner.nextInt();
                        scanner.nextLine();

                        String nomChamp = switch (choix) {
                            case 1 -> "mot_de_passe";
                            case 2 -> "nom";
                            case 3 -> "prenom";
                            case 4 -> "pseudo";
                            case 5 -> "email";
                            default -> null;
                        };

                        if (nomChamp != null) {
                            System.out.print("Nouvelle valeur : ");
                            String nouvelleValeur = scanner.nextLine();
                            modificationCompteProducer.envoyer(new String[]{nomChamp, nouvelleValeur, String.valueOf(idEditeur)});
                        }
                        break;

                    case 3:
                        System.out.println("Voulez-vous vraiment supprimer votre compte ? (oui/non)");
                        if (scanner.next().equalsIgnoreCase("oui")) {
                            jeuOuDLCDAO.supprimerJeuxEditeur(idEditeur);
                            suppressionCompteProducer.envoyer(new String[]{String.valueOf(idEditeur)});
                            valeurChoisie = -1; // Déconnexion forcée après suppression
                        }
                        break;

                    case 4:
                        valeurChoisie = -1;
                        break;

                    case 5:

                        System.out.println("Entrez le nom du jeu");
                        String nom = scanner.next();

                        System.out.println("Entrez le nombre de genres que vous voulez entrer");
                        int nbGenres = scanner.nextInt();
                        //Collection des genres
                        ArrayList<String> genres = new ArrayList<>();
                        for (int i=0; i<nbGenres; i++)
                        {
                            System.out.println("Entrez le nom de genre");
                            genres.add( scanner.next());
                        }


                        System.out.println("Entrez le nombre de supports que vous voulez entrer");
                        int nbSupports = scanner.nextInt();
                        //Collection des supports
                        ArrayList<String> supports = new ArrayList<>();
                        for (int i=0; i<nbGenres; i++)
                        {
                            System.out.println("Entrez le nom du support");
                            supports.add( scanner.next());
                        }

                        System.out.println("Entrez le prix");
                        int prix = scanner.nextInt();

                        System.out.println("Entrez le type de cette façon : DLC ou Jeu");
                        String type = scanner.next();

                        Long idParent;
                        if (!type.equals("DLC"))
                        {
                            idParent=Long.parseLong("-1");
                        }
                        else{
                            String nomJeu;
                            do {
                                System.out.print("Entrez un nom existant : ");
                                nomJeu = scanner.nextLine();
                            } while (!jeuOuDLCDAO.existeParNom(nomJeu));

                            System.out.println("Nom valide : " + nomJeu);
                            idParent=jeuOuDLCDAO.getIDParent(nomJeu);

                        }


                        PublicationJeuOuDLC jeuOuDLC= new PublicationJeuOuDLC(
                                jeuOuDLCDAO.getMaxIDJeu(), nom, "", genres,supports, prix, type, idEditeur, LocalDate.now(), idParent
                        );
                        jeuOuDLCDAO.insertionNouveauJeuOuDLC(jeuOuDLC);
                        break;
                }
            }
            else{
                sessionActive = false;
            }


        }


    }


    private static void startBackgroundServices(String bootstrap, String schemaRegistry,String topicCrash, String topicTriggers, String topicPatches) {
        // Thread pour Kafka Streams
        Thread streamsThread = new Thread(() -> {
            CrashAggregationStream.main(new String[]{});
        }, "streams-thread");

        // Thread pour l'historisation des crashs en DB
        Thread crashConsumerThread = new Thread(() -> {
            try {
                CrashConsumer crashConsumer = new CrashConsumer(bootstrap, schemaRegistry);
                crashConsumer.start(topicCrash);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "crash-consumer-thread");
        // Thread pour la génération automatique de patchs

        Thread patchTriggerThread = new Thread(() -> {
            PatchTriggerConsumer ptc =
                    new PatchTriggerConsumer(bootstrap, schemaRegistry, topicPatches);
            ptc.start(topicTriggers);
        }, "patch-trigger-thread");

        // Lancement en mode Daemon pour que les threads s'arrêtent si on ferme le menu
        streamsThread.setDaemon(true);
        crashConsumerThread.setDaemon(true);
        patchTriggerThread.setDaemon(true);

        streamsThread.start();
        crashConsumerThread.start();
        patchTriggerThread.start();

        System.out.println("Services de monitoring (Streams & Consumers) démarrés en arrière-plan.");
    }

}
