package edu.cantuario2.service;

import edu.cantuario2.persistence.dao.BoardColumnDAO;
import edu.cantuario2.persistence.dao.BoardDAO;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService {
    private final Connection conn;

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(conn);
        return boardColumnDAO.findById(id);
    }
}
