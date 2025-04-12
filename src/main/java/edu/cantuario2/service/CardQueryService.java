package edu.cantuario2.service;

import edu.cantuario2.dto.CardDetailsDTO;
import edu.cantuario2.persistence.dao.CardDAO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection conn;
    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        CardDAO cardDAO = new CardDAO(conn);
        return cardDAO.findById(id);
    }
}
