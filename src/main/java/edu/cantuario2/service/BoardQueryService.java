package edu.cantuario2.service;

import edu.cantuario2.dto.BoardColumnDTO;
import edu.cantuario2.dto.BoardDetailDTO;
import edu.cantuario2.persistence.dao.BoardColumnDAO;
import edu.cantuario2.persistence.dao.BoardDAO;
import edu.cantuario2.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {
    private final Connection conn;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        BoardDAO boardDAO = new BoardDAO(conn);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(conn);
        Optional<BoardEntity> optional = boardDAO.findById(id);
        if (optional.isPresent()) {
            BoardEntity board = optional.get();
            board.setBoardColumns(boardColumnDAO.findByBoardId(board.getId()));
            return Optional.of(board);
        }
        return Optional.empty();
    }

    public Optional<BoardDetailDTO> showBoardDetails(final Long id) throws SQLException {
        BoardDAO boardDAO = new BoardDAO(conn);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(conn);
        Optional<BoardEntity> optional = boardDAO.findById(id);
        if (optional.isPresent()) {
            BoardEntity board = optional.get();
            List<BoardColumnDTO> columns = boardColumnDAO.findByBoardIdDetailed(board.getId());
            BoardDetailDTO boardDetailed = new BoardDetailDTO(board.getId(), board.getName(), columns);
            return Optional.of(boardDetailed);
        }
        return Optional.empty();
    }
}
