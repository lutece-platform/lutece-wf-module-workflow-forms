package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

public class ModifyFormResponseUpdateStatusTask extends SimpleTask
{
	private final IFormsTaskService _formsTaskService;
	
	@Inject
    public ModifyFormResponseUpdateStatusTask( IFormsTaskService formsTaskService )
    {
        _formsTaskService = formsTaskService;
    }
	
	@Override
	public void processTask(int nIdResourceHistory, HttpServletRequest request, Locale locale) {
		ResourceHistory resourceHistory = _formsTaskService.findResourceHistory( nIdResourceHistory );

        if ( resourceHistory != null )
        {
            FormResponse formResponse = _formsTaskService.findFormResponseFrom( resourceHistory );
            formResponse.setPublished(true);
            FormResponseHome.update( formResponse );
        }
	}

	@Override
	public String getTitle(Locale locale) {
		return I18nService.getLocalizedString( getTaskType( ).getTitleI18nKey( ), locale );
	}

}
