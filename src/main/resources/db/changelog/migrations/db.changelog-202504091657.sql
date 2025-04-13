--liquibase formatted sql
--changeset Cantuario2:202504091641
--comment: blocks table create

CREATE TABLE blocks(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(200) NOT NULL,
    unlocked_at TIMESTAMP NULL,
    unlock_reason VARCHAR(200) NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT fk_cards_blocks FOREIGN KEY (card_id) REFERENCES cards (id) ON DELETE CASCADE
)ENGINE=InnoDB;

--rollback DROP TABLE blocks
