package edu.cantuario2.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BoardColumnDAO {
    private final Connection conn;

    public void insert(final BoardColumnEntity boardColumn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO BOARDS_COLUMNS (name,at_order,kind,board_id) VALUES (?,?,?,?)"
        )) {
            int i = 1;
            statement.setString(i, boardColumn.getName());
            statement.setInt(i++, boardColumn.getOrder());
            statement.setString(i++, boardColumn.getKind().name());
            statement.setLong(i++, boardColumn.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                boardColumn.setId(impl.getLastInsertID());
            }
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long id) throws SQLException {
        List<BoardColumnEntity> boardColumns = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT id,name,at_order FROM BOARDS_COLUMNS WHERE board_id=? ORDER BY at_order ASC"
        )) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                BoardColumnEntity boardColumn = new BoardColumnEntity();
                boardColumn.setId(resultSet.getLong("id"));
                boardColumn.setName(resultSet.getString("name"));
                boardColumn.setOrder(resultSet.getInt("at_order"));
                boardColumn.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));
                boardColumns.add(boardColumn);
            }
            return boardColumns;
        }
    }
}
