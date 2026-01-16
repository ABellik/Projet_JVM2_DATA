package com.projet_JVM2_DATA

import com.projet_JVM2_DATA.data.PlayerCache
import com.projet_JVM2_DATA.producers.KafkaProducerManager
import com.projet_JVM2_DATA.producers.productionCompteJoueur

fun main() {
    println("Démarrage du simulateur Joueur...")

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

    println(PlayerCache.exists("GamerZ"))
    println(PlayerCache.exists("Gamer"))

    // --- NETTOYAGE ---
    // Ajout d'un hook pour fermer proprement la connexion Kafka à la fin du programme
    Runtime.getRuntime().addShutdownHook(Thread {
        println("\n Arrêt du service, fermeture du Producer...")
        KafkaProducerManager.close()
    })

    println("\n Fin du script de test. (Le programme s'arrêtera après l'envoi des logs Kafka)")
}