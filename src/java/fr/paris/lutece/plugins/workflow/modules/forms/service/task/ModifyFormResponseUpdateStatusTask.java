package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.sql.Timestamp;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ModifyFormResponseUpdateStatusTaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

public class ModifyFormResponseUpdateStatusTask extends SimpleTask
{
	private final IFormsTaskService _formsTaskService;
	
	@Inject
    private FormService _formService;
	
	@Inject
    @Named( "workflow-forms.modifyFormResponseUpdateStatusTaskService" )
    private ITaskConfigService _taskConfigService;
	
	@Inject
    public ModifyFormResponseUpdateStatusTask( IFormsTaskService formsTaskService )
    {
        _formsTaskService = formsTaskService;
    }
	
	@Override
	public void processTask(int nIdResourceHistory, HttpServletRequest request, Locale locale) 
	{
		ResourceHistory resourceHistory = _formsTaskService.findResourceHistory( nIdResourceHistory );
		ModifyFormResponseUpdateStatusTaskConfig config = _taskConfigService.findByPrimaryKey(getId());
        if ( resourceHistory != null )
        {
            FormResponse formResponse = _formsTaskService.findFormResponseFrom( resourceHistory );
            Timestamp timestampCurrentTime = new Timestamp( System.currentTimeMillis( ) );
            formResponse.setPublished(config.isPublished());
            formResponse.setUpdateStatus(timestampCurrentTime);
            _formService.saveFormResponseWithoutQuestionResponse( formResponse );
        }
	}

	@Override
	public String getTitle(Locale locale) 
	{
		return I18nService.getLocalizedString( getTaskType( ).getTitleI18nKey( ), locale );
	}
	
	@Override
    public void doRemoveConfig( )
    {
		_taskConfigService.remove( getId( ) );
    }

}
