package edu.cantuario2.service;

import edu.cantuario2.dto.BoardColumnInfoDTO;
import edu.cantuario2.dto.CardDetailsDTO;
import edu.cantuario2.exception.CardBlockedException;
import edu.cantuario2.exception.CardsFinishedException;
import edu.cantuario2.exception.EntityNotFoundException;
import edu.cantuario2.persistence.dao.CardDAO;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import edu.cantuario2.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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

    public CardEntity moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> bcInfo) throws SQLException {
        try {
            CardDAO cardDAO = new CardDAO(conn);
            Optional<CardDetailsDTO> optional = cardDAO.findById(cardId);
            CardDetailsDTO cardDetailsDTO = optional.orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado.".formatted(cardId)));

            if (cardDetailsDTO.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado. Necessário desbloquear para mover.".formatted(cardId));
            }

            BoardColumnInfoDTO boardColumnInfoDTO = bcInfo.stream()
                    .filter(bc -> bc.id().equals(cardDetailsDTO.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence à outro board."));

            if (boardColumnInfoDTO.kind().equals(BoardColumnKindEnum.FINAL)) {
                throw new CardsFinishedException("Card já finalizado.");
            }
            BoardColumnInfoDTO nextColumn = bcInfo.stream().filter(bc -> bc.atOrder() == boardColumnInfoDTO.atOrder() + 1).findFirst().orElseThrow();
            cardDAO.moveToColumn(nextColumn.id(), cardId);
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "insert method"));
            logger.severe(e.toString());
        }
        return null;
    }
}
