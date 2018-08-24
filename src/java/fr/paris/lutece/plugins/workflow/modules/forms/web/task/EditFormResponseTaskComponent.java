/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class represents a component for the task {@link fr.paris.lutece.plugins.workflow.modules.forms.service.task.EditFormResponseTask EditFormResponseTask}
 *
 */
public class EditFormResponseTaskComponent extends NoConfigTaskComponent
{
    // Messages
    private static final String MESSAGE_ERROR = "module.workflow.forms.error.task.editFormResponse";

    // Templates
    private static final String TEMPLATE_TASK_FORM = "admin/plugins/workflow/modules/forms/task_edit_form_response_form.html";

    // Marks
    private static final String MARK_STEP_LIST = "list_step";

    private final IFormsTaskService _formsTaskService;
    private final IEditFormResponseTaskService _editFormResponseTaskService;

    /**
     * Constructor
     * 
     * @param formsTaskService
     *            the form task service
     * @param editFormResponseTaskService
     *            the edit form response task service
     */
    @Inject
    public EditFormResponseTaskComponent( IFormsTaskService formsTaskService, IEditFormResponseTaskService editFormResponseTaskService )
    {
        super( );

        _formsTaskService = formsTaskService;
        _editFormResponseTaskService = editFormResponseTaskService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        String strErrorUrl = null;

        FormResponse formResponse = _formsTaskService.findFormResponseFrom( nIdResource, strResourceType );
        List<Question> listQuestion = _editFormResponseTaskService.findQuestionsToEdit( formResponse );
        GenericAttributeError error = validateQuestions( listQuestion, request );

        if ( error != null )
        {
            strErrorUrl = buildErrorUrl( error, request );
        }

        return strErrorUrl;
    }

    /**
     * Validates the specified questions
     * 
     * @param listQuestion
     *            the questions to validate
     * @param request
     *            the request
     * @return a {@code GenericAttributeError} if the validation fails, {@code null} otherwise
     */
    private GenericAttributeError validateQuestions( List<Question> listQuestion, HttpServletRequest request )
    {
        GenericAttributeError error = null;

        for ( Question question : listQuestion )
        {
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            FormQuestionResponse formQuestionResponse = entryDataService.createResponseFromRequest( question, request );

            if ( formQuestionResponse.hasError( ) )
            {
                error = formQuestionResponse.getError( );
                break;
            }
        }

        return error;
    }

    /**
     * Builds the error URL
     * 
     * @param error
     *            the error
     * @param request
     *            the request
     * @return the error URL
     */
    private String buildErrorUrl( GenericAttributeError error, HttpServletRequest request )
    {
        Object [ ] listMessageParameters = {
            error.getErrorMessage( ),
        };

        return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR, listMessageParameters, AdminMessage.TYPE_STOP );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        FormResponse formResponse = _formsTaskService.findFormResponseFrom( nIdResource, strResourceType );
        List<Question> listQuestion = _editFormResponseTaskService.findQuestionsToEdit( formResponse );
        Collection<StepDisplay> listStepDisplay = createStepDisplays( formResponse, listQuestion, locale );
        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_STEP_LIST, listStepDisplay );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * Creates the {@code StepDisplay} objects for the specified questions
     * 
     * @param formResponse
     *            the form response associated to the questions
     * @param listQuestion
     *            the questions
     * @param locale
     *            the locale
     * @return the {@code StepDisplay} objects
     */
    private Collection<StepDisplay> createStepDisplays( FormResponse formResponse, List<Question> listQuestion, Locale locale )
    {
        Map<Integer, StepDisplay> mapStepDisplay = new HashMap<>( );

        for ( Question question : listQuestion )
        {
            Step step = StepHome.findByPrimaryKey( question.getIdStep( ) );
            StepDisplay stepDisplay = mapStepDisplay.get( step.getId( ) );

            if ( stepDisplay == null )
            {
                stepDisplay = new StepDisplay( step );
                mapStepDisplay.put( step.getId( ), stepDisplay );
            }

            stepDisplay.addHtml( buildHtmlForQuestion( formResponse, question, locale ) );
        }

        return mapStepDisplay.values( );
    }

    /**
     * Builds the HTML for the specified question
     * 
     * @param formResponse
     *            the form response associated to the questions
     * @param question
     *            the question
     * @param locale
     *            the locale
     * @return the HTML
     */
    private String buildHtmlForQuestion( FormResponse formResponse, Question question, Locale locale )
    {
        Map<String, Object> model = new HashMap<>( );
        List<Response> listResponse = findResponses( formResponse, question );

        model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, listResponse );

        IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( question.getEntry( ).getEntryType( ) );
        return displayService.getEntryTemplateDisplay( question.getEntry( ), locale, model, DisplayType.EDITION_BACKOFFICE );
    }

    /**
     * Finds the responses in the specified form response for the specified question
     * 
     * @param formResponse
     *            the form response containing the responses
     * @param question
     *            the question
     * @return the responses
     */
    private List<Response> findResponses( FormResponse formResponse, Question question )
    {
        List<Response> listResponse = new ArrayList<>( );

        FormQuestionResponse responseSave = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( formResponse.getId( ), question.getId( ) );

        if ( responseSave != null )
        {
            listResponse.addAll( responseSave.getEntryResponse( ) );
        }

        return listResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * This class is used to display a step with editable questions
     *
     */
    public static final class StepDisplay
    {
        private final String _strTitle;
        private final List<String> _listQuestionHtml;

        /**
         * Constructor
         * 
         * @param step
         *            the step containing editable questions
         */
        private StepDisplay( Step step )
        {
            _strTitle = step.getTitle( );
            _listQuestionHtml = new ArrayList<>( );
        }

        /**
         * Adds HTML for a question
         * 
         * @param strHtml
         *            the HTML to add
         */
        private void addHtml( String strHtml )
        {
            _listQuestionHtml.add( strHtml );
        }

        /**
         * Gives the title of the step
         * 
         * @return the title
         */
        public String getTitle( )
        {
            return _strTitle;
        }

        /**
         * Gives the HTML of the questions
         * 
         * @return the HTML of the questions
         */
        public List<String> getQuestionHtml( )
        {
            return _listQuestionHtml;
        }
    }

}
