package com.projet_JVM2_DATA.repository;

import com.projet_JVM2_DATA.entity.Modification;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ModificationRepository {
    private final EntityManager em;
    public ModificationRepository(EntityManager em) {
        this.em = em;
    }
    public void save(Modification modification) {
        em.persist(modification);
    }

    // TODO : à verifier pour l'id car le id est composé
    public Modification findById(Long id) {
        return em.find(Modification.class, id);
    }

    public List<Modification> findAll() {
        return em.createQuery("select m from Modification m", Modification.class).getResultList();
    }

    public List<Modification> findByPatch(long idPatch) {
        return em.createQuery("select m from Modification m where m.idpatch=:idPatch", Modification.class)
                .setParameter("idPatch", idPatch)
                .getResultList();
    }

    public void delete(Modification modification) {
        em.remove(modification);
    }
}
