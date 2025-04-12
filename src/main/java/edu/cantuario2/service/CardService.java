package edu.cantuario2.service;

import edu.cantuario2.persistence.dao.CardDAO;
import edu.cantuario2.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@AllArgsConstructor
public class CardService {
    private final Connection conn;
    private static final Logger logger = Logger.getLogger(CardService.class.getName());

    public CardEntity insert(final CardEntity card) throws SQLException {
        try {
            CardDAO cardDAO = new CardDAO(conn);
            cardDAO.insert(card);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "insert method"));
            logger.severe(e.toString());
        }
        return card;
    }
}
