package com.projet_JVM2_DATA.data

import org.apache.commons.lang3.mutable.Mutable
import java.util.Collections

object PlayerCache {
    private var idJoueur = (-1).toLong()
    private var pseudo = ""
    private var nom = ""
    private var prenom = ""
    private var email = ""
    private var motDePasse = ""
    private var dateDeNaissance = ""
    private var dateDeCreationDuCompte = ""
    private val games = Collections.synchronizedSet(mutableSetOf<Long>())

    fun getPseudo(): String{
        return pseudo
    }

    fun getId(): Long{
        return idJoueur
    }
    fun getNom(): String{
        return nom
    }
    fun getPrenom(): String{
        return prenom
    }
    fun getEmail(): String{
        return email
    }
    fun getMotDePasse(): String{
        return motDePasse
    }
    fun getDateDeCreationDuCompte(): String{
        return dateDeCreationDuCompte
    }
    fun getDateDeNaissance(): String{
        return dateDeNaissance
    }
    fun getGames(): MutableSet<Long>{
        return games
    }


    fun addGame(idJeu: Long) {
        games.add(idJeu)
        println("Cache : Jeu $games mémorisé.")
    }

    fun setPlayer(id: Long, ps: String, n: String, pr: String, e: String, m: String, ddn: String, ddcdc: String, games: MutableList<Long>) {
        idJoueur = id
        pseudo = ps
        nom = n
        prenom = pr
        email = e
        motDePasse = m
        dateDeNaissance = ddn
        dateDeCreationDuCompte = ddcdc

        println("Cache : Joueur $pseudo mémorisé.")
    }

    fun hasActivePlayer(): Boolean{
        return idJoueur != (-1).toLong()
    }

    fun isCurrentPlayer(id: Long): Boolean {
        return idJoueur == id
    }

    fun isGamePurchased(idJeu: Long): Boolean {
        return games.contains(idJeu)
    }

}