package com.projet_JVM2_DATA.cache

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache représentant le joueur actuellement connecté dans le module Joueur.
 */
object PlayerCache {

    private var currentPlayer: Player? = null
    private val players = ConcurrentHashMap<Long, Player>()

    /**
     * Classe représentant les données d'un joueur connecté
     */
    data class Player(
        val id: Long,
        val pseudo: String,
        val nom: String?,
        val prenom: String?,
        val email: String?,
        val dateDeNaissance: String?,
        val dateDeCreationDuCompte: String,
        val motDePasse: String?,
        val games: MutableSet<Long> = ConcurrentHashMap.newKeySet()
    ) {
        val dateDeConnexion: LocalDateTime = LocalDateTime.now()

        fun addGame(gameId: Long) {
            games.add(gameId)
        }

        fun hasGame(gameId: Long): Boolean = games.contains(gameId)

        fun removeGame(gameId: Long) {
            games.remove(gameId)
        }
    }

    // ==================== Gestion de session ====================

    /**
     * Connecte un joueur et initialise sa session
     */
    fun login(
        id: Long,
        pseudo: String,
        nom: String,
        prenom: String,
        email: String,
        dateDeNaissance: String,
        dateDeCreationDuCompte: String,
        motDePasse: String,
        games: List<Long> = emptyList()
    ) {
        if (isConnected()) {
            throw IllegalStateException("Un joueur est déjà connecté : ${currentPlayer?.pseudo}")
        }

        currentPlayer = Player(
            id = id,
            pseudo = pseudo,
            nom = nom,
            prenom = prenom,
            email = email,
            dateDeNaissance = dateDeNaissance,
            dateDeCreationDuCompte = dateDeCreationDuCompte,
            motDePasse = motDePasse,
            games = games.toMutableSet().let { ConcurrentHashMap.newKeySet<Long>().apply { addAll(it) } }
        )

        println("Cache : Joueur '$pseudo' connecté (${games.size} jeux)")
    }

    /**
     * Déconnecte le joueur actuel et vide le cache
     */
    fun logout() {
        if (currentPlayer != null) {
            println("Cache : Joueur '${currentPlayer?.pseudo}' déconnecté")
            currentPlayer = null
        }
    }

    /**
     * Vérifie si un joueur est actuellement connecté
     */
    fun isConnected(): Boolean = currentPlayer != null

    /**
     * Récupère le joueur connecté ou lance une exception
     */
    private fun requirePlayer(): Player {
        return currentPlayer ?: throw IllegalStateException("Aucun joueur connecté")
    }

    // ==================== Accesseurs ====================

    fun getId(): Long = requirePlayer().id

    fun getPseudo(): String = requirePlayer().pseudo

    fun getNom(): String? = requirePlayer().nom

    fun getPrenom(): String? = requirePlayer().prenom

    fun getEmail(): String? = requirePlayer().email

    fun getMotDePasse(): String? = requirePlayer().motDePasse

    fun getDateDeNaissance(): String? = requirePlayer().dateDeNaissance

    fun getDateDeCreationDuCompte(): String = requirePlayer().dateDeCreationDuCompte

    fun getGames(): Set<Long> = requirePlayer().games.toSet()  // Copie immuable

    fun getDateDeConnexion(): LocalDateTime = requirePlayer().dateDeConnexion

    /**
     * Retourne le joueur complet (pour affichage ou sérialisation)
     */
    fun getPlayer(): Player? = currentPlayer

    // ==================== Gestion de la bibliothèque ====================

    /**
     * Ajoute un jeu à la bibliothèque du joueur
     */
    fun addGame(gameId: Long) {
        requirePlayer().addGame(gameId)
        println("Cache : Jeu $gameId ajouté à la bibliothèque de ${getPseudo()}")
    }

    /**
     * Vérifie si le joueur possède un jeu
     */
    fun hasGame(gameId: Long): Boolean = requirePlayer().hasGame(gameId)

    /**
     * Retire un jeu de la bibliothèque (ex: remboursement)
     */
    fun removeGame(gameId: Long) {
        requirePlayer().removeGame(gameId)
        println("Cache : Jeu $gameId retiré de la bibliothèque de ${getPseudo()}")
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Vérifie si le joueur connecté correspond à cet ID
     */
    fun isCurrentPlayer(playerId: Long): Boolean {
        return currentPlayer?.id == playerId
    }

    /**
     * Affiche les informations du joueur (debug)
     */
    fun printPlayerInfo() {
        val player = currentPlayer
        if (player == null) {
            println("Aucun joueur connecté")
        } else {
            println(
                """
                ╔════════════════════════════════════════╗
                ║      Informations du joueur            ║
                ╠════════════════════════════════════════╣
                ║ ID: ${player.id}
                ║ Pseudo: ${player.pseudo}
                ║ Nom: ${player.nom} ${player.prenom}
                ║ Email: ${player.email}
                ║ Date de naissance: ${player.dateDeNaissance}
                ║ Membre depuis: ${player.dateDeCreationDuCompte}
                ║ Connecté depuis: ${player.dateDeConnexion}
                ║ Jeux possédés: ${player.games.size}
                ╚════════════════════════════════════════╝
            """.trimIndent()
            )
        }
    }

    // ==================== Gestion de la liste globale des joueurs ====================

    /**
     * Ajoute ou met à jour un joueur dans le cache global (venant de Kafka).
     * Cette méthode est appelée par le Consumer.
     */
    fun updateOrAddPlayer(
        id: Long,
        pseudo: String,
        dateDeCreationDuCompte: String,
        games: List<Long>
    ) {
        val newPlayer = Player(
            id = id,
            pseudo = pseudo,
            nom = null,
            prenom = null,
            email = null,
            dateDeNaissance = null,
            dateDeCreationDuCompte = dateDeCreationDuCompte,
            motDePasse = null,
            games = games.toMutableSet().let { ConcurrentHashMap.newKeySet() }
        )

        // On stocke dans la map globale
        players[id] = newPlayer
        println("Cache Global : Joueur reçu et stocké -> $pseudo (ID: $id)")
    }

    /**
     * Récupère un joueur spécifique par son ID (pour consulter son profil par exemple).
     */
    fun getPlayerById(id: Long): Player? {
        return players[id]
    }

    /**
     * Récupère la liste de tous les joueurs connus.
     */
    fun getAllPlayers(): List<Player> {
        return players.values.toList()
    }
}