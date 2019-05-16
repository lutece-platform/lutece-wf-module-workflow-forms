package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public abstract class AbstractFormResponseTaskComponent extends AbstractTaskComponent {

	protected static final String MARK_STEP_LIST = "list_step";
	protected static final String TEMPLATE_TASK_FORM = "admin/plugins/workflow/modules/forms/task_edit_form_response_form.html";
	
	/**
     * Return the list of all DisplayTree for the given list of Step
     * 
     * @param request
     *            the request
     * @param listStep
     *            The list of all Step on which the DisplayTree must be build
     * @param formResponse
     *            The form response on which to retrieve the Response objects
     * @return the list of all DisplayTree for the given list of Step
     */
    protected List<String> buildFormStepDisplayTreeList( HttpServletRequest request, List<Step> listStep, List<Question> listQuestionToDisplay,
            FormResponse formResponse, DisplayType displayType )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
        List<Integer> listQuestionToDisplayId = listQuestionToDisplay.stream( ).map( question -> question.getId( ) ).collect( Collectors.toList( ) );

        listFormQuestionResponse.stream( ).filter( formQuestionResponse -> listQuestionToDisplayId.contains( formQuestionResponse.getQuestion( ).getId( ) ) );

        if ( !CollectionUtils.isEmpty( listStep ) )
        {
            for ( Step step : listStep )
            {
                int nIdStep = step.getId( );

                StepDisplayTree stepDisplayTree = new StepDisplayTree( nIdStep, formResponse );
                listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, listFormQuestionResponse, request.getLocale( ),
                		displayType ) );
            }
        }

        return listFormDisplayTrees;
    }
    
    @Override
	public String getTaskInformationXml(int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
	{
		return null;
	}
}
