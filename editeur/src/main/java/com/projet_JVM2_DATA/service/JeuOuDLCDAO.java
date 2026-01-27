package com.projet_JVM2_DATA.service;

import com.example.events.PublicationJeuOuDLC;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JeuOuDLCDAO {

    private String url;
    private String username;
    private String password;


    public JeuOuDLCDAO(String url, String username, String mdp)
    {
        this.url=url;
        this.username=username;
        password=mdp;
    }

    public PublicationJeuOuDLC getDLCByID(long jeuId)
    {
        String requete= "Select * from Jeu where id=?";


        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             //Créer un statement précompilé avec la requête SQL
             PreparedStatement pstmt = connection.prepareStatement(requete)) {

            //Permet de remplacer le paramètre ? par l'id
            pstmt.setLong(1, jeuId);

            //exécute la requête SQL et récupère le résultat
            //Curseur qui pointe sur les lignes retournées par la requêtes
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // on split la chaîne par les espaces pour obtenir tous les genres
                String[] mots = rs.getString("genre").split(" ");

                String type=rs.getBoolean("isDLC") ? "DLC" : "Jeu";

                // on transforme le tableau en liste
                List<String> genres = Arrays.asList(mots);
                return new PublicationJeuOuDLC(rs.getLong("id"), rs.getString("nom"),
                        rs.getString("versionActuelle"),
                        genres, rs.getInt("prixEditeur"),
                        type, rs.getLong("idEditeur"),LocalDate.now(),rs.getLong("idParent"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (récupération DLC) : " + e.getMessage());
        }
        return null;

    }

    public void retirerDLCEnPublication(long idJeu)
    {
        String requete= "Update Jeu SET enPublication = false where idParent=?";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


            pstmt.setLong(1, idJeu);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("AUCUN DLC trouvé pour le jeu ID : " + idJeu);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL avec l'update : " + e.getMessage());
        }
    }

    public void retirerJeuEnPublication(long idJeu)
    {
        String requete= "Update Jeu SET enPublication = false where id=?";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


            pstmt.setLong(1, idJeu);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("AUCUN Jeu trouvé pour le jeu ID : " + idJeu);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL avec l'update : " + e.getMessage());
        }
    }


    public List<PublicationJeuOuDLC> recuperationDLCOuJeu() {
        List<PublicationJeuOuDLC> publications = new ArrayList<>();
        String requete = "SELECT * FROM Jeu WHERE enPublication = true";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = connection.prepareStatement(requete);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Connexion à la base avec succès ...");

            while (rs.next()) {
                PublicationJeuOuDLC.Builder builder = PublicationJeuOuDLC.newBuilder();

                builder.setId(rs.getLong("id"))
                        .setNom(rs.getString("nom"))
                        .setVersionActuelle(rs.getString("versionCourante"))
                        .setGenre(Arrays.asList((String[]) rs.getArray("genre").getArray()))
                        .setPrixEditeur(rs.getInt("prix"))
                        .setType(rs.getBoolean("isDLC") ? "DLC" : "Jeu")
                        .setIdEditeur(rs.getInt("idEditeur"))
                        .setDate(rs.getDate("datePublication").toLocalDate());


                if (rs.getBoolean("isDLC")) {
                    builder.setIdParent(rs.getLong("idParent"));
                } else {
                    builder.setIdParent(null);
                }

                publications.add(builder.build());
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL (récupération DLC) : " + e.getMessage());
        }

        return publications;
    }


    public ArrayList<PublicationJeuOuDLC> getJeuByEditeur(long idEditeur) {
        String requete = "SELECT * FROM Jeu WHERE idEditeur = ?";
        ArrayList<PublicationJeuOuDLC> listeJeux = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = connection.prepareStatement(requete)) {

            pstmt.setLong(1, idEditeur);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {


                Array sqlArray = rs.getArray("genre");
                List<String> genres = Arrays.asList((String[]) sqlArray.getArray());

                String type = rs.getBoolean("isDLC") ? "DLC" : "Jeu";


                Date dateBase = rs.getDate("datePublication");
                LocalDate datePublication = (dateBase != null) ? dateBase.toLocalDate() : LocalDate.now();

                listeJeux.add(new PublicationJeuOuDLC(
                        rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getString("versionCourante"),
                        genres,
                        rs.getInt("prix"),
                        type,
                        rs.getLong("idEditeur"),
                        datePublication,
                        rs.getLong("idParent")
                ));
            }
            System.out.println("Récupération réussie : " + listeJeux.size() + " éléments trouvés.");

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }

        return listeJeux;
    }


    public long getMaxIDEditeur()
    {
        String requete = "SELECT max(idEditeur) as maxId FROM Jeu ";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


             ResultSet rs= pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("maxId");
            } else {
                return 0;
            }


        } catch (SQLException e) {
            System.err.println("Erreur SQL avec l'update : " + e.getMessage());
            return -1;
        }


    }



    public long getMaxIDJeu()
    {
        String requete = "SELECT max(id) as maxId FROM Jeu ";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


            ResultSet rs= pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("maxId");
            } else {
                return 0;
            }


        } catch (SQLException e) {
            System.err.println("Erreur SQL avec la sélection : " + e.getMessage());
            return -1;
        }


    }



    public void supprimerJeuxEditeur(long idEditeur)
    {
        String requete= "DELETE FROM Jeu WHERE id = ?";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


            pstmt.setLong(1, idEditeur);

            pstmt.executeQuery();


        } catch (SQLException e) {
            System.err.println("Erreur SQL avec le delete : " + e.getMessage());
        }

    }



    public void insertionNouveauJeuOuDLC(PublicationJeuOuDLC jeuOuDLC)
    {
        String requete= "INSERT INTO Jeu(id,nom,datePublication, genre, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)  VALUES(?,?,?,?,?,?,?,?,?,?,?) ";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


            String[] valeurs = jeuOuDLC.getGenre().toArray(new String[0]);

            pstmt.setLong(1, getMaxIDJeu()+1);
            pstmt.setString(2, jeuOuDLC.getNom());
            pstmt.setDate(  3, java.sql.Date.valueOf(jeuOuDLC.getDate()));
            pstmt.setArray(4, connection.createArrayOf("VARCHAR", valeurs));
            pstmt.setString(5, "1.0");
            pstmt.setString(6, "1.0");
            pstmt.setBoolean(7, "DLC".equals(jeuOuDLC.getType()));
            pstmt.setInt(8, jeuOuDLC.getPrixEditeur());
            pstmt.setBoolean(9, false);
            pstmt.setLong(10, jeuOuDLC.getIdEditeur());
            pstmt.setLong(11,jeuOuDLC.getIdParent() );

            pstmt.executeUpdate();


        } catch (SQLException e) {
            System.err.println("Erreur SQL avec l'insertion : " + e.getMessage());
        }

    }






}
