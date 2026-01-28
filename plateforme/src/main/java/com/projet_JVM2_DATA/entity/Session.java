package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

/**
 * Table contenant les sessions de jeux d'un joueur sur une plateforme
 * La durée stockée est en minutes
 */
@Entity
@Table(name = "\"Session\"")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idUtilisateur\"", nullable = false)
    private Utilisateur idUtilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"idPlateforme\"", nullable = false)
    private Plateforme idPlateforme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idjeu")
    private Jeu idjeu;

    @Column(name = "\"durée\"", nullable = false)
    private Long durée;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeSession type;

    public Session(){}
    public Session(Utilisateur utilisateur, Plateforme plateforme, Jeu jeu, long duree, TypeSession type) {
        this.idUtilisateur = utilisateur;
        this.idPlateforme = plateforme;
        this.idjeu = jeu;
        this.type=type;
        this.durée=duree;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Utilisateur idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Plateforme getIdPlateforme() {
        return idPlateforme;
    }

    public void setIdPlateforme(Plateforme idPlateforme) {
        this.idPlateforme = idPlateforme;
    }

    public Jeu getIdjeu() {
        return idjeu;
    }

    public void setIdjeu(Jeu idjeu) {
        this.idjeu = idjeu;
    }

    public Long getDurée() {
        return durée;
    }

    public void setDurée(Long durée) {
        this.durée = durée;
    }

    public TypeSession getType() {
        return type;
    }
    public void setType(TypeSession type) {
        this.type = type;
    }

}