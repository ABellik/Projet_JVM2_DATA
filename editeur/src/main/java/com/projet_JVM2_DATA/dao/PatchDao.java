package com.projet_JVM2_DATA.dao;

import com.example.events.CreationPatch; // Ta classe générée par Avro
import com.projet_JVM2_DATA.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatchDao {

    // Vérifie si un patch avec cette version cible existe déjà pour ce jeu sur ce support
    private static final String EXISTS =
            "SELECT 1 FROM patch " +
                    "WHERE idJeu=? AND versionProblematique=? AND versionCible=? AND support=? " +
                    "LIMIT 1";

    // Insertion incluant le type de modification (enum Avro) [cite: 3, 4, 5]
    private static final String INSERT =
            "INSERT INTO patch (idJeu, versionProblematique, versionCible, datePublication, support, raison) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * Vérifie l'existence pour éviter les doublons avant publication Kafka
     */

    public boolean exists(CreationPatch p) throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement st = c.prepareStatement(EXISTS)) {

            st.setLong(1, p.getIdJeu());
            st.setString(2, p.getVersionProblematique().toString());
            st.setString(3, p.getNouvelleVersion().toString());
            st.setString(4, p.getSupport().toString());

            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(CreationPatch p) throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement st = c.prepareStatement(INSERT)) {

            st.setLong(1, p.getIdJeu());
            st.setString(2, p.getVersionProblematique().toString());
            st.setString(3, p.getNouvelleVersion().toString());
            st.setLong(4,p.getDatePublication().toEpochMilli()); // timestamp-millis
            st.setString(5, p.getSupport().toString());

            String reason = (p.getCommentaireEditeur() == null) ? null : p.getCommentaireEditeur().toString();
            st.setString(6, reason);

            st.executeUpdate();
        }
    }
}