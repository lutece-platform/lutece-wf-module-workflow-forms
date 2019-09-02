package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;

public class CompleteFormResponseService extends AbstractFormResponseService implements ICompleteFormResponseService {

	private static final String MESSAGE_APP_ERROR = "module.workflow.forms.message.app_error";
	private static final String PARAMETER_URL_RETURN = "url_return";
	
	@Inject
    private ICompleteFormResponseDAO _completeFormResponseDAO;
	
	@Inject
	private ICompleteFormResponseValueDAO _completeFormResponseValueDAO;
	
	@Inject
	private IEntryDAO _entryDAO;
	
	@Inject
    @Named( "workflow-forms.taskCompleteResponseConfigService" )
    private ITaskConfigService _taskCompleteResponseConfigService;
	
	@Inject
    private ITaskService _taskService;
	
	@Override
	public List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse )
	{
		List<Question> listQuestionForm = QuestionHome.getListQuestionByIdForm( formResponse.getFormId( ) );
		return listQuestionForm.stream( )
				.filter( question -> question.getEntry( ).isUsedInCompleteFormResponse( ) )
				.collect( Collectors.toList( ) );
	}

	@Override
	public CompleteFormResponse find( int nIdHistory, int nIdTask )
	{
		CompleteFormResponse resubmitFormResponse = _completeFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        if ( resubmitFormResponse != null )
        {
        	resubmitFormResponse.setListCompleteReponseValues( _completeFormResponseValueDAO.load( resubmitFormResponse.getIdHistory( ), WorkflowUtils.getPlugin( ) ) );
        }

        return resubmitFormResponse;
	}
	
	@Override
	public List<Entry> getInformationListEntries( int nIdHistory )
	{
		Plugin plugin = WorkflowUtils.getPlugin( );
		
		List<CompleteFormResponseValue> listEditRecordValues = _completeFormResponseValueDAO.load( nIdHistory, plugin );
		List<Entry> listEntries = new ArrayList<>( );
		for ( CompleteFormResponseValue value : listEditRecordValues )
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
	public void create( CompleteFormResponse completeFormResponse )
	{
		if ( completeFormResponse != null )
	    {
			Plugin plugin = WorkflowUtils.getPlugin( );
			_completeFormResponseDAO.insert( completeFormResponse, plugin );

			for ( CompleteFormResponseValue value : completeFormResponse.getListCompleteReponseValues( ) )
			{
				value.setIdHistory( completeFormResponse.getIdHistory( ) );
				_completeFormResponseValueDAO.insert( value, plugin );
			}
	    }
	}
	
	@Override
	public void update( CompleteFormResponse completeFormResponse )
	{
		if ( completeFormResponse != null )
        {
			Plugin plugin = WorkflowUtils.getPlugin( );
			_completeFormResponseDAO.store( completeFormResponse, plugin );
            // Remove its edit record values first
			_completeFormResponseValueDAO.delete( completeFormResponse.getIdHistory( ), plugin );

            for ( CompleteFormResponseValue value : completeFormResponse.getListCompleteReponseValues( ) )
            {
                value.setIdHistory( completeFormResponse.getIdHistory( ) );
                _completeFormResponseValueDAO.insert( value, plugin );
            }
        }
	}
	
	@Override
    public void removeByIdHistory( int nIdHistory, int nIdTask )
    {
		CompleteFormResponse resubmitFormResponse = find( nIdHistory, nIdTask );

        if ( resubmitFormResponse != null )
        {
        	Plugin plugin = WorkflowUtils.getPlugin( );
        	_completeFormResponseValueDAO.delete( resubmitFormResponse.getIdHistory( ), plugin );
        	_completeFormResponseDAO.deleteByIdHistory( nIdHistory, nIdTask, plugin );
        }
    }
	
	@Override
    public void removeByIdTask( int nIdTask )
    {
		Plugin plugin = WorkflowUtils.getPlugin( );
		List<CompleteFormResponse> listResponse = _completeFormResponseDAO.loadByIdTask( nIdTask, plugin );
        for ( CompleteFormResponse editRecord : listResponse )
        {
        	_completeFormResponseValueDAO.delete( editRecord.getIdHistory( ), plugin );
        }

        _completeFormResponseDAO.deleteByIdTask( nIdTask, plugin );
    }
	
	@Override
    public boolean isRecordStateValid( CompleteFormResponse completeFormResponse, Locale locale )
    {
        ITask task = _taskService.findByPrimaryKey( completeFormResponse.getIdTask( ), locale );
        CompleteFormResponseTaskConfig config = _taskCompleteResponseConfigService.findByPrimaryKey( completeFormResponse.getIdTask( ) );

        return isRecordStateValid( task, config, completeFormResponse.getIdHistory( ) );
    }
	
	@Override
	public List<Question> getListQuestionToEdit( FormResponse formResponse, List<CompleteFormResponseValue> listEditRecordValues )
	{
		List<Integer> idEntries = listEditRecordValues.stream( )
			.map( CompleteFormResponseValue::getIdEntry )
			.map( EntryHome::findByPrimaryKey )
			.map( Entry::getIdEntry )
			.collect( Collectors.toList( ) );

		List<Question> listQuestions = findListQuestionUsedCorrectForm( formResponse );
		return listQuestions.stream( ).filter( question -> idEntries.contains( question.getEntry( ).getIdEntry( ) ) )
				.collect( Collectors.toList( ) );
	}
	
	@Override
	public boolean doEditResponseData( HttpServletRequest request, CompleteFormResponse completeFormResponse ) throws SiteMessageException
	{
		FormResponse response = _formsTaskService.getFormResponseFromIdHistory( completeFormResponse.getIdHistory( ) );
		if ( response == null )
		{
			_formsTaskService.setSiteMessage( request, MESSAGE_APP_ERROR, SiteMessage.TYPE_STOP,
					request.getParameter( PARAMETER_URL_RETURN ) );

			return false;
		}
		List<Question> listQuestions = getListQuestionToEdit( response, completeFormResponse.getListCompleteReponseValues( ) );

		doEditResponseData( request, response, listQuestions );
		return true;
	}
	
	@Override
	public void doChangeResponseState( CompleteFormResponse completeFormResponse, Locale locale )
	{
		ITask task = _taskService.findByPrimaryKey( completeFormResponse.getIdTask( ), locale );
		CompleteFormResponseTaskConfig config = _taskCompleteResponseConfigService.findByPrimaryKey( completeFormResponse.getIdTask( ) );

		if ( task != null && config != null )
		{
			doChangeResponseState( task, config.getIdStateAfterEdition( ), completeFormResponse.getIdHistory( ), locale );
		}
	}
	
	@Override
	public void doCompleteResponse( CompleteFormResponse completeFormResponse )
	{
		completeFormResponse.setIsComplete( true );
		update( completeFormResponse );
	}
}
