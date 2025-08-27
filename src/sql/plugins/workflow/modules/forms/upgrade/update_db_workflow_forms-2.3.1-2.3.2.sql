-- liquibase formatted sql
-- changeset workflow-forms:update_db_workflow_forms-2.3.1-2.3.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Table structure for table workflow_task_duplicate_form_response
--
DROP TABLE IF EXISTS workflow_task_duplicate_form_response;
CREATE TABLE workflow_task_duplicate_form_response (
  id_task INT NOT NULL,
  PRIMARY KEY (id_task) 
);