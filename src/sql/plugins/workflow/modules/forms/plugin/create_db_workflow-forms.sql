-- liquibase formatted sql
-- changeset workflow-forms:create_db_workflow-forms.sql
-- preconditions onFail:MARK_RAN onError:WARN
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
  date_completed TIMESTAMP NULL,
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
  date_completed TIMESTAMP NULL,
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

DROP TABLE IF EXISTS workflow_task_editformresponse_config;
CREATE TABLE workflow_task_editformresponse_config
(
	id_config INT AUTO_INCREMENT,
	is_multiform SMALLINT DEFAULT 0 NOT NULL,
	id_task INT NOT NULL,
	PRIMARY KEY (id_config)
);

DROP TABLE IF EXISTS workflow_task_editformresponse_config_value;
CREATE TABLE workflow_task_editformresponse_config_value
(
	id_config_value INT AUTO_INCREMENT,
	id_config INT DEFAULT 0 NOT NULL,
	id_form INT NULL,
	id_step INT NULL,
	id_question INT NULL,
	code VARCHAR(100),
	response VARCHAR(100) NULL,
	PRIMARY KEY (id_config_value)
);
CREATE INDEX index_task_editformresponse_config_value ON workflow_task_editformresponse_config_value ( id_config );

DROP TABLE IF EXISTS workflow_state_controller_form_response_value;
CREATE TABLE workflow_state_controller_form_response_value
(
	id INT AUTO_INCREMENT,
	id_task INT DEFAULT 0 NOT NULL,
	is_multiform SMALLINT DEFAULT 0 NOT NULL,
	id_form INT NULL,
	id_step INT NULL,
	id_question INT NULL,
	code VARCHAR(100),
	response_value	VARCHAR(255),
	PRIMARY KEY (id)
);
CREATE INDEX index_state_controller_form_response_value ON workflow_state_controller_form_response_value ( id_task );

DROP TABLE IF EXISTS workflow_task_complete_response_history;
CREATE TABLE workflow_task_complete_response_history
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	id_question INT DEFAULT 0 NOT NULL,
	iteration_number int default '0',
	new_value long VARCHAR DEFAULT NULL,
	PRIMARY KEY (id_history, id_task, id_question, iteration_number)
);

DROP TABLE IF EXISTS workflow_task_resubmit_response_history;
CREATE TABLE workflow_task_resubmit_response_history
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	id_question INT DEFAULT 0 NOT NULL,
	iteration_number int default '0',
	previous_value long VARCHAR DEFAULT NULL,
	new_value long VARCHAR DEFAULT NULL,
	PRIMARY KEY (id_history, id_task, id_question, iteration_number)
);

DROP TABLE IF EXISTS workflow_task_update_status;
CREATE TABLE workflow_task_update_status(
  id_task INT DEFAULT 0 NOT NULL,
  status INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_task)
);

-- Table structure for table workflow_task_linkedvaluesformresponse_config
--
DROP TABLE IF EXISTS workflow_task_linkedvaluesformresponse_config;
CREATE TABLE workflow_task_linkedvaluesformresponse_config (
  id_config INT AUTO_INCREMENT,
  id_task INT DEFAULT 0 NOT NULL,
  PRIMARY KEY ( id_config )
);

-- Table structure for table workflow_task_linkedvaluesformresponse_config_value
--
DROP TABLE IF EXISTS workflow_task_linkedvaluesformresponse_config_value;
CREATE TABLE workflow_task_linkedvaluesformresponse_config_value (
  id_config_value INT AUTO_INCREMENT,
  id_config INT DEFAULT 0 NOT NULL,
  id_form INT DEFAULT 0 NOT NULL,
  id_question_source INT DEFAULT 0 NOT NULL,
  question_source_value VARCHAR(255) DEFAULT NULL,
  id_question_target INT DEFAULT 0 NOT NULL,
  question_target_value VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY ( id_config_value )
);

-- Table structure for table workflow_task_duplicate_form_response
--
DROP TABLE IF EXISTS workflow_task_duplicate_form_response;
CREATE TABLE workflow_task_duplicate_form_response (
  id_task INT NOT NULL,
  PRIMARY KEY (id_task) 
);