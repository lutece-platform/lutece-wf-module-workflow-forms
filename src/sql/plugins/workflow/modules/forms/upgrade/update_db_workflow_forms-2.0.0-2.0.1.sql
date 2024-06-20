DROP TABLE IF EXISTS workflow_task_editformresponse_config;
CREATE TABLE workflow_task_editformresponse_config
(
	id_config INT AUTO_INCREMENT,
	id_task INT NOT NULL,
	PRIMARY KEY (id_config)
);

DROP TABLE IF EXISTS workflow_task_editformresponse_config_value;
CREATE TABLE workflow_task_editformresponse_config_value
(
	id_config_value INT AUTO_INCREMENT,
	id_config INT DEFAULT 0 NOT NULL,
	id_form INT DEFAULT 0 NOT NULL,
	id_step INT DEFAULT 0 NOT NULL,
	id_question INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_config_value)
);
CREATE INDEX index_task_editformresponse_config_value ON workflow_task_editformresponse_config_value ( id_config );