package com.projet_JVM2_DATA

import com.example.events.ReponseAuthentificationJoueur
import java.util.concurrent.CompletableFuture

object AuthSync {
    // Cette variable va stocker la "promesse" d'une réponse future
    var futureReponse: CompletableFuture<ReponseAuthentificationJoueur>? = null

    // Prépare l'attente d'une nouvelle réponse
    fun initExpectation() {
        futureReponse = CompletableFuture()
    }
}

object ReviewSync {
    var futureReponse: CompletableFuture<com.example.events.ReponseListeEvaluations>? = null

    fun initExpectation() {
        futureReponse = CompletableFuture()
    }
}