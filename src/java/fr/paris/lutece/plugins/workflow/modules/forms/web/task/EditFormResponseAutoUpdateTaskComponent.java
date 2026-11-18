/*
 * Copyright (c) 2002-2025, City of Paris
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfigValue;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskHistoryService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class represents a component for the task {@link fr.paris.lutece.plugins.workflow.modules.forms.service.task.EditFormResponseAutoUpdateTask
 * EditFormResponseAutoUpdateTask}
 *
 */
@ApplicationScoped
@Named( "workflow-forms.editFormResponseAutoUpdateTaskComponent" )
public class EditFormResponseAutoUpdateTaskComponent extends NoFormTaskComponent
{
    // Mark
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_ID_FORM = "id_form";
    private static final String MARK_ID_STEP = "id_step";
    private static final String MARK_ID_QUESTION = "id_question";
    private static final String MARK_QUESTION_LIST = "question_list";
    private static final String MARK_MAPPING_LIST = "mapping_list";
    private static final String MARK_MULTIFORM = "multiform";
    private static final String MARK_CODE_LIST = "code_list";
    private static final String MARK_RESPONSE_LIST = "response_list";
    private static final String MARK_STEP_LIST = "list_step";

    // Parameters
    private static final String PARAMETER_ACTION = "apply";
    private static final String PARAMETER_MULTIFORM = "multiform";
    private static final String PARAMETER_FORM = "form_select";
    private static final String PARAMETER_STEP = "step_select";
    private static final String PARAMETER_QUESTION = "question_select";
    private static final String PARAMETER_MAPPING_ID = "mapping_id";
    private static final String PARAMETER_CODE = "code_select";
    private static final String PARAMETER_RESPONSE = "response_select";

    // Action
    private static final String ACTION_SELECT_FORM = "select_form_config";
    private static final String ACTION_SELECT_MULTIFORM = "select_multiform";
    private static final String ACTION_SELECT_STEP = "select_step_config";
    private static final String ACTION_SELECT_QUESTION = "select_question_config";
    private static final String ACTION_SELECT_RESPONSE = "select_response_config";
    private static final String ACTION_REMOVE_MAPPING = "delete_mapping";
    private static final String ACTION_SELECT_CODE = "select_code";

    private static final String TEMPLATE_TASK_FORM_EDITRESPONSE_CONFIG = "admin/plugins/workflow/modules/forms/task_edit_form_response_auto_update_form_config.html";

    protected final IFormsTaskService _formsTaskService;
    private final IEditFormResponseTaskService _editFormResponseTaskService;
    
    // the mapping being built is kept per task in the session of the administrator, not in this application scoped component
    private static final String SESSION_CONFIG_VALUE = "workflow-forms.editFormResponseAutoUpdate.configValue.";

    /**
     * Constructor
     * 
     * @param formsTaskService
     *            the form task service
     * @param editFormResponseTaskService
     *            the edit form response task service
     */
    @Inject
    public EditFormResponseAutoUpdateTaskComponent( IFormsTaskService formsTaskService, IEditFormResponseTaskService editFormResponseTaskService,
            IEditFormResponseTaskHistoryService editFormResponseTaskHistoryService, @Named( "workflow-forms.editFormResponseTypeAutoUpdateTask" ) ITaskType taskType,
            @Named( "workflow-forms.editFormResponseConfigService" ) ITaskConfigService taskConfigService )
    {
        super( );

        _formsTaskService = formsTaskService;
        _editFormResponseTaskService = editFormResponseTaskService;
        setTaskType( taskType );
        setTaskConfigService( taskConfigService );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        EditFormResponseConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        if ( config == null )
        {
            config = new EditFormResponseConfig( );
        }
        EditFormResponseConfigValue configValue = getConfigValue( request, task );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_LIST, FormHome.getFormsReferenceList( ) );
        model.put( MARK_MAPPING_LIST, config.getListConfigValues( ) );
        model.put( MARK_MULTIFORM, config.isMultiform( ) );
        model.put( MARK_CODE_LIST, _editFormResponseTaskService.selectAllTechnicalCode( isTaskBo( ) ) );

