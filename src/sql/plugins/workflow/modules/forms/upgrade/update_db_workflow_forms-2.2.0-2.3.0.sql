--liquibase formatted sql
--changeset workflow-forms:update_db_workflow_forms-2.2.0-2.3.0.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE workflow_task_editformresponse_config ADD COLUMN is_multiform SMALLINT DEFAULT 0 NOT NULL;

ALTER TABLE workflow_task_editformresponse_config_value ADD COLUMN code VARCHAR(100);
ALTER TABLE workflow_task_editformresponse_config_value MODIFY id_form INT NULL;
ALTER TABLE workflow_task_editformresponse_config_value MODIFY id_step INT NULL;
ALTER TABLE workflow_task_editformresponse_config_value MODIFY id_question INT NULL;

ALTER TABLE workflow_state_controller_form_response_value ADD COLUMN is_multiform SMALLINT DEFAULT 0 NOT NULL;
ALTER TABLE workflow_state_controller_form_response_value ADD COLUMN code VARCHAR(100);
ALTER TABLE workflow_state_controller_form_response_value MODIFY id_form INT NULL;
ALTER TABLE workflow_state_controller_form_response_value MODIFY id_step INT NULL;
ALTER TABLE workflow_state_controller_form_response_value MODIFY id_question INT NULL;

CREATE TABLE workflow_task_update_status(
  id_task INT DEFAULT 0 NOT NULL,
  status INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_task)
);

ALTER TABLE workflow_task_editformresponse_config_value ADD COLUMN response VARCHAR(100) NULL;
