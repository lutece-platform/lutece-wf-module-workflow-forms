--liquibase formatted sql
--changeset workflow-forms:update_db_workflow_forms-1.0.0-1.0.2.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_task_resubmit_response;
CREATE TABLE workflow_task_resubmit_response (
  id_history INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL,
  message LONG VARCHAR,
  is_complete SMALLINT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_history, id_task)
);

DROP TABLE IF EXISTS workflow_task_resubmit_response_value;
CREATE TABLE workflow_task_resubmit_response_value (
  id_history INT DEFAULT 0 NOT NULL,
  id_entry INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_history, id_entry)
);

DROP TABLE IF EXISTS workflow_task_resubmit_response_cf;
CREATE TABLE workflow_task_resubmit_response_cf(
  id_task INT DEFAULT 0 NOT NULL,
  id_state_after_edition INT DEFAULT 0 NOT NULL,
  default_message LONG VARCHAR,
  PRIMARY KEY (id_task)
);

DROP TABLE IF EXISTS workflow_task_complete_response;
CREATE TABLE workflow_task_complete_response (
  id_history INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL,
  message LONG VARCHAR,
  is_complete SMALLINT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_history, id_task)
);

DROP TABLE IF EXISTS workflow_task_complete_response_value;
CREATE TABLE workflow_task_complete_response_value (
  id_history INT DEFAULT 0 NOT NULL,
  id_entry INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_history, id_entry)
);

DROP TABLE IF EXISTS workflow_task_complete_response_cf;
CREATE TABLE workflow_task_complete_response_cf(
  id_task INT DEFAULT 0 NOT NULL,
  id_state_after_edition INT DEFAULT 0 NOT NULL,
  default_message LONG VARCHAR,
  PRIMARY KEY (id_task)
);
