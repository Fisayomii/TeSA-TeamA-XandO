// src/main/java/com/tictactoe/db/Database.java
package com.tictactoe.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/** Hands out connections to the PostgreSQL database. */
public final class Database {
    private static final String URL =
            env("TTT_DB_URL", "jdbc:postgresql://localhost:5432/tictactoe");
    private static final String USER =
            env("TTT_DB_USER", "tictactoe");
    private static final String PASSWORD =
            env("TTT_DB_PASSWORD", "tictactoe");
    private Database() {}
    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value;
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    /** Create the tables if they are missing. Called once on app start. */
    public static void initSchema() {
        try (Connection con = getConnection();
             var stream = Database.class.getResourceAsStream("/schema.sql")) {
            if (stream == null) throw new IllegalStateException("schema.sql not found on classpath");
            String sql = new String(stream.readAllBytes());
            try (var st = con.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialise the database schema", e);
        }
    }
}