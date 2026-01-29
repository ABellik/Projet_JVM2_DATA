package com.projet_JVM2_DATA.service;

import com.projet_JVM2_DATA.config.JpaUtil;
import com.projet_JVM2_DATA.entity.Genrejeu;
import com.projet_JVM2_DATA.entity.Jeu;
import com.projet_JVM2_DATA.repository.GenreJeuRepository;
import com.projet_JVM2_DATA.repository.JeuRepository;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class GenreJeuService extends MainService {

    public void creer(Jeu jeu, String genre) {
        Genrejeu genreJeu = new Genrejeu(jeu,genre);
        executeInTransaction(em -> {
            GenreJeuRepository repository = new GenreJeuRepository(em);
            repository.save(genreJeu);
        });
    }

    // TODO : supprimer (POURQUOI ??)

    public List<String> getGenresByIdJeu(Long idJeu) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            GenreJeuRepository repository = new GenreJeuRepository(em);

            List<String> genres = repository.findByIdJeu(idJeu);

            if (genres == null || genres.isEmpty()) {
                return new ArrayList<>();
            }
            return genres;

        } finally {
            em.close();
        }
    }

}