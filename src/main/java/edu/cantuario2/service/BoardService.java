package edu.cantuario2.service;

import edu.cantuario2.persistence.dao.BoardColumnDAO;
import edu.cantuario2.persistence.dao.BoardDAO;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@AllArgsConstructor
public class BoardService {
    private static final Logger logger = Logger.getLogger(BoardService.class.getName());
    private final Connection conn;

    public BoardEntity insert(final BoardEntity board) throws SQLException {
        BoardDAO boardDao = new BoardDAO(conn);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(conn);
        try {
            boardDao.insert(board);
            List<BoardColumnEntity> columns = board.getBoardColumns().stream().map(
                    c -> {
                        c.setBoard(board);
                        return c;
                    }
            ).toList();
            for (var column : columns) {
                boardColumnDAO.insert(column);
            }
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "insert method"));
            logger.severe(e.toString());
        }
        return board;
    }

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(conn);
        try {
            if (!dao.exists(id)) {
                return false;
            }
            dao.delete(id);
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "delete method"));
            logger.severe(e.toString());
        }
        return false;
    }
}
