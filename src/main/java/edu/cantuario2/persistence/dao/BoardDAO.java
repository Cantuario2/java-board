package edu.cantuario2.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import edu.cantuario2.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {
    private final Connection conn;

    public BoardEntity insert(final BoardEntity board) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO BOARDS (name) VALUES (?)"
                )
        ) {
            statement.setString(1, board.getName());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                board.setId(impl.getLastInsertID());
            }
            return board;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(final Long id) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "DELETE FROM BOARDS WHERE id=?"
                )
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT id,name FROM BOARDS WHERE id=?"
                )
        ) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                BoardEntity board = new BoardEntity();
                board.setId(resultSet.getLong("id"));
                board.setName(resultSet.getNString("name"));
                return Optional.of(board);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean exists(final Long id) throws SQLException {

        try (
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT 1 FROM BOARDS WHERE id=?"
                )
        ) {
            statement.setLong(1, id);
            statement.executeQuery();
            return statement.getResultSet().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
