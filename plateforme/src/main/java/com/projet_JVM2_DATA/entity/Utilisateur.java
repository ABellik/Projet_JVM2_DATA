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
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "pseudo", nullable = false, unique = true)
    private String pseudo;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "mdp", nullable = false)
    private String mdp;

    @ManyToMany
    @JoinTable(
            name = "\"Amitié\"", // Nom exact de la table intermédiaire
            joinColumns = @JoinColumn(name = "joueur_1_id"), // Colonne qui pointe vers CE joueur (moi)
            inverseJoinColumns = @JoinColumn(name = "joueur_2_id") // Colonne qui pointe vers L'AMI
    )
    private Set<Utilisateur> amis = new HashSet<>();

    // Getters et méthode utilitaire
    public Set<Utilisateur> getAmis() {return amis;}

    public void ajouterAmi(Utilisateur ami) {
        this.amis.add(ami);
        ami.getAmis().add(this); // Si l'amitié est symétrique
    }

    public void retirerAmi(Utilisateur ancienAmi) {
        ancienAmi.getAmis().remove(this);
        this.amis.remove(ancienAmi);
    }

    public Utilisateur() {}
    public Utilisateur(String nom, String prenom, String pseudo, String mail, String mdp, LocalDate dateInscription, LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.mail = mail;
        this.mdp = mdp;
        this.dateInscription = dateInscription;
        this.dateNaissance = dateNaissance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
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

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMail() {return mail;}

    public void setMail(String mail) {this.mail = mail;}

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

}