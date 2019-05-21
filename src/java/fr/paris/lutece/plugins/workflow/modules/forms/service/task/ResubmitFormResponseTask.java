package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;

public class ResubmitFormResponseTask extends Task {

	private static final String PARAMETER_MESSAGE = "message_";
	private static final String PARAMETER_IDS_ENTRY = "ids_entry";
	
	@Inject
    @Named( "workflow-forms.taskResubmitResponseConfigService" )
    private ITaskConfigService _taskResubmitResponseConfigService;
	
	@Inject
	private IResubmitFormResponseService _resubmitFormResponseService;
	
	@Inject
    private IStateService _stateService;
	
	@Override
	public void processTask(int nIdResourceHistory, HttpServletRequest request, Locale locale)
	{
		String strMessage = request.getParameter( PARAMETER_MESSAGE + getId( ) );
        String [ ] listIdsEntry = request.getParameterValues( PARAMETER_IDS_ENTRY );

        boolean bCreate = false;
        List<ResubmitFormResponseValue> listResubmitFormResponseValues = new ArrayList<>( );
        
        ResubmitFormResponse resubmitFormResponse = _resubmitFormResponseService.find( nIdResourceHistory, getId( ) );
        
        if ( resubmitFormResponse == null )
        {
        	resubmitFormResponse = new ResubmitFormResponse( );
        	resubmitFormResponse.setIdHistory( nIdResourceHistory );
        	resubmitFormResponse.setIdTask( getId( ) );
            bCreate = true;
        }
        
        if ( listIdsEntry != null )
        {
            for ( String strIdEntry : listIdsEntry )
            {
                if ( StringUtils.isNotBlank( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
                {
                    int nIdEntry = Integer.parseInt( strIdEntry );
                    ResubmitFormResponseValue editRecordValue = new ResubmitFormResponseValue( );
                    editRecordValue.setIdEntry( nIdEntry );

                    listResubmitFormResponseValues.add( editRecordValue );
                }
            }
        }
        
        resubmitFormResponse.setMessage( StringUtils.isNotBlank( strMessage ) ? strMessage : StringUtils.EMPTY );
        resubmitFormResponse.setListResubmitReponseValues( listResubmitFormResponseValues );
        resubmitFormResponse.setIsComplete( false );
        
        if ( bCreate )
        {
        	_resubmitFormResponseService.create( resubmitFormResponse );
        }
        else
        {
        	_resubmitFormResponseService.update( resubmitFormResponse );
        }
	}
	
	@Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
		_resubmitFormResponseService.removeByIdHistory( nIdHistory, getId( ) );
    }
	
	@Override
	public void doRemoveConfig( )
	{
		_resubmitFormResponseService.removeByIdTask( getId( ) );
		_taskResubmitResponseConfigService.remove( getId( ) );
	}
	
	@Override
    public String getTitle( Locale locale )
    {
        String strTitle = StringUtils.EMPTY;
        ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( getId( ) );

        if ( config != null && config.getIdStateAfterEdition( ) != -1 )
        {
            State state = _stateService.findByPrimaryKey( config.getIdStateAfterEdition( ) );

            if ( state != null )
            {
                strTitle = state.getName( );
            }
        }

        return strTitle;
    }
	
	@Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
		return null;
    }
	
	@Override
    public void init( )
    {
    }
}
