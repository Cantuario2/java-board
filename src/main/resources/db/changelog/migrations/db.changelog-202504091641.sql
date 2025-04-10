--liquibase formatted sql
--changeset Cantuario2:202504091641
--comment: boards_columns table create

CREATE TABLE boards_columns(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    at_order INTEGER NOT NULL,
    kind VARCHAR(7) NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT fk_board_columns_boards FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE,
    CONSTRAINT uk_id_at_order UNIQUE KEY unique_board_id_order (board_id, at_order)
)ENGINE=InnoDB;

--rollback DROP TABLE boards_columns
