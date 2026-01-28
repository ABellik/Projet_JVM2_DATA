package com.projet_JVM2_DATA

import com.projet_JVM2_DATA.cache.*
import com.projet_JVM2_DATA.consumers.startBackgroundConsumers
import com.projet_JVM2_DATA.producers.*
import java.util.Scanner
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Nettoie l'affichage de la console.
 *
 * Simule un nettoyage d'écran en imprimant 50 sauts de ligne successifs.
 * Utile pour rafraîchir le menu dans l'interface console d'IntelliJ.
 */
fun clearScreen() {
    repeat(50) { println() }
}

/**
 * Récupère la liste des pseudonymes existants.
 *
 * Cette fonction est utilisée pour vérifier l'unicité d'un pseudo lors de l'inscription
 * ou l'existence d'un compte lors de la connexion.
 *
 * @return [MutableList] de [String] contenant tous les pseudos enregistrés.
 * (Actuellement retourne une liste vide pour simulation).
 */
fun recupererPseudos(): MutableList<String> {
    return PlayerCache.getAllPlayers().map { it.pseudo }.toMutableList()
}

fun main() {
    //Démarrage de l'interface console du module Joueur
    println("Démarrage du simulateur Joueur...")
    Thread.sleep(2000)

    startBackgroundConsumers()

    //Initialisation des variables de l'interface
    var isFinished = false
    val scanner = Scanner(System.`in`)

    //Message de bienvenue
    println("Bienvenue !")

    //***************************************************** BOUCLE DE RENDU *****************************************************
    while (!isFinished) {

        //------------------------------------------------- PARTIE EXECUTEE SI LE JOUEUR EST CONNECTE -------------------------------------------------
        if(PlayerCache.isConnected()){
            println("Bienvenue "+ PlayerCache.getPseudo())

            var reponse2: String
            do {
                //Requête
                println("Souhaitez vous : ")
                println("\t- 1 : Consulter notre catalogue de jeux")
                println("\t- 2 : Voir votre profil et bibliothèque de jeux")
                println("\t- 3 : Jouer à un jeu")
                println("\t- 4 : Voir vos dernières informations")
                println("\t- 5 : Acheter un nouveau jeu")
                println("\t- 6 : Rechercher un autre joueur")
                println("\t- 7 : Rechercher un éditeur")
                println("\t- 8 : Quitter ?")

                //Attente de la réponse à la requête
                println("Entrez ici : ")
                reponse2 = scanner.nextLine()
                clearScreen()
            } while (reponse2.toIntOrNull() !in 1..8 )

            //......................................... CONSULTATION DU CATALOGUE .........................................
            if(reponse2.toInt() == 1){
                println("Voici la liste des jeux que nous avons actuellement : ")
                GameCatalogCache.getAllGames().forEach { game ->
                    println("${game.id} - ${game.name} - ${game.price}€")
                }
                var reponse5: String
                do {
                    println(
                        "Souhaitez-vous:" +
                                "\n\t1) Voir les évaluations d'un jeu" +
                                "\n\t2) Ajouter un jeu à la liste de souhaits" +
                                "\n\t3) Quitter"
                    )
                    reponse5 = scanner.nextLine()
                    if(reponse5.toIntOrNull() !in 1..3){
                        println("Choix interdit")
                    }
                } while(reponse5.toIntOrNull() !in 1..3)

                if(reponse5.toInt() == 1){
                    println("De quel jeu voulez-vous voir les avis ? (Entrez l'ID)")
                    val idJeuConsult = scanner.nextLine().toLongOrNull() ?: -1L

                    if (idJeuConsult != -1L) {
                        ReviewSync.initExpectation()
                        productionRequeteListeEvaluations(idJeuConsult)

                        try {
                            val reponse = ReviewSync.futureReponse!!.get(5, TimeUnit.SECONDS)
                            val listeAvis = reponse.evaluations

                            if (listeAvis.isEmpty()) {
                                println("Aucun avis pour ce jeu.")
                            } else {
                                println("\n--- Avis pour le jeu ${reponse.idJeu} ---")

                                listeAvis.forEachIndexed { index, avis ->
                                    val etoiles = "*".repeat(avis.note)
                                    println("[${index + 1}] $etoiles (${avis.note}/5)")
                                    println("    Version : ${avis.versionJeu}")
                                    if (avis.commentaire != null) {
                                        println("    Commentaire : \"${avis.commentaire}\"")
                                    }
                                }

                                var choixReaction: String
                                do {
                                    println("\nEntrez le numéro de l'avis pour réagir (ou 0 pour quitter) :")
                                    choixReaction = scanner.nextLine()
                                    val choixIndex = choixReaction.toIntOrNull() ?: 0

                                    if (choixIndex in 1..listeAvis.size) {
                                        val avisCible = listeAvis[choixIndex - 1]

                                        println("Cet avis est-il utile ? (1: Oui, 2: Non)")
                                        val utilite = scanner.nextLine()

                                        if (utilite == "1") {
                                            productionReactionEvaluation(avisCible.idEvaluation, true)
                                        } else if (utilite == "2") {
                                            productionReactionEvaluation(avisCible.idEvaluation, false)
                                        }
                                    }
                                } while (choixReaction != "0")
                            }
                        } catch (e: Exception) {
                            println("Erreur ou Timeout lors de la récupération des avis : ${e.message}")
                        }
                    }
                }
                else if(reponse5.toInt() == 2){
                    var numJeu: String
                    do{
                        println("Entrez le numéro du jeu que vous voulez ajouter à la liste de souhaits")
                        numJeu = scanner.nextLine()
                        if (!GameCatalogCache.getGameIds().contains(numJeu.toLongOrNull())){
                            println("Valeur interdite")
                        }
                    } while (!GameCatalogCache.getGameIds().contains(numJeu.toLongOrNull()))
                    productionAjoutWishlist(numJeu.toLong())
                    println("Ajout réussi !")
                    Thread.sleep(1500)
                }
            }
            //......................................... VUE PROFIL ET BIBLIOTHÈQUE .........................................
            else if(reponse2.toInt() == 2){
                PlayerCache.printPlayerInfo()

                val gamesCurrentPlayer = PlayerCache.getGames()
                if(gamesCurrentPlayer.isEmpty()){
                    println("Vous ne possédez pas de jeux")
                }
                else {
                    println("Votre liste de jeux : ")
                    gamesCurrentPlayer.forEach { idJeu ->
                        println("" +
                                "${idJeu} - " +
                                "${GameCatalogCache.getGame(idJeu)!!.name} - " +
                                "${GameCatalogCache.getGame(idJeu)!!.price}€"
                        )
                    }
                    var reponse3: String
                    do {
                        println(
                            "Souhaitez-vous : " +
                                    "\n\t 1) Évaluer un jeu" +
                                    "\n\t 2) Quitter"
                        )
                        reponse3 = scanner.nextLine()
                        if(reponse3.toIntOrNull() !in 1..2){
                            println("Réponse incorrecte !")
                        }
                    } while (reponse3.toIntOrNull() !in 1..2)
                    if(reponse3.toInt() == 1){
                        var reponse4: String
                        do {
                            println("Entrez le numéro du jeu à évaluer : ")
                            reponse4 = scanner.nextLine()
                            if(reponse4.toLongOrNull() !in PlayerCache.getGames()){
                                println("Numéro de jeu non valide !")
                            }
                        } while (reponse4.toLongOrNull() !in PlayerCache.getGames())
                        //A COMPLETER, VERIFIER QU'IL N'A PAS DEJA FAIT D'ÉVALUATION SUR CE JEU
                        var noteJeu: String
                        do {
                            println("Entrez une note entière (entre 1 et 5) : ")
                            noteJeu = scanner.nextLine()
                            if(noteJeu.toIntOrNull() !in 1..5) println("Valeur de note interdite")
                        } while(noteJeu.toIntOrNull() !in 1..5)
                        println("Entrez un commentaire : ")
                        val commentaire = scanner.nextLine()

                        productionEvaluationJeu(reponse4.toLong(), noteJeu.toInt(), commentaire)
                        println("Evaluation réussie !")
                        Thread.sleep(1500)

                    }
                }
            }
            //......................................... JOUER A UN JEU .........................................
            else if(reponse2.toInt() == 3){
                println("Choisissez un jeu auquel jouer : ")
                println(PlayerCache.getGames())
                var numJeuchoisi: String
                do{
                    numJeuchoisi = scanner.nextLine()
                    if(!(PlayerCache.getGames().contains(numJeuchoisi.toLongOrNull()))){
                        println("Numéro invalide")
                        println("Choisissez un jeu auquel jouer : ")
                    }
                } while (!(PlayerCache.getGames().contains(numJeuchoisi.toLongOrNull())))

                productionSession(numJeuchoisi.toLong())
                println("Session lancée !")
                println("Simulation du temps de jeu")
                Thread.sleep(2000)
                println("Session terminée !")
                clearScreen()
            }

            //......................................... VOIR ACTUALITÉS .........................................
            else if(reponse2.toInt() == 4){
                println("Voici vos notifications :")
                println(PlayerCache.getNotifications())
            }

            //......................................... ACHETER UN JEU .........................................
            else if(reponse2.toInt() == 5){
                val catalogueJeux = GameCatalogCache.getAllGames()

                if (catalogueJeux.isEmpty()) {
                    println("Catalogue vide. Veuillez réessayer dans quelques instants.")
                    Thread.sleep(2000)
                } else {
                    println("Voici la liste de nos jeux :")
                    catalogueJeux.forEach { game ->
                        println("${game.id} - ${game.name} - ${game.price}€")
                    }

                    var numJeu: Long
                    do {
                        print("Entrez le numéro du jeu : ")
                        numJeu = scanner.nextLine().toLongOrNull() ?: -1
                        if (GameCatalogCache.getGame(numJeu) == null) {
                            println("Jeu introuvable")
                        }
                    } while (GameCatalogCache.getGame(numJeu) == null)
                    var support: String
                    do {
                        println("Entrez le support de jeu : ")
                        support = scanner.nextLine()
                        if(support.isEmpty()) println("Champ vide interdit")
                    } while(support.isEmpty())

                    println("Achat en cours...")
                    Thread.sleep(2000)
                    productionAchatJeu(numJeu, support)
                    PlayerCache.addGame(numJeu)

                    println("Jeu acheté ! Vous pouvez désormais y jouer !")
                }
            }

            //......................................... CHERCHER UN JOUEUR .........................................
            else if(reponse2.toInt() == 6){}

            //......................................... CHERCHER UN ÉDITEUR .........................................
            else if(reponse2.toInt() == 7){}

            //......................................... QUITTER .........................................
            else{
                isFinished = true
            }

        }

        //------------------------------------------------- PARTIE EXECUTEE SI LE JOUEUR N'EST PAS CONNECTE -------------------------------------------------
        else {
            var reponse1: String
            do {
                //Requête
                println("Souhaitez vous : ")
                println("\t- 1 : Vous connecter ?")
                println("\t- 2 : Vous inscrire ?")
                println("\t- 3 : Quitter ?")

                //Attente de la réponse à la requête
                println("Entrez ici : ")
                reponse1 = scanner.nextLine()
                clearScreen()
            } while (reponse1.toIntOrNull() !in 1..3)

            //......................................... SE CONNECTER .........................................
            if (reponse1.toInt() == 1) {
                //Entrée du pseudo
                print("Pseudo : ")
                val pseudo = scanner.nextLine()

                AuthSync.initExpectation()

                productionRequeteAuthentificationJoueur(pseudo)

                try{
                    val reponse = AuthSync.futureReponse!!.get(15, TimeUnit.SECONDS)

                    if (reponse.idJoueur!=null){
                        print("Mot de passe : ")
                        val motDePasseSaisi = scanner.nextLine()

                        if(reponse.motDePasse.toString() == motDePasseSaisi){
                            PlayerCache.login(reponse.idJoueur, reponse.pseudo, reponse.nom, reponse.prenom, reponse.email, reponse.dateDeNaissance, reponse.dateDeCreationDuCompte, reponse.motDePasse, reponse.games)
                            println("Connecté avec succès !")
                            Thread.sleep(1500)
                        }
                        else{
                            println("Mot de passe incorrect")
                            Thread.sleep(1500)
                        }
                    }
                    else{
                        println("Ce pseudo n'existe pas")
                        Thread.sleep(1500)
                    }
                }
                catch(e: TimeoutException) {
                    println("ERREUR : La plateforme ne répond pas : ${e.message}")
                    Thread.sleep(1500)
                }
                catch(e: Exception) {
                    println("Erreur technique : ${e.message}")
                }

                clearScreen()
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