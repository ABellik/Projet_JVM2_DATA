package com.projet_JVM2_DATA.tests

import com.example.events.ReponseAuthentificationJoueur
import com.projet_JVM2_DATA.producers.KafkaProducerManager
import java.time.Instant

fun main() {
    println("Envoi d'un message de test conforme AVRO...")

    // 1. On crée l'objet avec la classe générée (garantit le bon format)
    val responseTest = ReponseAuthentificationJoueur.newBuilder()
        .setIdJoueur(100L)
        .setNom("Testeur")
        .setPrenom("Toto")
        .setEmail("toto@test.com")
        .setPseudo("SuperToto") // Le pseudo que tu attends dans ton test
        .setMotDePasse("1234")
        .setDateDeNaissance("01/01/2000")
        .setDateDeCreationDuCompte(Instant.now())
        .setGames(listOf(1L, 2L)) // Liste non vide pour tester
        .setWishlist(emptyList())
        .build()


    // 2. On l'envoie via ton Manager existant
    // Attention : Utilise bien le topic que ton Consumer écoute
    KafkaProducerManager.send(
        topic = "reponse-authentification-joueur",
        key = "SuperToto",
        event = responseTest
    )

    println("Message envoyé ! Vérifie ton Consumer.")

    // Petit délai pour laisser le temps à l'envoi de se faire avant de couper
    Thread.sleep(2000)
}