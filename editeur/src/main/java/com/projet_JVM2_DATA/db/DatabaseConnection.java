package com.projet_JVM2_DATA.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        // On force les paramètres qui fonctionnent avec votre Docker
        // Testez d'abord avec 5432, puis 5435 si c'est vraiment celui-là que vous avez choisi
        String url = "jdbc:postgresql://localhost:5435/editeur_db";
        String user = "editeur";
        String pass = "editeur123";

        System.out.println("🔗 Tentative manuelle sur : " + url);
        return DriverManager.getConnection(url, user, pass);
    }
}