        if ( configValue.getForm( ) != null )
        {
            model.put( MARK_ID_FORM, configValue.getForm( ).getId( ) );
            model.put( MARK_STEP_LIST, StepHome.getStepReferenceListByForm( configValue.getForm( ).getId( ) ) );
        }
        if ( configValue.getStep( ) != null )
        {
            model.put( MARK_ID_STEP, configValue.getStep( ).getId( ) );
            model.put( MARK_QUESTION_LIST, getQuestionReferenceList( configValue.getStep( ).getId( ) ) );
        }
        if ( configValue.getQuestion( ) != null )
        {
            model.put( MARK_ID_QUESTION, configValue.getQuestion( ).getId( ) );
            model.put( MARK_RESPONSE_LIST, _editFormResponseTaskService.getResponseReferenceList( configValue.getQuestion( ).getId( ) ) );
        }
        if ( configValue.getCode( ) != null )
        {
            model.put( MARK_ID_QUESTION, configValue.getCode( ) );
            model.put( MARK_RESPONSE_LIST,
                    _editFormResponseTaskService.getResponseReferenceList( QuestionHome.findByCode( configValue.getCode( ) ).get( 0 ).getId( ) ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM_EDITRESPONSE_CONFIG, locale, model );
        return template.getHtml( );
    }

    protected boolean isTaskBo( )
    {
        return true;
    }

    private ReferenceList getQuestionReferenceList( int idStep )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( -1, "" );
        if ( idStep != -1 )
        {
            List<Question> questionList = QuestionHome.getQuestionsListByStep( idStep );
            for ( Question question : questionList )
            {
                if ( question.getEntry( ).isOnlyDisplayInBack( ) == isTaskBo( ) )
                {
                    refList.addItem( question.getId( ), question.getTitle( ) );
                }
            }
        }

        return refList;
    }

    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        EditFormResponseConfig config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        boolean create = config == null;
        if ( create )
        {
            config = new EditFormResponseConfig( );
            config.setIdTask( task.getId( ) );
        }

        String action = request.getParameter( PARAMETER_ACTION );
        if ( action != null )
        {
            EditFormResponseConfigValue configValue = doProcessAction( action, request, config, getConfigValue( request, task ) );
            request.getSession( ).setAttribute( SESSION_CONFIG_VALUE + task.getId( ), configValue );
        }

        if ( create )
        {
            getTaskConfigService( ).create( config );
        }
        else
        {
            getTaskConfigService( ).update( config );
        }
        return null;
    }

    private EditFormResponseConfigValue doProcessAction( String action, HttpServletRequest request, EditFormResponseConfig config,
            EditFormResponseConfigValue configValue )
    {
        switch( action )
        {
            case ACTION_SELECT_MULTIFORM:
                config.setMultiform( request.getParameter( PARAMETER_MULTIFORM ) != null );
                config.setListConfigValues( new ArrayList<>( ) );
                break;
            case ACTION_SELECT_FORM:
                configValue = new EditFormResponseConfigValue( );
                configValue.setForm( FormHome.findByPrimaryKey( Integer.valueOf( request.getParameter( PARAMETER_FORM ) ) ) );
                break;
            case ACTION_SELECT_STEP:
                configValue.setStep( StepHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_STEP ) ) ) );
                configValue.setQuestion( null );
                break;
            case ACTION_SELECT_QUESTION:
                configValue.setQuestion( QuestionHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_QUESTION ) ) ) );
                break;
            case ACTION_SELECT_RESPONSE:
                configValue.setResponse( request.getParameter( PARAMETER_RESPONSE ) );
                config.addConfigValue( configValue );
                configValue = new EditFormResponseConfigValue( );
                break;
            case ACTION_SELECT_CODE:
                configValue = new EditFormResponseConfigValue( );
                configValue.setCode( request.getParameter( PARAMETER_CODE ) );
                break;
            case ACTION_REMOVE_MAPPING:
                int idToRemove = Integer.parseInt( request.getParameter( PARAMETER_MAPPING_ID ) );
                List<EditFormResponseConfigValue> newList = config.getListConfigValues( ).stream( )
                        .filter( value -> value.getIdConfigValue( ) != idToRemove ).collect( Collectors.toList( ) );
                config.setListConfigValues( newList );
                break;
            default:
                break;
        }
        return configValue;
    }

    /**
     * Gets the mapping being built for a task by the current administrator
     * 
     * @param request
     *            the request
     * @param task
     *            the task
     * @return the mapping being built, a new one if there is none
     */
    private EditFormResponseConfigValue getConfigValue( HttpServletRequest request, ITask task )
    {
        Object configValue = request.getSession( ).getAttribute( SESSION_CONFIG_VALUE + task.getId( ) );

        return ( configValue instanceof EditFormResponseConfigValue ) ? (EditFormResponseConfigValue) configValue : new EditFormResponseConfigValue( );
    }
}
