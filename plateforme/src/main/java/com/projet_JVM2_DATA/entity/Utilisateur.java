package com.projet_JVM2_DATA.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"Utilisateur\"")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pseudo", nullable = false)
    private String pseudo;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    //On utilise cette méthode puisqu'aucune autre colonne n'est stockée dans la table Amitié
    @ManyToMany
    @JoinTable(
            name = "\"Amitié\"", // Nom exact de la table intermédiaire
            joinColumns = @JoinColumn(name = "joueur_1_id"), // Colonne qui pointe vers CE joueur (moi)
            inverseJoinColumns = @JoinColumn(name = "joueur_2_id") // Colonne qui pointe vers L'AMI
    )
    private Set<Utilisateur> amis = new HashSet<>();

    // Getters et méthode utilitaire
    public Set<Utilisateur> getAmis() { return amis; }

    public void ajouterAmi(Utilisateur ami) {
        this.amis.add(ami);
        ami.getAmis().add(this); // Si l'amitié est symétrique
    }

    public Utilisateur() {}
    public Utilisateur(String pseudo, String nom, String prenom, LocalDate dateNaissance, LocalDate dateInscription) {
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.dateInscription = dateInscription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

}