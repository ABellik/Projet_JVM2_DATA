package com.projet_JVM2_DATA

import com.projet_JVM2_DATA.data.PlayerCache
import com.projet_JVM2_DATA.producers.KafkaProducerManager
import com.projet_JVM2_DATA.producers.productionAchatDLC
import com.projet_JVM2_DATA.producers.productionAchatJeu
import com.projet_JVM2_DATA.producers.productionCompteJoueur

fun test() {
    /*
    println("Démarrage du simulateur Joueur")
    Thread.sleep(2000)

    println("\n [Test 1] Inscription du joueur 'GamerZ'")
    productionCompteJoueur(
        "GamerZ",
        "Martin",
        "Paul",
        "paul.martin@gmail.com",
        "S3cr3t!",
        "1998-05-24"
    )

    Thread.sleep(1000)

    productionAchatJeu(
        "GamerZ",
        2,
        "PS5",
        20.99,
        "2.1.2")

    productionAchatDLC(
        "GamerZ",
        2,
        1,
        20.99,
        "2.1.2")

    // --- NETTOYAGE ---
    Runtime.getRuntime().addShutdownHook(Thread {
        println("\n Arrêt du service, fermeture du Producer...")
        KafkaProducerManager.close()
    })

    println("\n Fin du script de test. (Le programme s'arrêtera après l'envoi des logs Kafka)")
    */
}