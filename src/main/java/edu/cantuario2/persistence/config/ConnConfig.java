package edu.cantuario2.persistence.config;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnConfig {
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                "jdbc:" + System.getenv("db_url"),
                System.getenv("user"),
                System.getenv("password")
        );
        conn.setAutoCommit(false);
        return conn;
    }
}
