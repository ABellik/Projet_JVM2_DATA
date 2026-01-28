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

