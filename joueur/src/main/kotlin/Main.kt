package com.projet_JVM2_DATA

import com.projet_JVM2_DATA.data.PlayerCache
import com.projet_JVM2_DATA.producers.*
import java.util.Scanner

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
            println("Bienvenue "+ PlayerCache.getPseudo())

            var reponse2: String
            do {
                //Première requête
                println("Souhaitez vous : ")
                println("\t- 1 : Consulter notre catalogue de jeux")
                println("\t- 2 : Voir votre profil et bibliothèque de jeux")
                println("\t- 3 : Jouer à un jeu")
                println("\t- 4 : Voir vos dernières informations")
                println("\t- 5 : Acheter un nouveau jeu")
                println("\t- 6 : Rechercher un autre joueur")
                println("\t- 7 : Rechercher un éditeur")
                println("\t- 8 : Quitter ?")

                //Attente de la réponse à la première requête
                println("Entrez ici : ")
                reponse2 = scanner.nextLine()
                clearScreen()
            } while (reponse2.toIntOrNull() !in 1..8 )

            if(reponse2.toInt() == 1){}

            else if(reponse2.toInt() == 2){
                println("Votre profil : ")
                println("Pseudo : "+PlayerCache.getPseudo())
                println("Nom : "+PlayerCache.getNom())
                println("Prénom : "+PlayerCache.getPrenom())
                println("Date de Naissance : "+PlayerCache.getDateDeNaissance())
                println("Adresse email : "+PlayerCache.getEmail())
                println("\n\n")
                println("Votre liste de jeux : ")
                println(PlayerCache.getGames())
            }

            else if(reponse2.toInt() == 3){
                println("Choisissez un jeu auquel jouer : ")
                println(PlayerCache.getGames())
                var numJeuchoisi: String
                do{
                    numJeuchoisi = scanner.nextLine()
                    if(!(PlayerCache.getGames().contains(numJeuchoisi.toLongOrNull()))){
                        println("Numéro invalide")
                        println("Choisissez un jeu")
                    }
                } while (!(PlayerCache.getGames().contains(numJeuchoisi.toLongOrNull())))

                //productionSession(PlayerCache.getId(),numJeuchoisi, ...) //A COMPLETER
                println("Session lancée !")
                println("Simulation du temps de jeu")
                Thread.sleep(2000)
                println("Session terminée !")
                clearScreen()
            }

            else if(reponse2.toInt() == 4){}

            else if(reponse2.toInt() == 5){
                //A COMPLETER : Récupérer la liste des jeux du catalogue
                var catalogue_jeux: MutableList<Long> = mutableListOf()
                var numJeu: Long
                println("Voici la liste de nos jeux. Entrez le numéro du jeu que vous voulez acheter : ")
                println(catalogue_jeux)
                do {
                    numJeu = scanner.nextLine().toLongOrNull() ?: -1
                    if(!(catalogue_jeux.contains(numJeu))){}

                } while(!(catalogue_jeux.contains(numJeu)))

                var support: String
                println("Choisissez une plateforme support : ")
                do {
                    support = scanner.nextLine()
                    if(support.isEmpty()){
                        println("Champ vide interdit")
                    }

                } while(support.isEmpty())


                println("Achat en cours...")
                Thread.sleep(2000)
                //productionAchatJeu(PlayerCache.getId(), numJeu, support, ..., ...) A COMPLETER

                println("Jeu acheté ! Vous pouvez désormais y jouer !")
                Thread.sleep(2000)
                clearScreen()
            }

            else if(reponse2.toInt() == 6){}

            else if(reponse2.toInt() == 7){}

            else{}

        }
        //Première requête
        else {
            var reponse1: String
            do {
                //Première requête
                println("Souhaitez vous : ")
                println("\t- 1 : Vous connecter ?")
                println("\t- 2 : Vous inscrire ?")
                println("\t- 3 : Quitter ?")

                //Attente de la réponse à la première requête
                println("Entrez ici : ")
                reponse1 = scanner.nextLine()
                clearScreen()
            } while (reponse1.toIntOrNull() !in 1..3)

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
                    Thread.sleep(2000)
                    clearScreen()
                }
            } else if (reponse1.toInt() == 2) {
                println("Procédure d'inscription : \n")

                //Demande du nom
                var nom: String
                do {
                    println("Entrez votre nom : ")
                    nom = scanner.nextLine()
                    if(nom == "") println("Nom vide interdit !")
                } while (nom == "")

                //Demande du prénom
                var prenom: String
                do {
                    println("Entrez votre prénom : ")
                    prenom = scanner.nextLine()
                    if(prenom == "") println("Prénom vide interdit !")
                } while (prenom == "")

                //Demande de la date de naissance
                var dateDeNaissance: String
                do {
                    println("Entrez votre date de naissance (format : jj/mm/yyyy) : ")
                    dateDeNaissance = scanner.nextLine()
                    val isFormatValid = dateDeNaissance.matches("\\d{2}/\\d{2}/\\d{4}".toRegex())
                    if (!isFormatValid) println("Date invalide. Veuillez respecter le format jj/mm/aaaa.")

                } while (!isFormatValid)

                //Demande du mail
                var email: String
                do {
                    println("Entrez votre email : ")
                    email = scanner.nextLine()
                    if(email.isEmpty() || !(email.contains("@"))) println("Email invalide.")
                } while (email.isEmpty() || !(email.contains("@")))

                //Demande du pseudo
                var pseudo: String
                val listePseudos = recupererPseudos()
                do {
                    println("Entrez un pseudo : ")
                    pseudo = scanner.nextLine()
                    if (listePseudos.contains(pseudo)) println("Ce pseudo existe déjà")
                } while (listePseudos.contains(pseudo))

                //Demande du mot de passe
                var motDePasse: String
                do {
                    println("Entrez un mot de passe : ")
                    motDePasse = scanner.nextLine()
                    if(motDePasse.length < 8) println("Votre mot de passe doit avoir au moins 8 caractères")
                } while (motDePasse.length < 8)

                //Création du compte Joueur puis envoi dans Kafka
                productionCompteJoueur(pseudo, nom, prenom, email, motDePasse, dateDeNaissance)
                println("Création de compte réussie !")

                //Fin de la procédure
                println("Retour au menu précédent pour vous connecter")
                Thread.sleep(2000)
                clearScreen()
            } else {
                println("A bientôt !")
                Thread.sleep(2000)
                clearScreen()
                isFinished = true
            }
        }











    }
}