package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddlc")
    private Jeu iddlc;

    @Column(name = "\"durée\"", nullable = false)
    private Long durée;

    @Column(name = "type", nullable = false)
    private TypeSession type;

    public Session(){}
    public Session(Utilisateur utilisateur, Plateforme plateforme, Jeu jeu, Jeu dlc, TypeSession type) {
        this.idUtilisateur = utilisateur;
        this.idPlateforme = plateforme;
        this.idjeu = jeu;
        this.iddlc = dlc;
        this.type=type;
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

    public Jeu getIddlc() {
        return iddlc;
    }

    public void setIddlc(Jeu iddlc) {
        this.iddlc = iddlc;
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

/*
 TODO [Reverse Engineering] create field to map the 'type' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "type", columnDefinition = "type_session not null")
    private Object type;
*/
}