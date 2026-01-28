package com.projet_JVM2_DATA.dao;

import com.projet_JVM2_DATA.db.DatabaseConnection;
import com.projet_JVM2_DATA.model.Game;
import com.example.events.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GameDao {

    private static final String INSERT =
            "INSERT INTO game (id, editor_id, name, version_current, price, status) VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO NOTHING";

    private static final String FIND_BY_EDITOR =
            "SELECT id, editor_id, name, version_current, price, status FROM game WHERE editor_id=?";

    private static final String UPDATE_STATUS =
            "UPDATE game SET status=?, published_at=CASE WHEN ?='PUBLISHED' THEN NOW() ELSE published_at END WHERE id=? AND editor_id=?";

    public void insert(Game g) throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement st = c.prepareStatement(INSERT)) {

            st.setLong(1, g.id);
            st.setLong(2, g.editorId);
            st.setString(3, g.name);
            st.setString(4, g.versionCurrent);
            if (g.price == null) st.setNull(5, java.sql.Types.INTEGER); else st.setInt(5, g.price);
            st.setString(6, g.status);

            st.executeUpdate();
        }
    }

    public List<Game> findByEditorId(long editorId) throws Exception {
        List<Game> res = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement st = c.prepareStatement(FIND_BY_EDITOR)) {

            st.setLong(1, editorId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Game g = new Game();
                    g.id = rs.getLong("id");
                    g.editorId = rs.getLong("editor_id");
                    g.name = rs.getString("name");
                    g.versionCurrent = rs.getString("version_current");
                    int p = rs.getInt("price");
                    g.price = rs.wasNull() ? null : p;
                    g.status = rs.getString("status");
                    res.add(g);
                }
            }
        }
        return res;
    }

    public int updateStatus(long gameId, long editorId, String status) throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement st = c.prepareStatement(UPDATE_STATUS)) {

            st.setString(1, status);
            st.setString(2, status);
            st.setLong(3, gameId);
            st.setLong(4, editorId);
            return st.executeUpdate();
        }
    }
}
