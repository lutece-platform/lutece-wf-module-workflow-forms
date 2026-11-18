-- liquibase formatted sql
-- changeset workflow-forms:update_db_workflow_forms-2.3.6-2.3.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Removes what tasks left behind once removed, and what executions left once their history was removed. Only rows whose task, history or parent row no longer exists are deleted.
DELETE FROM workflow_task_forms_editresponse_history WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_forms_editresponse_history.id_task );
DELETE FROM workflow_task_resubmit_response WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_resubmit_response.id_task );
DELETE FROM workflow_task_resubmit_response_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_resubmit_response_cf.id_task );
DELETE FROM workflow_task_complete_response WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_complete_response.id_task );
DELETE FROM workflow_task_complete_response_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_complete_response_cf.id_task );
DELETE FROM workflow_task_editformresponse_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_editformresponse_config.id_task );
DELETE FROM workflow_state_controller_form_response_value WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_state_controller_form_response_value.id_task );
DELETE FROM workflow_task_complete_response_history WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_complete_response_history.id_task );
DELETE FROM workflow_task_resubmit_response_history WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_resubmit_response_history.id_task );
DELETE FROM workflow_task_update_status WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_update_status.id_task );
DELETE FROM workflow_task_linkedvaluesformresponse_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_linkedvaluesformresponse_config.id_task );
DELETE FROM workflow_task_duplicate_form_response WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_duplicate_form_response.id_task );
DELETE FROM workflow_task_forms_editresponse_history WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_forms_editresponse_history.id_history );
DELETE FROM workflow_task_resubmit_response WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_resubmit_response.id_history );
DELETE FROM workflow_task_complete_response WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_complete_response.id_history );
DELETE FROM workflow_task_complete_response_history WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_complete_response_history.id_history );
DELETE FROM workflow_task_resubmit_response_history WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_resubmit_response_history.id_history );
DELETE FROM workflow_task_resubmit_response_value WHERE NOT EXISTS ( SELECT 1 FROM workflow_task_resubmit_response r WHERE r.id_history = workflow_task_resubmit_response_value.id_history );
DELETE FROM workflow_task_complete_response_value WHERE NOT EXISTS ( SELECT 1 FROM workflow_task_complete_response r WHERE r.id_history = workflow_task_complete_response_value.id_history );
DELETE FROM workflow_task_editformresponse_config_value WHERE NOT EXISTS ( SELECT 1 FROM workflow_task_editformresponse_config c WHERE c.id_config = workflow_task_editformresponse_config_value.id_config );
DELETE FROM workflow_task_linkedvaluesformresponse_config_value WHERE NOT EXISTS ( SELECT 1 FROM workflow_task_linkedvaluesformresponse_config c WHERE c.id_config = workflow_task_linkedvaluesformresponse_config_value.id_config );
