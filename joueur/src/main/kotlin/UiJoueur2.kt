package com.projet_JVM2_DATA

import com.projet_JVM2_DATA.cache.*
import com.projet_JVM2_DATA.consumers.startBackgroundConsumers
import com.projet_JVM2_DATA.producers.*
import java.util.Scanner
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.system.exitProcess

// =========================================================================================
// OUTILS D'INTERFACE
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
        print("\u001B[H\u001B[2J")
        System.out.flush()
        // Fallback pour les terminaux qui ne supportent pas le code ANSI
        repeat(50) { println() }
    }

    fun printLogo() {
        println(PURPLE + """
        ╔═════════════════════════════════════════════════════════╗
        ║                 SIMULATEUR JOUEUR v2.0                  ║
        ╚═════════════════════════════════════════════════════════╝
        """ + RESET)
    }

    fun header(title: String) {
        println("\n" + CYAN + "=== " + title.uppercase() + " ===" + RESET)
        println(CYAN + "-".repeat(title.length + 8) + RESET)
    }

    /**
     * Affiche un menu et attend une réponse valide.
     * @param options Liste des choix
     * @param allowBack Ajoute automatiquement une option "Retour" ou "Quitter" à la fin (0)
     */
    fun menu(options: List<String>, allowBack: Boolean = true): Int {
        println()
        options.forEachIndexed { index, option ->
            println(WHITE_BOLD + "  [${index + 1}]" + RESET + " $option")
        }
        if (allowBack) {
            println(YELLOW + "  [0]" + RESET + " Retour / Quitter")
        }
        println()

        var choix: Int?
        do {
            val input = prompt("Votre choix")
            choix = input.toIntOrNull()

            // Validation
            val max = options.size
            val min = if (allowBack) 0 else 1

            if (choix == null || choix !in min..max) {
                error("Choix invalide.")
            }
        } while (choix == null || choix !in (if (allowBack) 0 else 1)..options.size)

        return choix
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

    fun loader(message: String, durationMs: Long = 1000) {
        print(PURPLE + "$message " + RESET)
        val steps = 5
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
// POINT D'ENTRÉE PRINCIPAL
// =========================================================================================

fun main() {
    ConsoleUI.clear()
    ConsoleUI.printLogo()
    ConsoleUI.loader("Démarrage du système", 1000)
    startBackgroundConsumers()
    ConsoleUI.success("Système prêt !")
    Thread.sleep(500)

    // Boucle principale de l'application
    while (true) {
        ConsoleUI.clear()
        ConsoleUI.printLogo()

        if (PlayerCache.isConnected()) {
            menuJoueurConnecte()
        } else {
            menuVisiteur()
        }
    }
}

// =========================================================================================
// MENUS PRINCIPAUX
// =========================================================================================

fun menuVisiteur() {
    ConsoleUI.header("ACCUEIL VISITEUR")

    // Le menu retourne l'int choisi (1, 2 ou 0)
    val choix = ConsoleUI.menu(listOf("Se connecter", "S'inscrire"))

    when (choix) {
        1 -> featureConnexion()
        2 -> featureInscription()
        0 -> {
            ConsoleUI.info("Fermeture de l'application. À bientôt !")
            exitProcess(0) // Arrêt total
        }
    }
}

fun menuJoueurConnecte() {
    ConsoleUI.header("ESPACE JOUEUR : ${PlayerCache.getPseudo()}")

    val choix = ConsoleUI.menu(listOf(
        "Consulter le catalogue",      // 1
        "Mon profil & Bibliothèque",   // 2
        "Jouer à un jeu",              // 3
        "Notifications",               // 4
        "Acheter un jeu",              // 5
        "Se déconnecter"               // 6
        // 0 est ajouté auto pour "Quitter"
    ))

    when (choix) {
        1 -> featureCatalogue()
        2 -> featureProfil()
        3 -> featureJouer()
        4 -> featureNotifications()
        5 -> featureAchat()
        6 -> {
            PlayerCache.logout()
            ConsoleUI.success("Déconnexion réussie.")
            Thread.sleep(1000)
        }
        0 -> {
            ConsoleUI.info("Fermeture de l'application.")
            exitProcess(0)
        }
    }
}

// =========================================================================================
// FEATURES (FONCTIONNALITÉS)
// =========================================================================================

fun featureConnexion() {
    ConsoleUI.clear()
    ConsoleUI.header("CONNEXION")

    val pseudo = ConsoleUI.prompt("Pseudo (ou 0 pour retour)")
    if (pseudo == "0") return // Retour menu précédent

    AuthSync.initExpectation()
    productionRequeteAuthentificationJoueur(pseudo)
    ConsoleUI.loader("Vérification...")

    try {
        val reponse = AuthSync.futureReponse!!.get(10, TimeUnit.SECONDS)

        if (reponse.idJoueur != null) {
            val motDePasseSaisi = ConsoleUI.prompt("Mot de passe")
            if (reponse.motDePasse.toString() == motDePasseSaisi) {
                PlayerCache.login(
                    reponse.idJoueur, reponse.pseudo, reponse.nom, reponse.prenom,
                    reponse.email, reponse.dateDeNaissance, reponse.dateDeCreationDuCompte,
                    reponse.motDePasse, reponse.games
                )
                ConsoleUI.success("Connexion réussie !")
                Thread.sleep(1000)
            } else {
                ConsoleUI.error("Mot de passe incorrect.")
                Thread.sleep(1500)
            }
        } else {
            ConsoleUI.error("Compte introuvable.")
            Thread.sleep(1500)
        }
    } catch (e: Exception) {
        ConsoleUI.error("Erreur ou Timeout: ${e.message}")
        Thread.sleep(1500)
    }
}

fun featureInscription() {
    ConsoleUI.clear()
    ConsoleUI.header("INSCRIPTION")
    println("Tapez '0' à tout moment pour annuler.")

    // Helper pour annuler si l'user tape 0
    fun ask(msg: String, validator: (String) -> Boolean = { true }, errMsg: String = ""): String? {
        var input: String
        do {
            input = ConsoleUI.prompt(msg)
            if (input == "0") return null
            if (!validator(input)) ConsoleUI.error(errMsg)
        } while (!validator(input))
        return input
    }

    val nom = ask("Nom", { it.isNotEmpty() }, "Ne peut pas être vide") ?: return
    val prenom = ask("Prénom", { it.isNotEmpty() }, "Ne peut pas être vide") ?: return
    val dateNaiss = ask("Date de naissance (aaaa-mm-jj)",
        { it.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()) }, "Format invalide") ?: return
    val email = ask("Email", { it.contains("@") }, "Email invalide") ?: return

    val listePseudos = ConsoleUI.recupererPseudos()
    val pseudo = ask("Pseudo", { !listePseudos.contains(it) }, "Ce pseudo est déjà pris") ?: return

    val mdp = ask("Mot de passe (8 car. min)", { it.length >= 8 }, "Trop court") ?: return

    ConsoleUI.loader("Création du compte...")
    productionCompteJoueur(pseudo, nom, prenom, email, mdp, dateNaiss)
    ConsoleUI.success("Compte créé ! Connectez-vous maintenant.")
    Thread.sleep(2000)
}

fun featureCatalogue() {
    // Boucle pour rester dans le catalogue tant qu'on ne fait pas "Retour"
    while (true) {
        ConsoleUI.clear()
        ConsoleUI.header("CATALOGUE")

        val games = GameCatalogCache.getAllGames()
        if (games.isEmpty()) {
            ConsoleUI.info("Catalogue vide ou indisponible.")
        } else {
            println(String.format("%-5s | %-40s | %s", "ID", "NOM", "PRIX"))
            println("-".repeat(60))
            games.forEach { game ->
                println(String.format(ConsoleUI.WHITE_BOLD + "%-5d" + ConsoleUI.RESET + " | %-40s | " + ConsoleUI.GREEN + "%.2f€" + ConsoleUI.RESET, game.id, game.name, game.price))
            }
        }

        val choix = ConsoleUI.menu(listOf("Détails & Avis d'un jeu", "Ajouter à la Wishlist"))

        when (choix) {
            1 -> sousMenuAvis()
            2 -> sousMenuWishlist()
            0 -> return // Retour au menu principal
        }
    }
}

fun sousMenuAvis() {
    val idInput = ConsoleUI.prompt("ID du jeu (0 pour annuler)").toLongOrNull() ?: 0L
    if (idInput == 0L) return

    if (GameCatalogCache.getGame(idInput) == null) {
        ConsoleUI.error("Jeu introuvable.")
        Thread.sleep(1000)
        return
    }

    ReviewSync.initExpectation()
    productionRequeteListeEvaluations(idInput)
    ConsoleUI.loader("Chargement avis...")

    try {
        val reponse = ReviewSync.futureReponse!!.get(5, TimeUnit.SECONDS)
        ConsoleUI.clear()
        ConsoleUI.header("AVIS : JEU #${reponse.idJeu}")

        if (reponse.evaluations.isEmpty()) {
            ConsoleUI.info("Aucun avis pour ce jeu.")
        } else {
            reponse.evaluations.forEachIndexed { idx, avis ->
                val stars = ConsoleUI.YELLOW + "★".repeat(avis.note) + ConsoleUI.RESET + "☆".repeat(5 - avis.note)
                println("[${idx + 1}] $stars (${avis.note}/5) : \"${avis.commentaire ?: ""}\"")
            }

            // Interaction avec les avis
            val choixAvis = ConsoleUI.menu(listOf("Réagir à un avis"), allowBack = true)
            if (choixAvis == 1) {
                val numAvis = ConsoleUI.prompt("Numéro de l'avis").toIntOrNull() ?: 0
                if (numAvis in 1..reponse.evaluations.size) {
                    val utile = ConsoleUI.prompt("Utile ? (1=Oui, 2=Non)")
                    val isUseful = utile == "1"
                    productionReactionEvaluation(reponse.evaluations[numAvis-1].idEvaluation, isUseful)
                    ConsoleUI.success("Réaction envoyée.")
                    Thread.sleep(1000)
                }
            }
        }
        ConsoleUI.prompt("Appuyez sur Entrée pour revenir au catalogue...")
    } catch (e: Exception) {
        ConsoleUI.error("Erreur récupération avis.")
        Thread.sleep(1000)
    }
}

fun sousMenuWishlist() {
    val id = ConsoleUI.prompt("ID du jeu à ajouter (0 pour annuler)").toLongOrNull() ?: 0L
    if (id == 0L) return

    if (GameCatalogCache.getGame(id) != null) {
        productionAjoutWishlist(id)
        ConsoleUI.success("Ajout wishlist envoyé !")
    } else {
        ConsoleUI.error("ID inconnu.")
    }
    Thread.sleep(1000)
}

fun featureProfil() {
    while (true) {
        ConsoleUI.clear()
        ConsoleUI.header("MON PROFIL")
        PlayerCache.printPlayerInfo()

        ConsoleUI.header("MA BIBLIOTHÈQUE")
        val myGames = PlayerCache.getGames()
        if (myGames.isEmpty()) {
            ConsoleUI.info("Vous ne possédez aucun jeu.")
        } else {
            myGames.forEach { id ->
                val g = GameCatalogCache.getGame(id)
                val nom = g?.name ?: "Inconnu"
                println(" - [ID: $id] $nom")
            }
        }

        val choix = ConsoleUI.menu(listOf("Évaluer un jeu possédé"))

        if (choix == 1) {
            val idJeu = ConsoleUI.prompt("ID du jeu (0 retour)").toLongOrNull() ?: 0L
            if (idJeu == 0L) continue // Retour début boucle profil

            if (idJeu in myGames) {
                var note: Int?
                do {
                    note = ConsoleUI.prompt("Note (1-5)").toIntOrNull()
                } while (note == null || note !in 1..5)

                val comm = ConsoleUI.prompt("Commentaire")
                productionEvaluationJeu(idJeu, note!!, comm)
                ConsoleUI.success("Évaluation envoyée !")
                Thread.sleep(1000)
            } else {
                ConsoleUI.error("Vous ne possédez pas ce jeu ou ID invalide.")
                Thread.sleep(1000)
            }
        } else if (choix == 0) {
            return // Retour menu principal
        }
    }
}

fun featureJouer() {
    ConsoleUI.clear()
    ConsoleUI.header("JOUER")

    val myGames = PlayerCache.getGames()
    if (myGames.isEmpty()) {
        ConsoleUI.error("Bibliothèque vide. Achetez un jeu d'abord.")
        Thread.sleep(2000)
        return
    }

    println("Jeux disponibles :")
    myGames.forEach { id ->
        println(" - ${GameCatalogCache.getGame(id)?.name ?: id} (ID: $id)")
    }

    val idInput = ConsoleUI.prompt("ID du jeu à lancer (0 pour retour)").toLongOrNull() ?: 0L
    if (idInput == 0L) return

    if (idInput in myGames) {
        val jeu = GameCatalogCache.getGame(idInput)

        // --- DEBUT AJOUT : SÉLECTION DU SUPPORT ---
        if (jeu == null) {
            ConsoleUI.error("Erreur: infos jeu introuvables.")
            return
        }

        val supports = jeu.supports.toList()
        var supportChoisi = "Inconnu"

        if (supports.isNotEmpty()) {
            ConsoleUI.info("Sur quelle plateforme voulez-vous jouer ?")
            // On demande de choisir le support
            val choixSupport = ConsoleUI.menu(supports, allowBack = true)
            if (choixSupport == 0) return // Annulation

            supportChoisi = supports[choixSupport - 1]
        } else {
            // Cas de secours si aucun support n'est défini (peu probable avec ta correction précédente)
            ConsoleUI.error("Aucun support détecté pour ce jeu.")
            return
        }
        // --- FIN AJOUT ---

        // On passe maintenant l'ID ET le support
        productionSession(idInput, supportChoisi)

        ConsoleUI.loader("Lancement du jeu sur $supportChoisi...", 1000)
        ConsoleUI.header("SESSION EN COURS")
        ConsoleUI.info("Vous jouez à ${jeu.name}...")

        ConsoleUI.loader("Gameplay en cours", 3000)

        ConsoleUI.success("Fin de session. Sauvegarde...")
        Thread.sleep(1500)
    } else {
        ConsoleUI.error("Jeu non possédé.")
        Thread.sleep(1000)
    }
}

fun featureNotifications() {
    ConsoleUI.clear()
    ConsoleUI.header("NOTIFICATIONS")
    val notifs = PlayerCache.getNotifications()
    if (notifs.isEmpty()) ConsoleUI.info("Rien à signaler.")
    else {
        notifs.forEach { println("📩 $it") }
    }
    ConsoleUI.prompt("Appuyez sur Entrée pour retour...")
}

fun featureAchat() {
    ConsoleUI.clear()
    ConsoleUI.header("ACHAT DE JEU")

    // Affichage compact du catalogue
    GameCatalogCache.getAllGames().forEach {
        println("[${it.id}] ${it.name} (${String.format("%.2f", it.price)}€)")
    }

    val numJeu = ConsoleUI.prompt("ID du jeu à acheter (0 pour annuler)").toLongOrNull() ?: 0L
    if (numJeu == 0L) return

    val jeu = GameCatalogCache.getGame(numJeu)
    if (jeu == null) {
        ConsoleUI.error("ID inconnu.")
        Thread.sleep(1000)
        return
    }

    // Gestion des supports
    println("TESSSSSSST : "+jeu.supports.toList())
    val supports = jeu.supports.toList()
    if (supports.isEmpty()) {
        ConsoleUI.error("Erreur technique: Aucun support dispo pour ce jeu.")
        Thread.sleep(1500)
        return
    }

    ConsoleUI.info("Supports disponibles pour ${jeu.name} :")
    // On réutilise le menu générique, avec option retour (0)
    // attention, supports est List<String>, menu renvoie l'index + 1
    val choixSupport = ConsoleUI.menu(supports, allowBack = true)

    if (choixSupport == 0) return // Annulation lors du choix du support

    val supportChoisi = supports[choixSupport - 1]

    ConsoleUI.loader("Paiement en cours (${String.format("%.2f", jeu.price)}€)...", 2000)
    productionAchatJeu(numJeu, supportChoisi)
    PlayerCache.addGame(numJeu) // Simulation ajout local immédiat (optimiste)
    ConsoleUI.success("Achat validé ! Jeu ajouté à la bibliothèque.")
    Thread.sleep(2000)
}