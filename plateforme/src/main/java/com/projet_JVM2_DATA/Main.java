package com.projet_JVM2_DATA;

import com.projet_JVM2_DATA.consumer.InscriptionJoueurConsumer;
import com.projet_JVM2_DATA.consumer.RequeteAuthentificationJoueurConsumer;
import com.projet_JVM2_DATA.service.UtilisateurService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {

        // 1. Instancier le service
        UtilisateurService utilisateurService = new UtilisateurService();

        // 2. Créer le consumer en lui donnant le service
        InscriptionJoueurConsumer consumerTask = new InscriptionJoueurConsumer(utilisateurService);
        RequeteAuthentificationJoueurConsumer consumerRequest = new RequeteAuthentificationJoueurConsumer(utilisateurService);


        // 3. Lancer le consumer dans un thread dédié pour ne pas bloquer le Main
        Thread kafkaThreadInscriptionJoueur = new Thread(consumerTask);
        kafkaThreadInscriptionJoueur.start();
        Thread kafkaThreadRequeteAuthentificationJoueur = new Thread(consumerRequest);
        kafkaThreadRequeteAuthentificationJoueur.start();


        System.out.println("Plateforme démarrée. En attente d'inscriptions via Kafka...");




    }
}
