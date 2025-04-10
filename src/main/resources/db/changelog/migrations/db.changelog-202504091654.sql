--liquibase formatted sql
--changeset Cantuario2:202504091641
--comment: cards table create

CREATE TABLE cards(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(200) NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT fk_board_columns_cards FOREIGN KEY (board_column_id) REFERENCES boards_columns (id) ON DELETE CASCADE
)ENGINE=InnoDB;

--rollback DROP TABLE cards
