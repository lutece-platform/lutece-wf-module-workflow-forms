package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.forms.business.ModifyFormResponseUpdateStatusTaskConfig;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class ModifyFormResponseUpdateStatusTaskComponent extends NoFormTaskComponent
{
	// Mark
    private static final String MARK_PUBLISHED = "published";
    
	// Templates
    private static final String TEMPLATE_TASK_FORM_EDITSTATUS_CONFIG = "admin/plugins/workflow/modules/forms/task_edit_form_response_status.html";

	private ModifyFormResponseUpdateStatusTaskConfig _config;
	
	@Inject
    @Named( "workflow-forms.modifyFormResponseUpdateStatusTaskService" )
    private ITaskConfigService _taskConfigService;
	
	@Override
	public String getDisplayConfigForm(HttpServletRequest request, Locale locale, ITask task) {
		_config = _taskConfigService.findByPrimaryKey( task.getId( ) );
		if ( _config == null )
        {
            _config = new ModifyFormResponseUpdateStatusTaskConfig( );
        }
		
		Map<String, Object> model = new HashMap<>( );
		model.put( MARK_PUBLISHED, _config.isPublished() ); 
		
		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM_EDITSTATUS_CONFIG, locale, model );
		
		return template.getHtml( );
	}

	@Override
	public String getDisplayTaskInformation(int nIdHistory, HttpServletRequest request, Locale locale, ITask task) {
		return null;
	}

}
