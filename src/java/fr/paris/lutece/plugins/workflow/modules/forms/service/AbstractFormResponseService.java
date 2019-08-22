package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

public class AbstractFormResponseService
{
	@Inject
    private IStateService _stateService;
	
	@Inject
	private IActionService _actionService;
	
	@Inject
	private IResourceWorkflowService _resourceWorkflowService;
	
	@Inject
	private IEditFormResponseTaskService _editFormResponseTaskService;
	
	@Inject
	protected IFormsTaskService _formsTaskService;

	protected void doChangeResponseState( ITask task, int idStateAfterEdition, int idHistory, Locale locale )
	{
		State state = _stateService.findByPrimaryKey( idStateAfterEdition );
		Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

		if ( state != null && action != null )
		{
			FormResponse response = _formsTaskService.getFormResponseFromIdHistory( idHistory );

			// Update Resource
			ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( response.getId( ),
					FormResponse.RESOURCE_TYPE, action.getWorkflow( ).getId( ) );
			resourceWorkflow.setState( state );
			_resourceWorkflowService.update( resourceWorkflow );

			WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( response.getId( ),
					FormResponse.RESOURCE_TYPE, action.getStateAfter( ).getId( ),
					resourceWorkflow.getExternalParentId( ), locale );
			// if new state have action automatic
			WorkflowService.getInstance( ).executeActionAutomatic( response.getId( ), FormResponse.RESOURCE_TYPE,
					action.getWorkflow( ).getId( ), resourceWorkflow.getExternalParentId( ) );
		}
	}
	
	protected void doEditResponseData( HttpServletRequest request, FormResponse response, List<Question> listQuestions ) throws SiteMessageException
	{
		List<EditableResponse> listEditableResponse = _formsTaskService.createEditableResponses( response, listQuestions, request );
		List<EditableResponse> _listChangedResponse = _formsTaskService.findChangedResponses( listEditableResponse );
		List<FormQuestionResponse> listChangedResponseToSave = new ArrayList<>( );

		for ( EditableResponse editableResponse : _listChangedResponse )
		{
			listChangedResponseToSave.add( editableResponse.getResponseFromForm( ) );
		}

		_editFormResponseTaskService.saveResponses( response, listChangedResponseToSave );
	}
	
	protected boolean isRecordStateValid( ITask task, TaskConfig config, int idHistory )
	{
		boolean bIsValid = false;
		if ( task != null && config != null )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( action != null && action.getStateAfter( ) != null )
            {
            	FormResponse formResponse = _formsTaskService.getFormResponseFromIdHistory( idHistory );

                // Update Resource
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( formResponse.getId( ), FormResponse.RESOURCE_TYPE, action
                        .getWorkflow( ).getId( ) );

                if ( resourceWorkflow != null && resourceWorkflow.getState( ) != null 
                		&& resourceWorkflow.getState( ).getId( ) == action.getStateAfter( ).getId( ) )
                {
                    bIsValid = true;
                }
            }
        }
        return bIsValid;
	}
}
