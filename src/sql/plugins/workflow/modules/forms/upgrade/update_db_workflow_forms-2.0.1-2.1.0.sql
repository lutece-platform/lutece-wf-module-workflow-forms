--liquibase formatted sql
--changeset workflow-forms:update_db_workflow_forms-2.0.1-2.1.0.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_state_controller_form_response_value;
CREATE TABLE workflow_state_controller_form_response_value
(
	id INT AUTO_INCREMENT,
	id_task INT DEFAULT 0 NOT NULL,
	id_form INT DEFAULT 0 NOT NULL,
	id_step INT DEFAULT 0 NOT NULL,
	id_question INT DEFAULT 0 NOT NULL,
	response_value	VARCHAR(255),
	PRIMARY KEY (id)
);
CREATE INDEX index_state_controller_form_response_value ON workflow_state_controller_form_response_value ( id_task );
