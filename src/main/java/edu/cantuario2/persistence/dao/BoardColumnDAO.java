package edu.cantuario2.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import edu.cantuario2.dto.BoardColumnDTO;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import edu.cantuario2.persistence.entity.BoardEntity;
import edu.cantuario2.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

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

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> boardColumns = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT id,name,at_order,kind FROM boards_columns WHERE board_id=? ORDER BY at_order ASC"
        )) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                BoardColumnEntity boardColumn = new BoardColumnEntity();
                boardColumn.setId(resultSet.getLong("id"));
                boardColumn.setName(resultSet.getString("name"));
                boardColumn.setAtOrder(resultSet.getInt("at_order"));
                boardColumn.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));

                BoardDAO boardDao = new BoardDAO(conn);
                Optional<BoardEntity> board = boardDao.findById(boardId);
                if (board.isPresent()) {
                    BoardEntity boardOfColumn = board.get();
                    boardColumn.setBoard(boardOfColumn);
                }
                boardColumns.add(boardColumn);
            }
            return boardColumns;
        }
    }

    public List<BoardColumnDTO> findByBoardIdDetailed(final Long boardId) throws SQLException {
        List<BoardColumnDTO> boardColumnDTOS = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT
                        	bc.id,
                        	bc.name,
                        	bc.kind,
                        	(SELECT COUNT(c.id) FROM cards c WHERE c.board_column_id = bc.id) as cards_amount
                        FROM boards_columns bc
                        WHERE board_id=?
                        ORDER BY bc.at_order ASC;
                        """
        )) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                BoardColumnDTO bcDTO = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        BoardColumnKindEnum.findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                boardColumnDTOS.add(bcDTO);
            }
            return boardColumnDTOS;
        }
    }

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT
                        	bc.name,
                        	bc.kind,
                        	c.id,
                        	c.title,
                        	c.description
                        FROM boards_columns bc
                        	LEFT JOIN cards c ON c.board_column_id = bc.id
                        WHERE bc.id=?
                        ORDER BY bc.at_order ASC;
                        """
        )) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                BoardColumnEntity boardColumn = new BoardColumnEntity();
                boardColumn.setName(resultSet.getString("bc.name"));
                boardColumn.setKind(BoardColumnKindEnum.findByName(resultSet.getString("bc.kind")));
                do {
                    if (isNull(resultSet.getString("c.title"))) {
                        break;
                    }
                    CardEntity card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    boardColumn.getCards().add(card);
                    return Optional.of(boardColumn);
                } while (resultSet.next());
            }
            return Optional.empty();
        }
    }
}
