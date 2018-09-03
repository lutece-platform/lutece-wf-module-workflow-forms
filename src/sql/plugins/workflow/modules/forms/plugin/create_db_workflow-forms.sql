--
-- ADD  TABLE workflow_task_forms_editresponse_history to record changements about the forms workflow
--
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