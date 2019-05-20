package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public abstract class AbstractFormResponseTaskComponent extends AbstractTaskComponent {

	protected static final String MARK_STEP_LIST = "list_step";
	protected static final String TEMPLATE_TASK_FORM = "admin/plugins/workflow/modules/forms/task_edit_form_response_form.html";
	
    @Override
	public String getTaskInformationXml(int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
	{
		return null;
	}
}
