package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.IFormResponseDAO;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implements IResubmitResponseService
 */
public class ResubmitFormResponseService implements IResubmitFormResponseService {

	private static final String MESSAGE_APP_ERROR = "module.workflow.forms.message.app_error";
	private static final String PARAMETER_URL_RETURN = "url_return";
	
	@Inject
    private IActionService _actionService;
	
	@Inject
    private IStateService _stateService;
	
	@Inject
    private ITaskService _taskService;
	
	@Inject
    private IResubmitFormResponseDAO _resubmitFormResponseDAO;
	
	@Inject
	private IResubmitFormResponseValueDAO _resubmitFormResponseValueDAO;
	
	@Inject
	private IEntryDAO _entryDAO;
	
	@Inject
	private IFormResponseDAO formResponseDAO;
	
	@Inject
    private IResourceHistoryService _resourceHistoryService;
	
	@Inject
    @Named( "workflow-forms.taskResubmitResponseConfigService" )
    private ITaskConfigService _taskResubmitResponseConfigService;
	
	@Inject
    private IResourceWorkflowService _resourceWorkflowService;
	
	@Inject
	private IFormsTaskService _formsTaskService;
	
	@Inject
	private IEditFormResponseTaskService _editFormResponseTaskService;
	
	@Override
	public ReferenceList getListStates(int nIdAction)
	{
		ReferenceList referenceListStates = new ReferenceList( );
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action != null ) && ( action.getWorkflow( ) != null ) )
        {
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( action.getWorkflow( ).getId( ) );

            List<State> listStates = _stateService.getListStateByFilter( stateFilter );

            referenceListStates.addItem( -1, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, "id", "name", true ) );
        }

        return referenceListStates;
	}

	@Override
	public ResubmitFormResponse find(int nIdHistory, int nIdTask)
	{
		ResubmitFormResponse resubmitFormResponse = _resubmitFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        if ( resubmitFormResponse != null )
        {
        	resubmitFormResponse.setListResubmitReponseValues( _resubmitFormResponseValueDAO.load( resubmitFormResponse.getIdHistory( ), WorkflowUtils.getPlugin( ) ) );
        }

        return resubmitFormResponse;
	}

	@Override
	public List<Entry> getInformationListEntries( int nIdHistory )
	{
		Plugin plugin = WorkflowUtils.getPlugin( );
		
		List<ResubmitFormResponseValue> listEditRecordValues = _resubmitFormResponseValueDAO.load( nIdHistory, plugin );
		List<Entry> listEntries = new ArrayList<>( );
		for ( ResubmitFormResponseValue value : listEditRecordValues )
		{
			Entry entry = _entryDAO.load( value.getIdEntry( ), plugin );
			
			if ( entry != null )
            {
                listEntries.add( entry );
            }
		}
		return listEntries;
	}
	
	@Override
    public List<Entry> getFormListEntries( int nIdRecord, String strResourceType )
    {
		Plugin plugin = WorkflowUtils.getPlugin( );
		FormResponse response = formResponseDAO.load( nIdRecord, plugin );
		return _entryDAO.findEntriesWithoutParent(plugin, response.getFormId( ), Form.RESOURCE_TYPE );
    }
	
	@Override
	public  List<Question> findListQuestionShownCompleteness( FormResponse formResponse )
	{
		Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
		List<Question> listFormQuestion = QuestionHome.getListQuestionByIdForm( form.getId( ) );
        
        return listFormQuestion.stream( )
        		.filter( question -> question.getEntry( ).isShownInCompleteness( ) )
        		.collect( Collectors.toList( ) );
	}

	@Override
    public void removeByIdHistory( int nIdHistory, int nIdTask )
    {
		ResubmitFormResponse resubmitFormResponse = find( nIdHistory, nIdTask );

        if ( resubmitFormResponse != null )
        {
        	Plugin plugin = WorkflowUtils.getPlugin( );
        	_resubmitFormResponseValueDAO.delete( resubmitFormResponse.getIdHistory( ), plugin );
        	_resubmitFormResponseDAO.deleteByIdHistory( nIdHistory, nIdTask, plugin );
        }
    }
	
	@Override
    public void removeByIdTask( int nIdTask )
    {
		Plugin plugin = WorkflowUtils.getPlugin( );
		List<ResubmitFormResponse> listResponse = _resubmitFormResponseDAO.loadByIdTask( nIdTask, plugin );
        for ( ResubmitFormResponse editRecord : listResponse )
        {
        	_resubmitFormResponseValueDAO.delete( editRecord.getIdHistory( ), plugin );
        }

        _resubmitFormResponseDAO.deleteByIdTask( nIdTask, plugin );
    }
	
	@Override
	public void create(ResubmitFormResponse resubmitFormResponse) {
		if ( resubmitFormResponse != null )
	    {
			Plugin plugin = WorkflowUtils.getPlugin( );
			_resubmitFormResponseDAO.insert( resubmitFormResponse, plugin );

			for ( ResubmitFormResponseValue value : resubmitFormResponse.getListResubmitReponseValues( ) )
			{
				value.setIdHistory( resubmitFormResponse.getIdHistory( ) );
				_resubmitFormResponseValueDAO.insert( value, plugin );
			}
	    }
	}
	
	@Override
	public void update(ResubmitFormResponse resubmitFormResponse) {
		if ( resubmitFormResponse != null )
        {
			Plugin plugin = WorkflowUtils.getPlugin( );
			_resubmitFormResponseDAO.store( resubmitFormResponse, plugin );
            // Remove its edit record values first
			_resubmitFormResponseValueDAO.delete( resubmitFormResponse.getIdHistory( ), plugin );

            for ( ResubmitFormResponseValue value : resubmitFormResponse.getListResubmitReponseValues( ) )
            {
                value.setIdHistory( resubmitFormResponse.getIdHistory( ) );
                _resubmitFormResponseValueDAO.insert( value, plugin );
            }
        }
	}
	
	@Override
	public void setSiteMessage( HttpServletRequest request, String strMessage, int nTypeMessage, String strUrlReturn) throws SiteMessageException
	{
		if ( StringUtils.isNotBlank( strUrlReturn ) )
		{
			SiteMessageService.setMessage( request, strMessage, nTypeMessage, strUrlReturn );
		}
		else
		{
			SiteMessageService.setMessage( request, strMessage, nTypeMessage );
		}
	}
	
	@Override
    public FormResponse getFormResponseFromIdHistory( int nIdHistory )
    {
		FormResponse response = null;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );

        if ( resourceHistory != null && FormResponse.RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) ) )
        {
        	response = FormResponseHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        }

        return response;
    }
	
	@Override
    public boolean isRecordStateValid( ResubmitFormResponse resubmitFormResponse, Locale locale )
    {
        boolean bIsValid = false;

        ITask task = _taskService.findByPrimaryKey( resubmitFormResponse.getIdTask( ), locale );
        ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( resubmitFormResponse.getIdTask( ) );

        if ( ( task != null ) && ( config != null ) )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( ( action != null ) && ( action.getStateAfter( ) != null ) )
            {
            	FormResponse formResponse = getFormResponseFromIdHistory( resubmitFormResponse.getIdHistory( ) );

                // Update Resource
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( formResponse.getId( ), FormResponse.RESOURCE_TYPE, action
                        .getWorkflow( ).getId( ) );

                if ( ( resourceWorkflow != null ) && ( resourceWorkflow.getState( ) != null )
                        && ( resourceWorkflow.getState( ).getId( ) == action.getStateAfter( ).getId( ) ) )
                {
                    bIsValid = true;
                }
            }
        }

        return bIsValid;
    }
	
	 @Override
	 public List<Question> getListQuestionToEdit( FormResponse formResponse, List<ResubmitFormResponseValue> listEditRecordValues )
	 {
		List<Entry> listEntries = new ArrayList<>( );
		for ( ResubmitFormResponseValue value : listEditRecordValues )
		{
			Entry entry = EntryHome.findByPrimaryKey( value.getIdEntry( ) );
			listEntries.add( entry );
		}
		List<Integer> idEntries = listEntries.stream( ).map( Entry::getIdEntry ).collect( Collectors.toList( ) );
		
		List<Question> listQuestions = findListQuestionShownCompleteness( formResponse );
		return listQuestions.stream( )
					.filter( question -> idEntries.contains( question.getEntry( ).getIdEntry( ) ) )
					.collect( Collectors.toList( ) );
	}
	 
	@Override
	public boolean doEditResponseData( HttpServletRequest request, ResubmitFormResponse resubmitFormResponse ) throws SiteMessageException {
		FormResponse response = getFormResponseFromIdHistory( resubmitFormResponse.getIdHistory( ) );
		if ( response == null)
		{
			setSiteMessage( request, MESSAGE_APP_ERROR, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );

	        return false;
		}
		List<Question> listQuestions = getListQuestionToEdit( response, resubmitFormResponse.getListResubmitReponseValues( ) );
		
		List<EditableResponse> listEditableResponse = _formsTaskService.createEditableResponses( response, listQuestions, request );
		List<EditableResponse>_listChangedResponse = _formsTaskService.findChangedResponses( listEditableResponse );
        List<FormQuestionResponse> listChangedResponseToSave = new ArrayList<>( );

        for ( EditableResponse editableResponse : _listChangedResponse )
        {
            listChangedResponseToSave.add( editableResponse.getResponseFromForm( ) );
        }

        _editFormResponseTaskService.saveResponses( listChangedResponseToSave );
		
		return true;
	}
	
	@Override
    public void doChangeResponseState( ResubmitFormResponse resubmitFormResponse, Locale locale )
    {
        ITask task = _taskService.findByPrimaryKey( resubmitFormResponse.getIdTask( ), locale );
        ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( resubmitFormResponse.getIdTask( ) );

        if ( task != null && config != null )
        {
            State state = _stateService.findByPrimaryKey( config.getIdStateAfterEdition( ) );
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( state != null && action != null )
            {
            	FormResponse response = getFormResponseFromIdHistory( resubmitFormResponse.getIdHistory( ) );

                // Update Resource
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( response.getId( ), FormResponse.RESOURCE_TYPE, action
                        .getWorkflow( ).getId( ) );
                resourceWorkflow.setState( state );
                _resourceWorkflowService.update( resourceWorkflow );
                WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( response.getId( ), FormResponse.RESOURCE_TYPE,
                        action.getStateAfter( ).getId( ), resourceWorkflow.getExternalParentId( ), locale );
                // if new state have action automatic
                WorkflowService.getInstance( ).executeActionAutomatic( response.getId( ), FormResponse.RESOURCE_TYPE, action.getWorkflow( ).getId( ),
                        resourceWorkflow.getExternalParentId( ) );
            }
        }
    }
	
	@Override
	public void doCompleteResponse(ResubmitFormResponse resubmitFormResponse) {
		resubmitFormResponse.setIsComplete( true );
		update( resubmitFormResponse );
	}
}
