package edu.cantuario2.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class BoardColumnDAO {
    private final Connection conn;

    public BoardColumnEntity insert(final BoardColumnEntity boardColumn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO BOARDS_COLUMNS (name,at_order,kind,board_id) VALUES (?,?,?,?)"
        )) {
            var i = 1;
            statement.setString(i, boardColumn.getName());
            statement.setInt(i++, boardColumn.getOrder());
            statement.setString(i++, boardColumn.getKind().name());
            statement.setLong(i++, boardColumn.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                boardColumn.setId(impl.getLastInsertID());
            }
            return boardColumn;
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long id) throws SQLException {
        return null;
    }
}
