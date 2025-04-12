package edu.cantuario2.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity {
    private Long id;
    private String name;
    private int atOrder;
    private BoardColumnKindEnum kind;
    private BoardEntity board = new BoardEntity();
}
