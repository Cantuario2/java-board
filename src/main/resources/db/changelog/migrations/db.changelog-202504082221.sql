--liquibase formatted sql
--changeset Cantuario2:202504082221
--comment: boards table create

CREATE TABLE boards(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL
)ENGINE=InnoDB;

--rollback DROP TABLE boards
