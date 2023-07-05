package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public class DuplicateFormResponseTaskComponent extends NoFormTaskComponent
{
	@Override
	public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
	{
		return null;
	}

	@Override
	public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task ) 
    {
		return null;
	}
}