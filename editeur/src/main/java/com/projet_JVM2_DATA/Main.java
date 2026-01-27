package com.projet_JVM2_DATA;


import com.example.events.PublicationJeuOuDLC;
import com.projet_JVM2_DATA.consumer.ReponseAuthentificationConsumer;
import com.projet_JVM2_DATA.producer.*;
import com.projet_JVM2_DATA.service.JeuOuDLCDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class Main {

    public static void main(String[] args) throws SQLException {

        try (Connection con = DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASSWORD"))) {
            System.out.println("Connecté à la base de données !");


        } catch (Exception e) {
            System.err.println("Erreur : non connecté !");
            e.printStackTrace();
        }

        //Ce qui permet de faire des requêtes à la base de données

        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        JeuOuDLCDAO jeuOuDLCDAO = new JeuOuDLCDAO(url, username, password);

        //Tous les producers nécessaires pour la suite
        AuthentificationProducer auth = new AuthentificationProducer();
        JeuProducer jeuProducer = new JeuProducer();
        NouveauCompteProducer nouveauCompteProducer = new NouveauCompteProducer();
        SuppressionCompteProducer suppressionCompteProducer = new SuppressionCompteProducer();
        SuppressionJeuOuDLCProducer suppressionJeuProducer = new SuppressionJeuOuDLCProducer();


        //Tous les consumers necessaires pour la suite
        ReponseAuthentificationConsumer authCons = new ReponseAuthentificationConsumer();



        //Ce qui permettra une entrée en console
        Scanner scanner = new Scanner(System.in);


        boolean sessionActive = true;
        int valeurChoisie = -1;

        while (sessionActive) {
            if (valeurChoisie == -1) {
                System.out.println("Entrez 0 pour créer un nouveau compte et 1 pour se connecter");
                valeurChoisie = Integer.parseInt(scanner.next());
            }

            if (valeurChoisie == 0) {
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

                String[] producerArgs = new String[8];

                producerArgs[0] = String.valueOf(jeuOuDLCDAO.getMaxIDEditeur() + 1);
                producerArgs[1] = nom;
                producerArgs[2] = prenom;
                producerArgs[3] = email;
                producerArgs[4] = pseudo;
                producerArgs[5] = mdp;
                producerArgs[6] = dateNaissance;


                nouveauCompteProducer.main(producerArgs);

                System.out.println("Votre compte est créé !  Redirection vers la connexion");

                valeurChoisie = 1; //passage au bloc connexion
                continue; // On remonte au début du while


            } else if (valeurChoisie == 1) {

                System.out.print("Pseudo : ");
                String pseudo = scanner.next();
                System.out.print("Mot de passe : ");
                String mdp = scanner.next();

                auth.main(new String[]{pseudo, mdp});

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                long idEditeur = authCons.idEditeur;


                if (idEditeur <= 0)
                {
                    System.out.println("Connexion impossible !  Pseudo ou mot de passe incorrect.");
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
                            suppressionJeuProducer.main(new String[]{String.valueOf(idJeu), String.valueOf(idEditeur)});
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
                                ModificationCompteProducer.main(new String[]{nomChamp, nouvelleValeur, String.valueOf(idEditeur)});
                            }
                            break;

                        case 3:
                            System.out.println("Voulez-vous vraiment supprimer votre compte ? (oui/non)");
                            if (scanner.next().equalsIgnoreCase("oui")) {
                                jeuOuDLCDAO.supprimerJeuxEditeur(idEditeur);
                                suppressionCompteProducer.main(new String[]{String.valueOf(idEditeur)});
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


                            System.out.println("Entrez le prix");
                            int prix = scanner.nextInt();

                            System.out.println("Entrez le type de cette façon : DLC ou Jeu");
                            String type = scanner.next();

                            Long idParent;
                            if (!type.equals("DLC"))
                            {
                                idParent = idEditeur;
                            }
                            else{
                                idParent=Long.parseLong("-1");
                            }


                            PublicationJeuOuDLC jeuOuDLC= new PublicationJeuOuDLC(
                                    jeuOuDLCDAO.getMaxIDJeu(), nom, "", genres, prix, type, idEditeur, LocalDate.now(), idParent
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


    }

