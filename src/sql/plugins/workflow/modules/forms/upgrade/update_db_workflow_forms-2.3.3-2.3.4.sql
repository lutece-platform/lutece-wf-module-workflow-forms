-- liquibase formatted sql
-- changeset workflow-forms:update_db_workflow_forms-2.3.2-2.3.4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Table structure for table workflow_task_duplicate_form_response
--

ALTER TABLE workflow_task_resubmit_response_value ADD COLUMN iteration_number int default 0;
ALTER TABLE workflow_task_resubmit_response_value DROP PRIMARY KEY;
ALTER TABLE workflow_task_resubmit_response_value ADD PRIMARY KEY (id_history, id_entry, iteration_number );

ALTER TABLE workflow_task_complete_response_value ADD COLUMN iteration_number int default 0;
ALTER TABLE workflow_task_complete_response_value DROP PRIMARY KEY;
ALTER TABLE workflow_task_complete_response_value ADD PRIMARY KEY (id_history, id_entry, iteration_number );
