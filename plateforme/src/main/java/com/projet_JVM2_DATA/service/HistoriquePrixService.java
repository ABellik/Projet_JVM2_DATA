package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.HistoriquePrix;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.repository.HistoriquePrixRepository;

import java.util.List;

public class HistoriquePrixService extends MainService {

    public void creer(Jeu jeu, Long ancienPrix, Long nouveauPrix, String commentaire) {
        HistoriquePrix historiquePrix = new HistoriquePrix(jeu, ancienPrix, nouveauPrix, commentaire);
        executeInTransaction(em -> {
            HistoriquePrixRepository repository = new HistoriquePrixRepository(em);
            repository.save(historiquePrix);
        });
    }

    private String genererCommentaire(double qualite, double demande) {

        StringBuilder commentaire = new StringBuilder();

        // Commentaire qualité et demande
        if (qualite > 0) {
            commentaire.append("Ce jeu est bien noté par nos utilisateurs !\n");
        }
        if (demande > 0) {
            commentaire.append("Ce jeu est actuellement populaire !\n");
        }

        return commentaire.toString();
    }

    private String genererCommentaire(double qualite, double demande, long prixActuel, long nouveauPrix) {

        StringBuilder commentaire = new StringBuilder(genererCommentaire(qualite,demande));

        // Commentaire variation
        if (prixActuel > 0) {

            long variation = (prixActuel-nouveauPrix)/prixActuel * 100;
            if (variation < 0) {
                commentaire.append("Profite de la réduction de (")
                        .append(Math.abs(Math.round(variation)))
                        .append("%) !");
            } else if (variation > 0) {
                commentaire.append("Le prix a augmenté (")
                        .append(Math.round(variation))
                        .append("%), dépêche-toi avant qu'il ne devienne trop cher !");
            } else {
                commentaire.append("Le prix du jeu est resté stable.");
            }
        }

        return commentaire.toString();
    }

    private double demandeJeuSemaine(Jeu jeu) {
        // TODO : get le nombre de demande
        return 0;
    }

    private double qualiteJeuPercue(Jeu jeu) {
        // TODO : get moyenne entre -1 et 1
        return 0;
    }

    public void majPrix(Jeu jeu){

        // Données de prix du jeu
        long prixEditeur = jeu.getPrixEditeur();
        long prixActuel = jeu.getPrixActuel();

        // Coefficients d'évolution
        double qualitePercue = qualiteJeuPercue(jeu);
        double demandeSemaine = demandeJeuSemaine(jeu);

        double coeffQualite = 0.2  * qualitePercue;  // entre +20% et -20%
        double coeffDemande = 0.05 * demandeSemaine; // +5% à chaque demande

        double coefficient = 1 + coeffQualite + coeffDemande;

        // Calcul du nouveau prix
        long nouveauPrix = (long) (prixEditeur * coefficient);
        jeu.setPrixActuel(nouveauPrix);

        // Calcul de la variation du prix
        String commentaire = genererCommentaire(
                qualitePercue,
                demandeSemaine,
                prixActuel,
                nouveauPrix
        );

        // Mise à jour du prix
        executeInTransaction(em -> {
            HistoriquePrixRepository repository = new HistoriquePrixRepository(em);

            // TODO : à voir selon la réponse si on crée un nouveau ou si on en fait un autre
            HistoriquePrix historique = new HistoriquePrix(
                    jeu,
                    prixActuel,
                    nouveauPrix,
                    commentaire
            );

            repository.save(historique);
        });
    }

    public void supprimerJeu(Jeu jeu) {
        executeInTransaction(em -> {
            HistoriquePrixRepository repository = new HistoriquePrixRepository(em);
            List<HistoriquePrix> list = repository.findByJeu(jeu);
            for(HistoriquePrix historiquePrix : list) {
                repository.delete(historiquePrix);
            }
        });
    }

    public void supprimer(Long idHistoriquePrix) {
        executeInTransaction(em -> {
            HistoriquePrixRepository repository = new HistoriquePrixRepository(em);
            HistoriquePrix historiquePrix = repository.findById(idHistoriquePrix);
            repository.delete(historiquePrix);
        });
    }
}

