package com.projet_JVM2_DATA.producers

import com.example.events.AchatDLC
import com.example.events.CreationCompteJoueur
import com.example.events.AchatJeu
import com.example.events.Session
import com.example.events.CauseFermetureSession
import com.projet_JVM2_DATA.data.PlayerCache
import java.lang.Math.random
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
}


fun productionAchatJeu(idJoueur: Long, idJeu: Long, support: String, prixPaye: Double, versionInstallee: String) {
    if (!PlayerCache.isCurrentPlayer(idJoueur)) {
        println("Achat refusé : Joueur non connecté")
        return
    }

    val event = AchatJeu.newBuilder()
        .setIdJoueur(idJoueur)
        .setIdJeu(idJeu)
        .setSupport(support)
        .setPrixPaye(prixPaye)
        .setVersionInstallee(versionInstallee)
        .setDateAchat(Instant.now())
        .build()

    KafkaProducerManager.send("game-purchases", idJoueur.toString(), event)
}

fun productionAchatDLC(idJoueur: Long, idJeu: Long, idDlc: Long, prixPaye: Double, versionInstallee: String) {
    if (!PlayerCache.isCurrentPlayer(idJoueur)) {
        println("Achat refusé : Joueur non connecté")
        return
    }

    val event = AchatDLC.newBuilder()
        .setIdJeu(idJeu)
        .setIdDlc(idDlc)
        .setIdJoueur(idJoueur)
        .setPrixPaye(prixPaye)
        .setVersionInstalle(versionInstallee)
        .setDateAchat(Instant.now())
        .build()

    KafkaProducerManager.send("dlc-purchases", idJoueur.toString(), event)
}

fun productionSession(idJoueur: Long, idJeu: Long, versionJeu: String) {
    if ((!(PlayerCache.isCurrentPlayer(idJoueur))) || (!(PlayerCache.isGamePurchased(idJeu)))) {
        println("Session refusée : Le joueur n'est pas connecté et/ou ne possède pas ce jeu")
        return
    }

    val minSeconds = 30L * 60   // 1800
    val maxSeconds = 3L * 3600  // 10800

    val randomSeconds = (minSeconds..maxSeconds).random()

    val randomCause = random()

    val event = Session.newBuilder()
        .setIdJoueur(idJoueur)
        .setIdJeu(idJeu)
        .setVersionJeu(versionJeu)
        .setHeureDeDebut(Instant.now())
        .setHeureDeFin(Instant.now().plusSeconds(randomSeconds))
        .setCauseFermeture(
            when {
                randomCause < 0.33 -> CauseFermetureSession.NORMAL
                randomCause < 0.67 -> CauseFermetureSession.CRASH
                else -> CauseFermetureSession.FORCED_EXIT
            }
        )
        .build()

    KafkaProducerManager.send("session-launched", idJoueur.toString(), event)
}

