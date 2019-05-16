package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.IFormResponseDAO;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseValue;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implements IResubmitResponseService
 */
public class ResubmitFormResponseService implements IResubmitFormResponseService {

	@Inject
    private IActionService _actionService;
	
	@Inject
    private IStateService _stateService;
	
	@Inject
    private IResubmitFormResponseDAO _resubmitFormResponseDAO;
	
	@Inject
	private IResubmitFormResponseValueDAO _resubmitFormResponseValueDAO;
	
	@Inject
	private IEntryDAO _entryDAO;
	
	@Inject
	private IFormResponseDAO formResponseDAO;
	
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
}
