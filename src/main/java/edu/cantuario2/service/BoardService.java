package edu.cantuario2.service;

import edu.cantuario2.persistence.dao.BoardColumnDAO;
import edu.cantuario2.persistence.dao.BoardDAO;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class BoardService {
    private final Connection conn;

    public BoardEntity insert(final BoardEntity board) throws SQLException {
        BoardDAO boardDao = new BoardDAO(conn);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(conn);
        try {
            boardDao.insert(board);
            List<BoardColumnEntity> columns = board.getBoardColumns().stream().peek(
                    c -> c.setBoard(board)
            ).toList();
            for (var column : columns) {
                boardColumnDAO.insert(column);
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
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
            throw e;
        }
    }
}
