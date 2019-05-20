package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class ResubmitFormResponseTaskComponent extends AbstractFormResponseTaskComponent {

	// Templates
	private static final String TEMPLATE_TASK_RESUBMIT_RESPONSE_CONFIG = "admin/plugins/workflow/modules/forms/task_resubmit_response_config.html";
	private static final String TEMPLATE_TASK_RESUBMIT_RESPONSE_INFORMATION = "admin/plugins/workflow/modules/forms/task_resubmit_response_information.html";
	
	// Marks
	private static final String MARK_CONFIG = "config";
	private static final String MARK_LIST_STATES = "list_states";
	private static final String MARK_EDIT_RESPONSE = "edit_response";
	private static final String MARK_LIST_ENTRIES = "list_entries";
	
	@Inject
    @Named( "workflow-forms.taskResubmitResponseConfigService" )
    private ITaskConfigService _taskResubmitResponseConfigService;
	
	@Inject
	private IResubmitFormResponseService _resubmitResponseService;
	
	@Inject
	private IFormsTaskService _formsTaskService;
	
	@Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, _taskResubmitResponseConfigService.findByPrimaryKey( task.getId( ) ) );
        model.put( MARK_LIST_STATES, _resubmitResponseService.getListStates( task.getAction( ).getId( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_RESUBMIT_RESPONSE_CONFIG, locale, model );

        return template.getHtml( );
    }

	@Override
	public String getDisplayTaskForm(int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task ) 
	{
		
		FormResponse formResponse = _formsTaskService.findFormResponseFrom( nIdResource, strResourceType );
		List<Question> listQuestions = _resubmitResponseService.findListQuestionShownCompleteness( formResponse );
		
		List<Step> listStep = listQuestions.stream( )
				.map( Question::getStep )
				.map( Step::getId )
				.distinct( )
				.map( StepHome::findByPrimaryKey )
				.collect( Collectors.toList( ) );
		
		List<String> listStepDisplayTree = _formsTaskService.buildFormStepDisplayTreeList( request, listStep, listQuestions, formResponse, DisplayType.RESUBMIT_BACKOFFICE );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_STEP_LIST, listStepDisplayTree );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM, locale, model );

        return template.getHtml( );
	}

	@Override
	public String getDisplayTaskInformation(int nIdHistory, HttpServletRequest request, Locale locale, ITask task)
	{
		ResubmitFormResponse resubmitFormResponse = _resubmitResponseService.find( nIdHistory, task.getId( ) );
		ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( task.getId( ) );
		
		Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_EDIT_RESPONSE, resubmitFormResponse );

        if ( resubmitFormResponse != null )
        {
            model.put( MARK_LIST_ENTRIES, _resubmitResponseService.getInformationListEntries( nIdHistory ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_RESUBMIT_RESPONSE_INFORMATION, locale, model );

        return template.getHtml( );
	}

	@Override
	public String doValidateTask(int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task ) 
	{
		return null;
	}
}
