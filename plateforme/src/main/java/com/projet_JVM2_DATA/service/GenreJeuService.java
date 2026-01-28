package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.entity.Genrejeu;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;

public class GenreJeuService extends MainService {

    public void creer(Genrejeu genreJeu) {
        executeInTransaction(em -> {
            GenreJeuRepository repository = new GenreJeuRepository(em);
            repository.save(genreJeu);
        });
    }

}