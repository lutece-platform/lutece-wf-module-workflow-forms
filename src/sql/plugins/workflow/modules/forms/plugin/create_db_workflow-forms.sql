--
-- ADD  TABLE workflow_task_forms_editresponse_history to record changements about the forms workflow
--
DROP TABLE IF EXISTS workflow_task_forms_editresponse_history;
CREATE TABLE workflow_task_forms_editresponse_history
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	id_question INT DEFAULT 0 NOT NULL,
	iteration_number int default '0',
	previous_value long VARCHAR DEFAULT NULL,
	new_value long VARCHAR DEFAULT NULL,
	PRIMARY KEY (id_history, id_task, id_question, iteration_number)
);

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
