package com.projet_JVM2_DATA.producer;

import com.example.events.CreationCompteJoueur;

import java.time.Instant;


//TODO : à supprimer

/**
 * Test pour InscriptionCompteConsumer
 */
public class TestEnvoi {
    public static void main(String[] args) {
        InscriptionProducer producer = new InscriptionProducer();

        // On utilise le Builder généré par Avro
        CreationCompteJoueur nouveauJoueur = CreationCompteJoueur.newBuilder()
                .setNom("Dupont")
                .setPrenom("Jean")
                .setEmail("jean@test.fr")
                .setPseudo("JD")
                .setMotDePasse("pass123")
                .setDateDeNaissance("1995-10-10")
                .setDateDeCreationDuCompte(Instant.ofEpochSecond(Instant.now().toEpochMilli()))
                .build();

        producer.envoyerInscription(nouveauJoueur);

        // On attend un peu pour laisser le temps au thread réseau d'envoyer
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        producer.close();
    }
}