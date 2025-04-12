package edu.cantuario2.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import edu.cantuario2.persistence.entity.BoardEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BoardColumnDAO {
    private final Connection conn;

    public void insert(final BoardColumnEntity boardColumn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO boards_columns (name,at_order,kind,board_id) VALUES (?,?,?,?)"
        )) {
            statement.setString(1, boardColumn.getName());
            statement.setInt(2, boardColumn.getAtOrder());
            statement.setString(3, boardColumn.getKind().name());
            statement.setLong(4, boardColumn.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                boardColumn.setId(impl.getLastInsertID());
            }
            conn.commit();
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long id) throws SQLException {
        List<BoardColumnEntity> boardColumns = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT id,name,at_order,kind FROM boards_columns WHERE board_id=? ORDER BY at_order ASC"
        )) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                BoardColumnEntity boardColumn = new BoardColumnEntity();
                boardColumn.setId(resultSet.getLong("id"));
                boardColumn.setName(resultSet.getString("name"));
                boardColumn.setAtOrder(resultSet.getInt("at_order"));
                boardColumn.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));

                BoardDAO boardDao = new BoardDAO(conn);
                Optional<BoardEntity> board = boardDao.findById(id);
                if (board.isPresent()) {
                    BoardEntity boardOfColumn = board.get();
                    boardColumn.setBoard(boardOfColumn);
                }
                boardColumns.add(boardColumn);
            }
            return boardColumns;
        }
    }
}
