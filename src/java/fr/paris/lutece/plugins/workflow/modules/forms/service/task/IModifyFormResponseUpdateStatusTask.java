package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public interface IModifyFormResponseUpdateStatusTask 
{
	void processTask(int nIdResourceHistory, HttpServletRequest request, Locale locale);
	
	String getTitle(Locale locale);
	
	void doRemoveConfig( );
}
