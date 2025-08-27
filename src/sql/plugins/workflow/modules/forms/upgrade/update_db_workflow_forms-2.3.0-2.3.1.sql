-- liquibase formatted sql
-- changeset workflow-forms:update_db_workflow_forms-2.3.0-2.3.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Table structure for table workflow_task_linkedvaluesformresponse_config
--
DROP TABLE IF EXISTS workflow_task_linkedvaluesformresponse_config;
CREATE TABLE workflow_task_linkedvaluesformresponse_config (
  id_config INT NOT NULL AUTO_INCREMENT,
  id_task INT DEFAULT 0 NOT NULL,
  PRIMARY KEY ( id_config )
);

-- Table structure for table workflow_task_linkedvaluesformresponse_config_value
--
DROP TABLE IF EXISTS workflow_task_linkedvaluesformresponse_config_value;
CREATE TABLE workflow_task_linkedvaluesformresponse_config_value (
  id_config_value INT NOT NULL AUTO_INCREMENT,
  id_config INT DEFAULT 0 NOT NULL,
  id_form INT DEFAULT 0 NOT NULL,
  id_question_source INT DEFAULT 0 NOT NULL,
  id_question_target INT DEFAULT 0 NOT NULL,
  PRIMARY KEY ( id_config_value )
);