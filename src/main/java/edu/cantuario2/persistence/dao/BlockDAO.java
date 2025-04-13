package edu.cantuario2.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

@AllArgsConstructor
public class BlockDAO {
    private static final Logger logger = Logger.getLogger(BlockDAO.class.getName());
    private final Connection conn;

    public void blockCard(final String reason, final Long cardId) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO blocks (blocked_at,block_reason,card_id) VALUES (?,?,?)"
                )
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "block method"));
            logger.severe(e.toString());
        }
    }

    public void unblockCard(final String reason, final Long cardId) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "UPDATE blocks SET unlocked_at=?,unlock_reason=? WHERE card_id=? AND unlock_reason IS NULL"
                )
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "block method"));
            logger.severe(e.toString());
        }
    }
}
