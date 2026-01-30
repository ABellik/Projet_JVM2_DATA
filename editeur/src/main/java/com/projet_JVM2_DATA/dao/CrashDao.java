package com.projet_JVM2_DATA.dao;

import com.projet_JVM2_DATA.db.DatabaseConnection;
import com.example.events.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/*
* Accède à la table crash
* Responsable du stockage des crashs
* */
public class CrashDao {

    //Requete d'insertion des crashs
    private static final String requeteInsert =
            "INSERT INTO crash (idJeu, idSession, versionJeuConcernee, codeErreur, dateCrash, support, isTreated) " +
                    "VALUES (?, ?, ?, ?, ?, ?, false) ON CONFLICT (idSession) DO NOTHING";

    // Requete pour marquer qu'un crash est traité
    private static final String MARK_TREATED =
            "UPDATE crash SET isTreated = true " +
                    "WHERE idJeu = ? AND versionJeuConcernee = ? AND support = ? AND isTreated = false";

// Methode d'insertion des crashs
    public void insert(Session s) throws Exception {
        // Fabrication du code avro car n'existe pas dans le schéma avro
        String idSession = s.getIdJoueur() + "-" + s.getIdJeu() + "-" + s.getHeureDeDebut();

        // codeErreur peut être null
        String codeErreur = (s.getCodeErreur() == null) ? null : s.getCodeErreur().toString();


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(requeteInsert)) {

            st.setLong(1, s.getIdJeu());
            st.setString(2, idSession);
            st.setString(3, s.getVersionJeu());
            st.setString(4, codeErreur);
            st.setTimestamp(5, Timestamp.from(s.getHeureDeFin()));
            st.setString(6, s.getSupport());

            st.executeUpdate();
        }
    }

    //Methode pour marquer les crashs traités
    public int markTreated(long idJeu, String versionJeuConcernee, String support) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(MARK_TREATED)) {

            st.setLong(1, idJeu);
            st.setString(2, versionJeuConcernee);
            st.setString(3, support);

            return st.executeUpdate(); // nb de crashs marqués traités
        }
    }
}
