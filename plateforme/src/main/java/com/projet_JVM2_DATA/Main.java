package com.projet_JVM2_DATA;

import com.example.events.RequeteAuthentificationEditeur;
import com.projet_JVM2_DATA.consumer.InscriptionEditeurConsumer;
import com.example.events.InfoJeu;
import com.projet_JVM2_DATA.consumer.InscriptionJoueurConsumer;
import com.projet_JVM2_DATA.consumer.RequeteAuthEditeurConsumer;
import com.projet_JVM2_DATA.consumer.RequeteAuthentificationJoueurConsumer;
import com.projet_JVM2_DATA.service.EditeurService;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.producer.InfoJeuProducer;
import com.projet_JVM2_DATA.service.GenreJeuService;
import com.projet_JVM2_DATA.service.JeuService;
import com.projet_JVM2_DATA.service.LicenceService;
import com.projet_JVM2_DATA.service.UtilisateurService;

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

        EditeurService editeurService= new EditeurService();

        // 2. Créer le consumer en lui donnant le service
        InscriptionJoueurConsumer consumerTask = new InscriptionJoueurConsumer(utilisateurService);
        RequeteAuthentificationJoueurConsumer consumerRequest = new RequeteAuthentificationJoueurConsumer(utilisateurService);

        InscriptionEditeurConsumer consumerEd= new InscriptionEditeurConsumer(editeurService);
        RequeteAuthEditeurConsumer consumer= new RequeteAuthEditeurConsumer(editeurService);


        // 3. Lancer le consumer dans un thread dédié pour ne pas bloquer le Main
        Thread kafkaThreadInscriptionJoueur = new Thread(consumerTask);
        kafkaThreadInscriptionJoueur.start();
        Thread kafkaThreadRequeteAuthentificationJoueur = new Thread(consumerRequest);
        kafkaThreadRequeteAuthentificationJoueur.start();

        Thread kafkaThreadInscriptionEditeur= new Thread(consumer);
        kafkaThreadInscriptionJoueur.start();
        Thread kafkaThreadRequeteAuthEditeur = new Thread(consumer);
        kafkaThreadRequeteAuthentificationJoueur.start();


        System.out.println("Plateforme démarrée. En attente d'inscriptions via Kafka...");




    }
}
