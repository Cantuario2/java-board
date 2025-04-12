package edu.cantuario2.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import edu.cantuario2.dto.CardDetailsDTO;
import edu.cantuario2.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {
    private static final Logger logger = Logger.getLogger(CardDAO.class.getName());

    private final Connection conn;

    public CardEntity insert(final CardEntity card) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO cards (title,description,board_column_id) VALUES (?,?,?)"
                )
        ) {
            statement.setString(1, card.getTitle());
            statement.setString(2, card.getDescription());
            statement.setLong(3, card.getBoardColumn().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                card.setId(impl.getLastInsertID());
            }
        }
        return card;
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        """
                                SELECT
                                	c.id,
                                	c.title,
                                	c.description,
                                	b.blocked_at,
                                	b.block_reason,
                                	c.board_column_id,
                                	bc.name,
                                	(SELECT COUNT(sub_b.id) FROM blocks sub_b WHERE sub_b.card_id = c.id) as blocks_amount
                                FROM cards c
                                	LEFT JOIN blocks b ON c.id = b.card_id AND b.unlocked_at IS NULL
                                	INNER JOIN boards_columns bc ON bc.id = c.board_column_id
                                WHERE c.id=?;
                                """
                )
        ) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                CardDetailsDTO cardDetailsDTO = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        !nonNull(resultSet.getTimestamp("b.blocked_at")) ? null : OffsetDateTime.ofInstant(resultSet.getTimestamp("b.blocked_at").toInstant(), ZoneOffset.UTC),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")

                );
                return Optional.of(cardDetailsDTO);
            }
        } catch (SQLException e) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "insert method"));
            logger.severe(e.toString());
        }
        return Optional.empty();
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "UPDATE cards SET board_column_id=? WHERE id=?"
                )
        ) {
            statement.setLong(1, columnId);
            statement.setLong(2, cardId);
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "delete method"));
            logger.severe(e.toString());
        }
    }
}
