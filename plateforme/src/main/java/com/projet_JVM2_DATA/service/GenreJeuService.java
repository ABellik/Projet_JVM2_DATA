package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.Genrejeu;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;

import java.util.List;

public class GenreJeuService extends MainService {

    public void creer(Jeu jeu, String genre) {
        Genrejeu genreJeu = new Genrejeu(jeu,genre);
        executeInTransaction(em -> {
            GenreJeuRepository repository = new GenreJeuRepository(em);
            repository.save(genreJeu);
        });
    }

    public void supprimerJeu(Jeu jeu) {
        executeInTransaction(em -> {
            GenreJeuRepository repository = new GenreJeuRepository(em);
            List<Genrejeu> list = repository.findByJeu(jeu);
            for (Genrejeu genreJeu : list) {
                repository.delete(genreJeu);
            }
        });
    }

}