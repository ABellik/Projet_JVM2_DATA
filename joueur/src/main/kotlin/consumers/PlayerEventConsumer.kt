package com.projet_JVM2_DATA.consumers

import com.example.events.InfoJeu
import com.example.events.InfoJoueur
import com.projet_JVM2_DATA.cache.GameCatalogCache
import com.projet_JVM2_DATA.cache.PlayerCache
import java.util.concurrent.ConcurrentHashMap

fun startBackgroundConsumers() {
    consommationInfoJoueur()
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
                    dateDeCreationDuCompte = event.dateDeCreationDuCompte.toString(),
                    games = event.games,
                )
            } catch (e: Exception) {
                println("Erreur lors de la mise en cache du joueur : ${e.message}")
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