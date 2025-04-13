package edu.cantuario2.service;

import edu.cantuario2.dto.BoardColumnInfoDTO;
import edu.cantuario2.dto.CanBeChangedDTO;
import edu.cantuario2.dto.CardDetailsDTO;
import edu.cantuario2.exception.CardBlockedException;
import edu.cantuario2.exception.CardsFinishedException;
import edu.cantuario2.exception.EntityNotFoundException;
import edu.cantuario2.persistence.dao.BlockDAO;
import edu.cantuario2.persistence.dao.CardDAO;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import edu.cantuario2.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

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

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> bcInfo) throws SQLException {
        try {
            CardDAO cardDAO = new CardDAO(conn);
            CanBeChangedDTO checkedCard = canBeChanged(cardId, bcInfo);
            if (checkedCard.canBeChanged()) {
                cardDAO.moveToColumn(checkedCard.bcInfoDTO().id(), cardId);
            }
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "insert method"));
            logger.severe(e.toString());
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> bcInfo) throws SQLException {
        try {
            CardDAO cardDAO = new CardDAO(conn);
            CanBeChangedDTO checkedCard = canBeChanged(cardId, bcInfo);
            if (nonNull(checkedCard)) {
                if (checkedCard.canBeChanged()) {
                    cardDAO.moveToColumn(cancelColumnId, cardId);
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "cancel method"));
            logger.severe(e.toString());
        }
    }

    public void blockCard(final Long id, final String reason, final List<BoardColumnInfoDTO> bcInfo) throws SQLException {
        try {
            BlockDAO blockDAO = new BlockDAO(conn);
            CanBeChangedDTO checkedCard = canBeChanged(id, bcInfo);
            if (nonNull(checkedCard)) {
                if (checkedCard.canBeChanged()) {
                    blockDAO.blockCard(reason, id);
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "cancel method"));
            logger.severe(e.toString());
        }
    }

    public void unlockCard(final Long id, final String reason) throws SQLException {
        try {
            CardDAO cardDAO = new CardDAO(conn);
            Optional<CardDetailsDTO> optional = cardDAO.findById(id);
            CardDetailsDTO cardDetailsDTO = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado.".formatted(id))
            );

            if (!cardDetailsDTO.blocked()) {
                throw new CardBlockedException("Card %s não está bloqueado.".formatted(id));
            }

            BlockDAO blockDAO = new BlockDAO(conn);
            blockDAO.unblockCard(reason, id);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "cancel method"));
            logger.severe(e.toString());
        }
    }

    public CanBeChangedDTO canBeChanged(final Long cardId, final List<BoardColumnInfoDTO> bcInfo) throws SQLException {
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

            BoardColumnInfoDTO nextColumn = bcInfo.stream()
                    .filter(bc -> {
                        return bc.atOrder() == boardColumnInfoDTO.atOrder() + 1;
                    })
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card cancelado."));

            return new CanBeChangedDTO(true, nextColumn);
        } catch (RuntimeException e) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "canBeChanged method"));
            logger.severe(e.toString());
        }
        return null;
    }
}
