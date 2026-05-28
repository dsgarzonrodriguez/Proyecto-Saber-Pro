package saberPro.infrastructure.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConexion {

    private static final String HOST     = "localhost";
    private static final String PUERTO   = "5432";
    private static final String DB       = "software2";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "waos123";

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