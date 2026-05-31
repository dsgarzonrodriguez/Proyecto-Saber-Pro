package com.saberpro.infrastructure.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConexion {

    private static final String HOST     = "aws-1-us-east-2.pooler.supabase.com";
    private static final String PUERTO   = "5432";
    private static final String DB       = "postgres";
    private static final String USER     = "postgres.wdqxwjswsbfhgkoujyjt";
    private static final String PASSWORD = "saberpro2026";

    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + DB;

    public Connection getConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de PostgreSQL no encontrado", e);
        }
    }
}