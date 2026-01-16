package com.projet_JVM2_DATA.data

import java.util.Collections

object PlayerCache {
    // Thread-safe map car Kafka peut être multi-threadé
    private val registeredPlayers = Collections.synchronizedSet(mutableSetOf<String>())

    fun addPlayer(pseudo: String) {
        registeredPlayers.add(pseudo)
        println("💾 Cache : Joueur $pseudo mémorisé.")
    }

    fun exists(pseudo: String): Boolean {
        return registeredPlayers.contains(pseudo)
    }
}