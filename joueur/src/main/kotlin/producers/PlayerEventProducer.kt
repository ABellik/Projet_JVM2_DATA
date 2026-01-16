package com.projet_JVM2_DATA.producers

import com.example.events.CreationCompteJoueur
import com.example.events.AchatJeu
import com.projet_JVM2_DATA.data.PlayerCache
import java.time.Instant


/**
 * Produit un événement de création de compte joueur et l'envoie dans le flux Kafka.
 *
 * Cette fonction orchestre le processus d'inscription en :
 * 1. Construisant l'objet Avro [CreationCompteJoueur] conforme au schéma présent dans le module common.
 * 2. Envoyant l'événement de manière asynchrone via le [KafkaProducerManager].
 * 3. Mettant à jour le [PlayerCache] local pour permettre les futurs achats.
 *
 * Le message est envoyé sur le topic `"creation-compte-joueur"` avec le [pseudo] comme clé
 *
 * @param pseudo Le pseudo unique du joueur (servira de Clé Kafka).
 * @param nom Le nom de famille du joueur.
 * @param prenom Le prénom du joueur.
 * @param email L'adresse email de contact.
 * @param motDePasse Le mot de passe du joueur.
 * @param dateDeNaissance La date de naissance (format attendu par le schéma, ex: "YYYY-MM-DD").
 *
 * @see KafkaProducerManager
 * @see PlayerCache
 */
fun productionCompteJoueur(pseudo: String, nom: String, prenom: String, email: String, motDePasse: String, dateDeNaissance: String) {

    val event = CreationCompteJoueur.newBuilder()
        .setPseudo(pseudo)
        .setNom(nom)
        .setPrenom(prenom)
        .setEmail(email)
        .setMotDePasse(motDePasse)
        .setDateDeNaissance(dateDeNaissance)
        .setDateDeCreationDuCompte(Instant.now())
        .build()


    // Envoi via le Manager technique
    KafkaProducerManager.send("creation-compte-joueur", pseudo, event)

    // Mise à jour du cache local
    PlayerCache.addPlayer(pseudo)
}

/*
// Fonction Top-Level pour l'achat
fun produceGamePurchase(pseudo: String, gameId: String, price: Double) {
    if (!PlayerCache.exists(pseudo)) {
        println("⚠️ Achat refusé : Joueur inconnu")
        return
    }

    val event = GamePurchased.newBuilder()
        // ... set les champs
        .build()

    KafkaProducerManager.send("game-purchases", pseudo, event)


}*/