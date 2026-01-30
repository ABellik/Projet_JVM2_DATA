package com.projet_JVM2_DATA;

import com.example.events.InfoJoueur;
import com.example.events.RequeteAuthentificationEditeur;
import com.projet_JVM2_DATA.consumer.*;
import com.example.events.InfoJeu;
import com.projet_JVM2_DATA.entity.Utilisateur;
import com.projet_JVM2_DATA.producer.EvaluationProducer;
import com.projet_JVM2_DATA.producer.InfoJoueurProducer;
import com.projet_JVM2_DATA.service.*;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.producer.InfoJeuProducer;

import java.time.ZoneId;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {


        // 1. Instancier le service
        UtilisateurService utilisateurService = new UtilisateurService();
        JeuService jeuService = new JeuService();
        GenreJeuService genreJeuService = new GenreJeuService();
        LicenceService licenceService = new LicenceService();
        SessionService sessionService = new SessionService();
        WishlistService wishlistService = new WishlistService();
        BibliothèqueService bibliothèqueService = new BibliothèqueService(new EvaluationProducer());

        // 1.bis Il faut lancer le producer permettant d'envoyer la liste des jeux au joueur
        List<Jeu> jeux = jeuService.getAllJeu();
        InfoJeuProducer infoJeuProducer = new InfoJeuProducer();
        for(Jeu jeu : jeux) {
            infoJeuProducer.envoyerInfoJeu(
                    new InfoJeu(
                            jeu.getId(),
                            jeu.getNom(),
                            jeu.getVersionActuelle(),
                            genreJeuService.getGenresByIdJeu(jeu.getId()),
                            jeu.getPrixActuel().doubleValue(),
                            jeu.getIdEditeur().getNom(),
                            licenceService.getNomLicencesByIdJeu(jeu.getId())
                    ));
        }

        // 1.bis Il faut lancer le producer permettant d'envoyer la liste des joueurs au joueur
        List<Utilisateur> utilisateurs = utilisateurService.ListeUtilisateurs();
        InfoJoueurProducer infoJoueurProducer = new InfoJoueurProducer();
        for(Utilisateur utilisateur : utilisateurs) {
            infoJoueurProducer.envoyerInfoJoueur(
                    new InfoJoueur(
                            utilisateur.getId(),
                            utilisateur.getPseudo(),
                            utilisateur.getDateInscription().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                            jeuService.getJeuByPseudo(utilisateur.getPseudo())
                    ));
        }

        EditeurService editeurService= new EditeurService();

        // 2. Créer le consumer en lui donnant le service
        InscriptionJoueurConsumer consumerTask = new InscriptionJoueurConsumer(utilisateurService);
        RequeteAuthentificationJoueurConsumer consumerRequest = new RequeteAuthentificationJoueurConsumer(utilisateurService);

        InscriptionEditeurConsumer consumerEd= new InscriptionEditeurConsumer(editeurService);
        RequeteAuthEditeurConsumer consumer= new RequeteAuthEditeurConsumer(editeurService);

        AchatJeuConsumer consumerAchatJeu= new AchatJeuConsumer(utilisateurService);

        SessionConsumer consumerSession = new SessionConsumer(sessionService);

        AjoutWishlistConsumer consumerAjoutWishlist = new AjoutWishlistConsumer(wishlistService);

        EvaluationJeuConsumer consumerEvaluationJeu = new EvaluationJeuConsumer(bibliothèqueService);


        // 3. Lancer le consumer dans un thread dédié pour ne pas bloquer le Main
        Thread kafkaThreadInscriptionJoueur = new Thread(consumerTask);
        kafkaThreadInscriptionJoueur.start();
        Thread kafkaThreadRequeteAuthentificationJoueur = new Thread(consumerRequest);
        kafkaThreadRequeteAuthentificationJoueur.start();

        Thread kafkaThreadAchatJeu = new Thread(consumerAchatJeu);
        kafkaThreadAchatJeu.start();

        Thread kafkaThreadSession = new Thread(consumerSession);
        kafkaThreadSession.start();

        Thread kafkaThreadAjoutWishlist = new Thread(consumerAjoutWishlist);
        kafkaThreadAjoutWishlist.start();

        Thread kafkaThreadEvaluationJeu = new Thread(consumerEvaluationJeu);
        kafkaThreadEvaluationJeu.start();


        Thread kafkaThreadInscriptionEditeur= new Thread(consumerEd);
        kafkaThreadInscriptionEditeur.start();

        Thread kafkaThreadRequeteAuthEditeur = new Thread(consumer);
        kafkaThreadRequeteAuthEditeur.start();

        System.out.println("Plateforme démarrée !");


    }
}
