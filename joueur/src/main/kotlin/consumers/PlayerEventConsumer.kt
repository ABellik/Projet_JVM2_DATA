package com.projet_JVM2_DATA.consumers

import com.example.events.InfoJeu
import com.example.events.InfoJoueur
import com.example.events.ReponseAuthentificationJoueur
import com.projet_JVM2_DATA.AuthSync
import com.projet_JVM2_DATA.cache.GameCatalogCache
import com.projet_JVM2_DATA.cache.PlayerCache
import java.util.concurrent.ConcurrentHashMap

fun startBackgroundConsumers() {
    consommationInfoJoueur()
    consommationReponseAuthentificationJoueur()
    consommationInfoJeu()
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

            // On vérifie si le Main attend une réponse
            if (AuthSync.futureReponse != null && !AuthSync.futureReponse!!.isDone) {
                // On débloque le Main en lui donnant la réponse
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
                println("Erreur lors de la mise en cache du joueur : ${e.message}")
            }
        }
    }.start()
}