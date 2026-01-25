package com.projet_JVM2_DATA;

import com.projet_JVM2_DATA.consumer.InscriptionJoueurConsumer;
import com.projet_JVM2_DATA.service.UtilisateurService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        // 1. Instancier le service
        UtilisateurService utilisateurService = new UtilisateurService();

        // 2. Créer le consumer en lui donnant le service
        InscriptionJoueurConsumer consumerTask = new InscriptionJoueurConsumer(utilisateurService);

        // 3. Lancer le consumer dans un thread dédié pour ne pas bloquer le Main
        Thread kafkaThread = new Thread(consumerTask);
        kafkaThread.start();

        System.out.println("🚀 Plateforme démarrée. En attente d'inscriptions via Kafka...");

    }
}
