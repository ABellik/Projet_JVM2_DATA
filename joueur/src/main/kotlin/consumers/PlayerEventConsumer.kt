package com.projet_JVM2_DATA.consumers

import com.example.events.EvolutionPrixJeu
import com.example.events.InfoJeu
import com.example.events.InfoJoueur
import com.example.events.ReponseAuthentificationJoueur
import com.example.events.ReponseListeEvaluations
import com.projet_JVM2_DATA.AuthSync
import com.projet_JVM2_DATA.ReviewSync
import com.projet_JVM2_DATA.cache.GameCatalogCache
import com.projet_JVM2_DATA.cache.PlayerCache
import java.util.concurrent.ConcurrentHashMap

fun startBackgroundConsumers() {
    consommationInfoJoueur()
    consommationReponseAuthentificationJoueur()
    consommationInfoJeu()
    consommationEvolutionPrixJeu()
    consommationReponseListeEvaluations()
}

fun consommationInfoJoueur() {
    Thread {
        KafkaConsumerManager.listen<InfoJoueur>(
            topic = "info-joueur",
            groupId = "module-joueur-cache-populator"
        ) { key, event ->

            try {
                PlayerCache.updateOrAddPlayer(
                    id = event.idJoueur,
                    pseudo = event.pseudo,
                    dateDeCreationDuCompte = event.dateDeCreationDuCompte,
                    games = event.games,
                )
            } catch (e: Exception) {
                println("Erreur lors de la mise en cache du joueur : ${e.message}")
            }
        }
    }.start()
}

fun consommationReponseAuthentificationJoueur() {
    Thread {
        KafkaConsumerManager.listen<ReponseAuthentificationJoueur>(
            topic = "reponse-authentification-joueur",
            groupId = "auth-group-console"
        ) { key, response ->

            println("[DEBUG] Réponse reçue pour : ${response.pseudo}")

            if (AuthSync.futureReponse != null && !AuthSync.futureReponse!!.isDone) {
                AuthSync.futureReponse!!.complete(response)
            }
        }
    }.start()
}

fun consommationInfoJeu() {
    Thread {
        KafkaConsumerManager.listen<InfoJeu>(
            topic = "info-jeu",
            groupId = "module-joueur-cache-populator"
        ) { key, event ->

            try {
                GameCatalogCache.addOrUpdateGame(
                    GameCatalogCache.GameInfo(
                    id = event.id,
                    name = event.nom,
                    genres = event.genre.toMutableSet().let { ConcurrentHashMap.newKeySet() },
                    publisher = event.nomEditeur,
                    price = event.prix,
                    version = event.versionActuelle)
                )
            } catch (e: Exception) {
                println("Erreur : ${e.message}")
            }
        }
    }.start()
}

fun consommationReponseListeEvaluations() {
    Thread {
        KafkaConsumerManager.listen<ReponseListeEvaluations>(
            topic = "reponse-liste-evaluations",
            groupId = "console-reviews-reader"
        ) { key, response ->
            if (ReviewSync.futureReponse != null && !ReviewSync.futureReponse!!.isDone) {
                ReviewSync.futureReponse!!.complete(response)
            }
        }
    }.start()
}

fun consommationEvolutionPrixJeu() {
    Thread {
        KafkaConsumerManager.listen<EvolutionPrixJeu>(
            topic = "evolution-prix-jeu",
            groupId = "module-joueur-cache-populator"
        ) { key, event ->

            try {
                if(PlayerCache.getGames().contains(event.idJeu)) PlayerCache.getNotifications().add("Le prix d'un jeu sur votre liste de souhaits a évolué : ${GameCatalogCache.getGame(event.idJeu)?.name} est passé de ${event.ancienPrix} à ${event.nouveauPrix}")
            } catch (e: Exception) {
                println("Erreur : ${e.message}")
            }
        }
    }.start()
}