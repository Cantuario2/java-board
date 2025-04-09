--liquibase formatted sql
--changeset Cantuario2:202504082250
--comment: tbl_ver table create

CREATE TABLE IF NOT EXISTS tbl_ver (
	id BIGINT auto_increment NOT NULL PRIMARY KEY,
	appName varchar(50) DEFAULT 'Cantuario2' NOT NULL COMMENT 'App Descriptive Name',
	version varchar(20) DEFAULT '0.0.0.1' NOT NULL COMMENT 'Release - Approve - Feature - Root version',
	release_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL COMMENT 'yyyy-MM-dd HH:mm:ss',
	type char(1) NOT NULL COMMENT 'A - API, D - Desktop, M - Mobile, S - Site, W - Webapp'
)ENGINE=InnoDB;

INSERT INTO tbl_ver (appName,version,release_date,type) VALUES ('Board','0.0.0.1',CURRENT_TIMESTAMP(),'D');
INSERT INTO tbl_ver (appName,version,release_date,type) VALUES ('Board API','0.0.0.1',CURRENT_TIMESTAMP(),'A');

--rollback DROP TABLE boards
