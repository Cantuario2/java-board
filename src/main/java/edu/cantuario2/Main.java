package edu.cantuario2;

import edu.cantuario2.persistence.migration.MigrationStrategy;
import edu.cantuario2.ui.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;

import static edu.cantuario2.persistence.config.ConnConfig.getConnection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = getConnection()) {
            new MigrationStrategy(conn).executeMigration();
        }
        new MainMenu().execute();
    }
}