/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfigHome;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

public class FormResponseValueStateController extends AbstractFormResponseStateController
{
    private static final String BEAN_NAME = "workflow-forms.formResponseValueStateController";

    private static final String PROPERTY_KEY_LABEL = "module.workflow.forms.state.control.forms.response";
    private static final String PROPERTY_KEY_HELP = "module.workflow.forms.state.control.forms.response.help";

    // Mark
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_ID_FORM = "id_form";
    private static final String MARK_STEP_LIST = "list_step";
    private static final String MARK_ID_STEP = "id_step";
    private static final String MARK_QUESTION_LIST = "question_list";
    private static final String MARK_ID_QUESTION = "id_question";
    private static final String MARK_VALUE_LIST = "value_list";
    private static final String MARK_RESPONSE_VALUE = "response_value";

    // Parameters
    private static final String PARAMETER_ACTION = "apply";
    private static final String PARAMETER_FORM = "form_select";
    private static final String PARAMETER_STEP = "step_select";
    private static final String PARAMETER_QUESTION = "question_select";
    private static final String PARAMETER_VALUE = "response_select";

    // Actions
    private static final String ACTION_SELECT_FORM = "select_form_config";
    private static final String ACTION_SELECT_STEP = "select_step_config";
    private static final String ACTION_SELECT_QUESTION = "select_question_config";

    private static final String TEMPLATE_TASK_CONFIG = "admin/plugins/workflow/modules/forms/state_control_form_response_value.html";

    @Override
    public String getLabelKey( )
    {
        return PROPERTY_KEY_LABEL;
    }

    @Override
    public String getHelpKey( )
    {
        return PROPERTY_KEY_HELP;
    }

    @Override
    public String getName( )
    {
        return BEAN_NAME;
    }

    @Override
    public boolean control( ITask task, int nIdResource, String strResourceType )
    {
        FormResponseValueStateControllerConfig config = loadConfig( task.getId( ) );
        if ( StringUtils.isEmpty( config.getValue( ) ) )
        {
            return false;
        }

        Response response = getResponseFromQuestionAndFormResponse( config.getQuestion( ).getId( ), nIdResource );
        if ( response == null )
        {
            return false;
        }
        return config.getValue( ).equals( response.getResponseValue( ) );
    }

    @Override
    public void doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        FormResponseValueStateControllerConfig controllerConfig = loadConfig( task.getId( ) );
        String action = request.getParameter( PARAMETER_ACTION );
        if ( action != null )
        {
            switch( action )
            {
                case ACTION_SELECT_FORM:
                    controllerConfig.setForm( FormHome.findByPrimaryKey( Integer.valueOf( request.getParameter( PARAMETER_FORM ) ) ) );
                    controllerConfig.setStep( null );
                    controllerConfig.setQuestion( null );
                    break;
                case ACTION_SELECT_STEP:
                    controllerConfig.setStep( StepHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_STEP ) ) ) );
                    controllerConfig.setQuestion( null );
                    break;
                case ACTION_SELECT_QUESTION:
                    controllerConfig.setQuestion( QuestionHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_QUESTION ) ) ) );
                    break;
                default:
                    break;
            }
        }
        controllerConfig.setValue( request.getParameter( PARAMETER_VALUE ) );
        FormResponseValueStateControllerConfigHome.update( controllerConfig );
    }

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITaskConfig config )
    {
        FormResponseValueStateControllerConfig controllerConfig = loadConfig( config.getIdTask( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_LIST, FormHome.getFormsReferenceList( ) );

        if ( controllerConfig.getForm( ) != null )
        {
            model.put( MARK_ID_FORM, controllerConfig.getForm( ).getId( ) );
            model.put( MARK_STEP_LIST, StepHome.getStepReferenceListByForm( controllerConfig.getForm( ).getId( ) ) );
        }
        if ( controllerConfig.getStep( ) != null )
        {
            model.put( MARK_ID_STEP, controllerConfig.getStep( ).getId( ) );
            model.put( MARK_QUESTION_LIST, getQuestionReferenceList( controllerConfig.getStep( ).getId( ) ) );
        }
        if ( controllerConfig.getQuestion( ) != null )
        {
            model.put( MARK_ID_QUESTION, controllerConfig.getQuestion( ).getId( ) );
            model.put( MARK_VALUE_LIST, getResponseReferenceList( controllerConfig.getQuestion( ).getId( ) ) );
        }
        if ( StringUtils.isNotEmpty( controllerConfig.getValue( ) ) )
        {
            model.put( MARK_RESPONSE_VALUE, controllerConfig.getValue( ) );
        }
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CONFIG, locale, model );
        return template.getHtml( );
    }

    @Override
    protected boolean canQuestionBeCondition( Question question )
    {
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) );

        return entryTypeService instanceof EntryTypeSelect || entryTypeService instanceof EntryTypeCheckBox || entryTypeService instanceof EntryTypeRadioButton;
    }

    private ReferenceList getResponseReferenceList( int idQuestion )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( "", "" );
        if ( idQuestion != -1 )
        {
            Question question = QuestionHome.findByPrimaryKey( idQuestion );
            for ( Field field : question.getEntry( ).getFields( ) )
            {
                if ( IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) )
                {
                    refList.addItem( field.getValue( ), field.getTitle( ) );
                }
            }
        }
        return refList;
    }
}
