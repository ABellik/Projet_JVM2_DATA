package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.Genrejeu;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;

public class GenreJeuService extends MainService {

    public void creer(Jeu jeu, String genre) {
        Genrejeu genreJeu = new Genrejeu(jeu,genre);
        executeInTransaction(em -> {
            GenreJeuRepository repository = new GenreJeuRepository(em);
            repository.save(genreJeu);
        });
    }

    // TODO : supprimer

}