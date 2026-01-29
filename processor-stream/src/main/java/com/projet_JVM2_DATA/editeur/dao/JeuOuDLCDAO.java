package com.projet_JVM2_DATA.editeur.dao;

import com.example.events.EvaluationJeu;

import java.sql.*;
import java.util.*;

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




    public void mettreDLCEnPublication(long idJeu)
    {
        String requete= "Update Jeu SET en_publication = true where idParent=?";

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

    public List<String> getGenreJeu(long idJeu) {
        String requete = "SELECT genre FROM Jeu WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = connection.prepareStatement(requete)) {

            pstmt.setLong(1, idJeu);
            ResultSet resultat = pstmt.executeQuery();


            if (resultat.next()) {

                java.sql.Array sqlArray = resultat.getArray("genre");
                if (sqlArray != null) {
                    String[] tabGenres = (String[]) sqlArray.getArray();
                    return Arrays.asList(tabGenres);
                }
            }

        } catch (SQLException e) {

            System.err.println("Erreur SQL lors de la récupération du genre : " + e.getMessage());
        }


        return Collections.emptyList();
    }

    public void mettreAutresJeuEnPublication(long idJeu)
    {
        String requete= "Update Jeu SET en_publication = true where id=? AND genre && ?::varchar[]";


        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement pstmt = connection.prepareStatement(requete)) {


            pstmt.setLong(1, idJeu);
            Array sqlArray = connection.createArrayOf("varchar", getGenreJeu(idJeu).toArray());
            pstmt.setArray(2, sqlArray);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("AUCUN Jeu trouvé pour ces genres ");
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL avec l'update : " + e.getMessage());
        }
    }

    public void stockerEvaluationsJeu(long jeuId, int note, String version, String commentaire, long date)
    {
        String requete = "INSERT INTO Evaluation (idJeu, versionJeuEvaluee, estDLC, note, commentaire, dateEval) VALUES (?, ?, ?, ?,?,?)";

        System.out.println("Tentative de connexion...");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             //Créer un statement précompilé avec la requête SQL
             PreparedStatement pstmt = connection.prepareStatement(requete)) {

            pstmt.setLong(1,jeuId);
            pstmt.setString(2,version );
            pstmt.setBoolean(3, false);
            pstmt.setInt(4, note);
            pstmt.setString(5, commentaire );
            pstmt.setLong(6,date);

            // 3. Exécution de la requête
            int lignesStockees = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur SQL (récupération DLC) : " + e.getMessage());
        }

    }


    public List<EvaluationJeu> recuperationEvaluations() {
        List<EvaluationJeu> evaluations = new ArrayList<>();
        // On récupère toutes les évaluations.
        // Si tu as un champ pour savoir si elles sont déjà traitées, ajoute un WHERE ici.
        String requete = "SELECT * FROM evaluation";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = connection.prepareStatement(requete);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Connexion DB : Récupération des évaluations...");

            while (rs.next()) {
                EvaluationJeu.Builder builder = EvaluationJeu.newBuilder();

                // Remplissage selon ton schéma Avro spécifique
                builder.setIdJeu(rs.getLong("idjeu"))
                        .setNote(rs.getInt("note"))
                        .setVersionJeu(rs.getString("versionjeuevaluee"))
                        .setCommentaire(rs.getString("commentaire"));


                long millis = rs.getLong("dateeval");
                builder.setDateEvaluationJeu(java.time.Instant.ofEpochMilli(millis));

                evaluations.add(builder.build());
            }

        } catch (SQLException e) {
            System.err.println(" Erreur SQL (récupération Evaluations) : " + e.getMessage());
        }

        return evaluations;
    }
}
