package com.projet_JVM2_DATA.cache

import java.util.concurrent.ConcurrentHashMap

object GameCatalogCache {
    private val games = ConcurrentHashMap<Long, GameInfo>()

    data class GameInfo(
        val id: Long,
        val name: String,
        val genres : Set<String>,
        val publisher: String,
        val price: Double,
        val version: String,
        val supports : Set<String>
    )

    fun addOrUpdateGame(game: GameInfo) {
        games[game.id] = game
        println("Cache : Jeu ${game.name} ajouté/mis à jour")
    }

    fun getAllGames(): List<GameInfo> {
        return games.values.toList()
    }

    fun getGame(id: Long): GameInfo? {
        return games[id]
    }

    fun getGameIds(): List<Long> {
        return games.keys.toList()
    }
}