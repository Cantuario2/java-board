package edu.cantuario2.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import static edu.cantuario2.persistence.config.ConnConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {
    private static final Logger logger = Logger.getLogger(MigrationStrategy.class.getName());

    private final Connection conn;

    public void executeMigration() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        try (FileOutputStream fos = new FileOutputStream("liquibase.log")) {
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));
            try (
                    Connection connection = getConnection();
                    JdbcConnection jdbcConnection = new JdbcConnection(connection)
            ) {
                var liquibase = new Liquibase(
                        "/db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection);
                liquibase.update();
            } catch (SQLException | LiquibaseException e) {
                logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "executeMigration method"));
                logger.severe(e.toString());
                System.setErr(originalErr);
            }
        } catch (IOException ex) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "executeMigration method"));
            logger.severe(ex.toString());
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

}