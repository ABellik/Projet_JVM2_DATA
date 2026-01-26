package com.projet_JVM2_DATA.producers

import com.example.events.RequeteAuthentificationJoueur
import com.example.events.CreationCompteJoueur
import com.example.events.AchatJeu
import com.example.events.Session
import com.example.events.CauseFermetureSession
import com.example.events.EvaluationJeu
import com.example.events.EvaluationNotee
import com.example.events.RequeteListeEvaluations
import com.projet_JVM2_DATA.cache.GameCatalogCache
import com.projet_JVM2_DATA.cache.PlayerCache
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


    KafkaProducerManager.send("creation-compte-joueur", pseudo, event)
}

fun productionRequeteAuthentificationJoueur(pseudo: String) {
    val event = RequeteAuthentificationJoueur.newBuilder()
        .setPseudo(pseudo)
        .build()

    KafkaProducerManager.send("requete-authentification-joueur", pseudo, event)
}

fun productionAchatJeu(idJeu: Long, support: String) {
    if (!PlayerCache.isConnected()) {
        println("Achat refusé : Joueur non connecté")
        return
    }
    val selectedGame = GameCatalogCache.getGame(idJeu)!!

    val event = AchatJeu.newBuilder()
        .setIdJoueur(PlayerCache.getId())
        .setIdJeu(idJeu)
        .setSupport(support)
        .setPrixPaye(selectedGame.price)
        .setVersionInstallee(selectedGame.version)
        .setDateAchat(Instant.now())
        .build()

    KafkaProducerManager.send("game-purchases", PlayerCache.getId().toString(), event)
}

/*
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
}*/

fun productionSession(idJeu: Long) {
    if ((!(PlayerCache.isConnected())) || (!(PlayerCache.hasGame(idJeu)))) {
        println("Session refusée : Le joueur n'est pas connecté ou ne possède pas ce jeu")
        return
    }

    val minSeconds = 30L * 60   // 1800
    val maxSeconds = 3L * 3600  // 10800

    val randomSeconds = (minSeconds..maxSeconds).random()

    val randomCause = random()

    val event = Session.newBuilder()
        .setIdJoueur(PlayerCache.getId())
        .setIdJeu(idJeu)
        .setVersionJeu(GameCatalogCache.getGame(idJeu)!!.version)
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

    KafkaProducerManager.send("session-launched", PlayerCache.getId().toString(), event)
}

fun productionEvaluationJeu(idJeu: Long, note: Int, commentaire: String?) {
    if (!PlayerCache.isConnected()) {
        println("Evaluation refusée : Vous n'êtes pas connecté")
        return
    }

    if (!(PlayerCache.hasGame(idJeu))) {
        println("Evaluation refusée : Vous ne possédez pas ce jeu")
        return
    }

    val event = EvaluationJeu.newBuilder()
        .setIdJoueur(PlayerCache.getId())
        .setIdJeu(idJeu)
        .setNote(note)
        .setVersionJeu(GameCatalogCache.getGame(idJeu)!!.version)
        .setCommentaire(commentaire)
        .setDateEvaluationJeu(Instant.now())
        .build()

    KafkaProducerManager.send("evaluation-jeu", PlayerCache.getId().toString(), event)
}

fun productionRequeteListeEvaluations(idJeu: Long) {
    val event = RequeteListeEvaluations.newBuilder()
        .setIdJeu(idJeu)
        .build()
    KafkaProducerManager.send("requete-liste-evaluations", idJeu.toString(), event)
}

fun productionReactionEvaluation(idEvaluation: Long, estUtile: Boolean) {
    val event = EvaluationNotee.newBuilder()
        .setIdJoueur(PlayerCache.getId())
        .setIdEvaluation(idEvaluation)
        .setUtile(estUtile)
        .setDateEvalutionDeLEvaluation(Instant.now())
        .build()

    KafkaProducerManager.send("evaluation-notee", PlayerCache.getId().toString(), event)
    println("Réaction envoyée !")
}