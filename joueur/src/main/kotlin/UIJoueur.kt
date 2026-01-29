package com.projet_JVM2_DATA

/*import com.projet_JVM2_DATA.cache.*
import com.projet_JVM2_DATA.consumers.startBackgroundConsumers
import com.projet_JVM2_DATA.producers.*
import java.util.Scanner
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// =========================================================================================
// OUTILS D'INTERFACE (Esthétique & Affordance)
// =========================================================================================

object ConsoleUI {
    // Codes couleurs ANSI
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val PURPLE = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE_BOLD = "\u001B[1;37m"

    fun clear() {
        // Tente un clear ANSI, sinon fallback sur les sauts de ligne
        print("\u001B[H\u001B[2J")
        System.out.flush()
        repeat(50) { println() }
    }

    fun printLogo() {
        println(PURPLE + """
        ╔═════════════════════════════════════════════════════════╗
        ║                 SIMULATEUR JOUEUR v1.0                  ║
        ╚═════════════════════════════════════════════════════════╝
        """ + RESET)
    }

    fun header(title: String) {
        println("\n" + CYAN + "=== " + title.uppercase() + " ===" + RESET)
        println(CYAN + "-".repeat(title.length + 8) + RESET)
    }

    fun menu(options: List<String>) {
        println()
        options.forEachIndexed { index, option ->
            println(WHITE_BOLD + "  [${index + 1}]" + RESET + " $option")
        }
        println()
    }

    fun prompt(message: String = "Votre choix"): String {
        print(YELLOW + "$message > " + RESET)
        return Scanner(System.`in`).nextLine()
    }

    fun success(msg: String) {
        println(GREEN + "✔ $msg" + RESET)
    }

    fun error(msg: String) {
        println(RED + "✘ $msg" + RESET)
    }

    fun info(msg: String) {
        println(BLUE + "ℹ $msg" + RESET)
    }

    fun loader(message: String, durationMs: Long = 2000) {
        print(PURPLE + "$message " + RESET)
        val steps = 10
        val sleepTime = durationMs / steps
        for (i in 0 until steps) {
            print(".")
            Thread.sleep(sleepTime)
        }
        println()
    }

    fun recupererPseudos(): MutableList<String> {
        return PlayerCache.getAllPlayers().map { it.pseudo }.toMutableList()
    }
}

// =========================================================================================
// LOGIQUE MÉTIER ( inchangée, juste rhabillée )
// =========================================================================================

fun main() {
    ConsoleUI.clear()
    ConsoleUI.printLogo()
    ConsoleUI.loader("Démarrage du système", 1000)

    startBackgroundConsumers()

    var isFinished = false
    val scanner = Scanner(System.`in`)

    ConsoleUI.success("Système prêt !")
    Thread.sleep(500)
    ConsoleUI.clear()

    //***************************************************** BOUCLE DE RENDU *****************************************************
    while (!isFinished) {
        ConsoleUI.printLogo()

        //------------------------------------------------- PARTIE CONNECTÉ -------------------------------------------------
        if (PlayerCache.isConnected()) {
            ConsoleUI.header("Bonjour, ${PlayerCache.getPseudo()} !")
            ConsoleUI.info("Que souhaitez-vous faire aujourd'hui ?")

            var reponse2: String
            do {
                ConsoleUI.menu(listOf(
                    "Consulter le catalogue de jeux",
                    "Voir profil et bibliothèque",
                    "Jouer à un jeu",
                    "Dernières notifications",
                    "Acheter un jeu",
                    "Rechercher un joueur",
                    "Rechercher un éditeur",
                    "Se déconnecter / Quitter"
                ))
                reponse2 = ConsoleUI.prompt()
                if (reponse2.toIntOrNull() !in 1..8) ConsoleUI.error("Choix invalide, réessayez.")
                else ConsoleUI.clear()
            } while (reponse2.toIntOrNull() !in 1..8)

            val choix = reponse2.toInt()

            //......................................... CATALOGUE .........................................
            if (choix == 1) {
                ConsoleUI.header("CATALOGUE DE JEUX")
                val games = GameCatalogCache.getAllGames()
                if(games.isEmpty()) ConsoleUI.info("Catalogue vide pour le moment.")
                else {
                    println(String.format("%-5s | %-40s | %s", "ID", "NOM", "PRIX"))
                    println("-".repeat(60))
                    games.forEach { game ->
                        println(String.format(ConsoleUI.WHITE_BOLD + "%-5d" + ConsoleUI.RESET + " | %-40s | " + ConsoleUI.GREEN + "%.2f€" + ConsoleUI.RESET, game.id, game.name, game.price))
                    }
                }

                var reponse5: String
                do {
                    ConsoleUI.menu(listOf("Voir les évaluations d'un jeu", "Ajouter à la Wishlist", "Retour"))
                    reponse5 = ConsoleUI.prompt()
                } while (reponse5.toIntOrNull() !in 1..3)

                // Voir avis
                if (reponse5.toInt() == 1) {
                    val idJeuConsult = ConsoleUI.prompt("ID du jeu à consulter").toLongOrNull() ?: -1L
                    if (idJeuConsult != -1L) {
                        ReviewSync.initExpectation()
                        productionRequeteListeEvaluations(idJeuConsult)
                        ConsoleUI.loader("Récupération des avis")

                        try {
                            val reponse = ReviewSync.futureReponse!!.get(5, TimeUnit.SECONDS)
                            val listeAvis = reponse.evaluations

                            if (listeAvis.isEmpty()) {
                                ConsoleUI.info("Aucun avis pour ce jeu.")
                            } else {
                                ConsoleUI.header("AVIS POUR LE JEU ${reponse.idJeu}")
                                listeAvis.forEachIndexed { index, avis ->
                                    val stars = ConsoleUI.YELLOW + "★".repeat(avis.note) + ConsoleUI.RESET + "☆".repeat(5 - avis.note)
                                    println("\n[${index + 1}] $stars (${avis.note}/5) - v${avis.versionJeu}")
                                    if (avis.commentaire != null) println("    \"${avis.commentaire}\"")
                                }

                                var choixReaction: String
                                do {
                                    println()
                                    choixReaction = ConsoleUI.prompt("Numéro avis pour réagir (0 pour quitter)")
                                    val choixIndex = choixReaction.toIntOrNull() ?: 0

                                    if (choixIndex in 1..listeAvis.size) {
                                        val avisCible = listeAvis[choixIndex - 1]
                                        val utilite = ConsoleUI.prompt("Utile ? (1: Oui, 2: Non)")
                                        if (utilite == "1") productionReactionEvaluation(avisCible.idEvaluation, true)
                                        else if (utilite == "2") productionReactionEvaluation(avisCible.idEvaluation, false)
                                        ConsoleUI.success("Réaction envoyée !")
                                    }
                                } while (choixReaction != "0")
                            }
                        } catch (e: Exception) {
                            ConsoleUI.error("Erreur récupération avis: ${e.message}")
                        }
                    }
                }
                // Wishlist
                else if (reponse5.toInt() == 2) {
                    var numJeu: String
                    do {
                        numJeu = ConsoleUI.prompt("ID du jeu pour la Wishlist")
                        if (!GameCatalogCache.getGameIds().contains(numJeu.toLongOrNull())) {
                            ConsoleUI.error("ID inconnu.")
                        }
                    } while (!GameCatalogCache.getGameIds().contains(numJeu.toLongOrNull()))
                    productionAjoutWishlist(numJeu.toLong())
                    ConsoleUI.success("Ajouté à la wishlist !")
                    Thread.sleep(1000)
                }
            }

            //......................................... PROFIL & BIBLIO .........................................
            else if (choix == 2) {
                ConsoleUI.header("MON PROFIL")
                PlayerCache.printPlayerInfo() // Supposons que cette méthode imprime du texte brut, on le garde tel quel

                ConsoleUI.header("MA BIBLIOTHÈQUE")
                val gamesCurrentPlayer = PlayerCache.getGames()
                if (gamesCurrentPlayer.isEmpty()) {
                    ConsoleUI.info("Votre bibliothèque est vide.")
                } else {
                    gamesCurrentPlayer.forEach { idJeu ->
                        val game = GameCatalogCache.getGame(idJeu)
                        if(game != null)
                            println("🎮 ${ConsoleUI.WHITE_BOLD}${game.name}${ConsoleUI.RESET} (ID: $idJeu)")
                        else
                            println("🎮 Jeu ID $idJeu (Info non dispo)")
                    }

                    var reponse3: String
                    do {
                        ConsoleUI.menu(listOf("Évaluer un jeu", "Retour"))
                        reponse3 = ConsoleUI.prompt()
                    } while (reponse3.toIntOrNull() !in 1..2)

                    if (reponse3.toInt() == 1) {
                        var reponse4: String
                        do {
                            reponse4 = ConsoleUI.prompt("ID du jeu à évaluer")
                            if (reponse4.toLongOrNull() !in PlayerCache.getGames()) ConsoleUI.error("Vous ne possédez pas ce jeu.")
                        } while (reponse4.toLongOrNull() !in PlayerCache.getGames())

                        var noteJeu: String
                        do {
                            noteJeu = ConsoleUI.prompt("Note (1-5)")
                        } while (noteJeu.toIntOrNull() !in 1..5)

                        val commentaire = ConsoleUI.prompt("Commentaire")
                        productionEvaluationJeu(reponse4.toLong(), noteJeu.toInt(), commentaire)
                        ConsoleUI.success("Merci pour votre avis !")
                        Thread.sleep(1500)
                    }
                }
            }

            //......................................... JOUER .........................................
            else if (choix == 3) {
                ConsoleUI.header("LANCER UNE SESSION")
                if(PlayerCache.getGames().isEmpty()) {
                    ConsoleUI.error("Aucun jeu possédé.")
                } else {
                    println("Jeux disponibles : ${PlayerCache.getGames()}")
                    var numJeuchoisi: String
                    do {
                        numJeuchoisi = ConsoleUI.prompt("ID du jeu")
                    } while (!(PlayerCache.getGames().contains(numJeuchoisi.toLongOrNull())))

                    productionSession(numJeuchoisi.toLong())
                    ConsoleUI.loader("Lancement du jeu...", 1000)
                    ConsoleUI.header("JEU EN COURS")
                    ConsoleUI.info("Vous jouez à l'ID $numJeuchoisi...")
                    ConsoleUI.loader("Simulation de gameplay", 3000)
                    ConsoleUI.success("Session terminée. Sauvegarde en cours.")
                    Thread.sleep(1000)
                    ConsoleUI.clear()
                }
            }

            //......................................... ACTUALITÉS .........................................
            else if (choix == 4) {
                ConsoleUI.header("NOTIFICATIONS")
                val notifs = PlayerCache.getNotifications()
                if(notifs.isEmpty()) ConsoleUI.info("Aucune nouvelle notification.")
                else println(notifs) // Idéalement à formater ligne par ligne si c'est une liste
                ConsoleUI.prompt("Appuyez sur Entrée pour continuer...")
            }

            //......................................... ACHAT .........................................
            else if (choix == 5) {
                ConsoleUI.header("BOUTIQUE")
                val catalogueJeux = GameCatalogCache.getAllGames()
                if (catalogueJeux.isEmpty()) {
                    ConsoleUI.error("Catalogue inaccessible.")
                } else {
                    catalogueJeux.forEach { game ->
                        println("${ConsoleUI.WHITE_BOLD}${game.id}${ConsoleUI.RESET} - ${game.name} - ${ConsoleUI.GREEN}${game.price}€${ConsoleUI.RESET}")
                    }

                    var numJeu: Long
                    do {
                        numJeu = ConsoleUI.prompt("ID du jeu à acheter").toLongOrNull() ?: -1
                        if (GameCatalogCache.getGame(numJeu) == null) ConsoleUI.error("Jeu introuvable.")
                    } while (GameCatalogCache.getGame(numJeu) == null)


                    val jeuSelectionne = GameCatalogCache.getGame(numJeu)!!
                    val supportsDisponibles = jeuSelectionne.supports.toList()

                    var supportChoisi: String = ""

                    if (supportsDisponibles.isEmpty()) {
                        ConsoleUI.error("Erreur : Ce jeu n'a aucun support configuré.")
                        // Ici tu devrais probablement faire un 'continue' ou gérer le cas d'erreur
                    } else {
                        ConsoleUI.info("Ce jeu est disponible sur les plateformes suivantes :")
                        ConsoleUI.menu(supportsDisponibles)
                        var choixIndex: Int
                        do {
                            val input = ConsoleUI.prompt("Choisissez le numéro du support")
                            choixIndex = input.toIntOrNull() ?: -1

                            if (choixIndex !in 1..supportsDisponibles.size) {
                                ConsoleUI.error("Choix invalide. Veuillez entrer un numéro de la liste.")
                            }
                        } while (choixIndex !in 1..supportsDisponibles.size)
                        supportChoisi = supportsDisponibles[choixIndex - 1]
                    }

                    ConsoleUI.loader("Transaction bancaire", 2000)
                    productionAchatJeu(numJeu, supportChoisi)
                    PlayerCache.addGame(numJeu)
                    ConsoleUI.success("Achat confirmé ! Ajouté à la bibliothèque.")
                    Thread.sleep(1500)
                }
            }

            //......................................... QUITTER .........................................
            else if (choix == 8) {
                ConsoleUI.info("Déconnexion...")
                isFinished = true
            }
        }

        //------------------------------------------------- PARTIE NON CONNECTÉ -------------------------------------------------
        else {
            ConsoleUI.header("ACCUEIL VISITEUR")
            var reponse1: String
            do {
                ConsoleUI.menu(listOf("Se connecter", "S'inscrire", "Quitter"))
                reponse1 = ConsoleUI.prompt()
                if (reponse1.toIntOrNull() !in 1..3) ConsoleUI.clear()
            } while (reponse1.toIntOrNull() !in 1..3)

            //......................................... CONNEXION .........................................
            if (reponse1.toInt() == 1) {
                ConsoleUI.header("CONNEXION")
                val pseudo = ConsoleUI.prompt("Pseudo")

                AuthSync.initExpectation()
                productionRequeteAuthentificationJoueur(pseudo)
                ConsoleUI.loader("Vérification du compte")

                try {
                    val reponse = AuthSync.futureReponse!!.get(15, TimeUnit.SECONDS)

                    if (reponse.idJoueur != null) {
                        val motDePasseSaisi = ConsoleUI.prompt("Mot de passe")

                        if (reponse.motDePasse.toString() == motDePasseSaisi) {
                            PlayerCache.login(
                                reponse.idJoueur, reponse.pseudo, reponse.nom, reponse.prenom,
                                reponse.email, reponse.dateDeNaissance, reponse.dateDeCreationDuCompte,
                                reponse.motDePasse, reponse.games
                            )
                            ConsoleUI.success("Bienvenue ${reponse.pseudo} !")
                            Thread.sleep(1000)
                        } else {
                            ConsoleUI.error("Mot de passe incorrect.")
                            Thread.sleep(1500)
                        }
                    } else {
                        ConsoleUI.error("Ce pseudo n'existe pas.")
                        Thread.sleep(1500)
                    }
                } catch (e: TimeoutException) {
                    ConsoleUI.error("Serveur injoignable.")
                } catch (e: Exception) {
                    ConsoleUI.error("Erreur technique: ${e.message}")
                }
                ConsoleUI.clear()
            }
            //......................................... INSCRIPTION .........................................
            else if (reponse1.toInt() == 2) {
                ConsoleUI.header("NOUVELLE INSCRIPTION")

                var nom: String
                do { nom = ConsoleUI.prompt("Nom") } while (nom.isEmpty())

                var prenom: String
                do { prenom = ConsoleUI.prompt("Prénom") } while (prenom.isEmpty())

                var dateDeNaissance: String
                do {
                    dateDeNaissance = ConsoleUI.prompt("Date de naissance (aaaa-mm-jj)")
                    if (!dateDeNaissance.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) ConsoleUI.error("Format invalide.")
                } while (!dateDeNaissance.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()))

                var email: String
                do {
                    email = ConsoleUI.prompt("Email")
                    if (email.isEmpty() || !email.contains("@")) ConsoleUI.error("Email invalide.")
                } while (email.isEmpty() || !email.contains("@"))

                val listePseudos = ConsoleUI.recupererPseudos()
                var pseudo: String
                do {
                    pseudo = ConsoleUI.prompt("Choisissez un Pseudo")
                    if (listePseudos.contains(pseudo)) ConsoleUI.error("Déjà pris !")
                } while (listePseudos.contains(pseudo))

                var motDePasse: String
                do {
                    motDePasse = ConsoleUI.prompt("Mot de passe (8 car. min)")
                    if (motDePasse.length < 8) ConsoleUI.error("Trop court.")
                } while (motDePasse.length < 8)

                ConsoleUI.loader("Création du compte")
                productionCompteJoueur(pseudo, nom, prenom, email, motDePasse, dateDeNaissance)
                ConsoleUI.success("Compte créé avec succès !")
                Thread.sleep(1500)
                ConsoleUI.clear()
            } else {
                ConsoleUI.info("Fermeture de l'application. À bientôt !")
                isFinished = true
            }
        }
    }
}*/