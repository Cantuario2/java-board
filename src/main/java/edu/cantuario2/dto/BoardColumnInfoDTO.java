package edu.cantuario2.dto;

import edu.cantuario2.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int atOrder, BoardColumnKindEnum kind
) {
}
