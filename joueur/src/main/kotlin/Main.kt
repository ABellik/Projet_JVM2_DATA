package com.projet_JVM2_DATA

import com.projet_JVM2_DATA.data.PlayerCache
import com.projet_JVM2_DATA.producers.*
import java.util.Scanner
import kotlin.system.exitProcess

fun clearScreen() {
    repeat(50) { println() }
}

fun recupererPseudos() : MutableList<String> {
    return mutableListOf()
}

fun isPassword(pseudo : String, passwordTry : String) : Boolean{
    return true;
}

fun main() {
    //Démarrage de l'interface console du module Joueur
    println("Démarrage du simulateur Joueur...")
    Thread.sleep(2000)

    //Initialisation des variables de l'interface
    var isFinished = false
    val scanner = Scanner(System.`in`)

    //Message de bienvenue
    println("Bienvenue !")

    //Boucle de rendu de l'interface
    while (!isFinished) {
        if(PlayerCache.hasActivePlayer()){

        }
        //Première requête
        else {
            var reponse1 = ""
            while (reponse1.toIntOrNull() != 1
                && reponse1.toIntOrNull() != 2
                && reponse1.toIntOrNull() != 3
            ) {
                //Première requête
                println("Souhaitez vous : ")
                println("\t- 1 : Vous connecter ?")
                println("\t- 2 : Vous inscrire ?")
                println("\t- 3 : Quitter ?")

                //Attente de la réponse à la première requête
                println("Entrez ici : ")
                reponse1 = scanner.nextLine()
                clearScreen()
            }

            //Décision Première Requête
            if (reponse1.toInt() == 1) {
                //Entrée du pseudo
                println("Pseudo : ")
                var pseudo = scanner.nextLine()
                val listePseudos = recupererPseudos()
                if (listePseudos.contains(pseudo)) {
                    //Entrée du mot de passe
                    println("Mot de passe : ")
                    var motDePasse = scanner.nextLine()
                    if (isPassword(pseudo, motDePasse)) {
                        //A COMPLETER : Code permettant de remplir le joueur du PlayerCache
                        println("Connecté !")
                        Thread.sleep(2000)
                    }
                } else {
                    println("Pseudo inconnu")
                    println("Retour au menu précédent")
                }
            } else if (reponse1.toInt() == 2) {
                println("Procédure d'inscription : \n")
                println("Entrez votre nom : ")
                val nom = scanner.nextLine()
                println("Entrez votre prénom : ")
                val prenom = scanner.nextLine()
                println("Entrez votre date de naissance : ")
                val dateDeNaissance = scanner.nextLine();
                println("Entrez votre email : ")
                val email = scanner.nextLine()
                println("Traitement en cours...")
                Thread.sleep(2000)
                var pseudo = ""
                val listePseudos = recupererPseudos()
                do {
                    if(pseudo != "") println("Ce pseudo existe déjà")
                    println("Entrez un pseudo : ")
                    pseudo = scanner.nextLine()
                } while (listePseudos.contains(pseudo))
                var motDePasse = ""
                do {
                    if(pseudo != "") println("Votre mot de passe doit avoir au moins 8 caractères")
                    println("Entrez un mot de passe : ")
                    motDePasse = scanner.nextLine()
                } while (motDePasse.length < 8)

                productionCompteJoueur(pseudo, nom, prenom, email, motDePasse, dateDeNaissance)
                println("Création de compte réussie !")
                println("Retour au menu précédent pour vous connecter")
                Thread.sleep(2000)
            } else {
                exitProcess(0)
            }
        }


        isFinished = true









    }
}