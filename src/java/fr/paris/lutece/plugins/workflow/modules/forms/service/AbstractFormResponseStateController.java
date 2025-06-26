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
package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfigHome;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateController;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.util.ReferenceList;

public abstract class AbstractFormResponseStateController implements IChooseStateController
{
    // Mark
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_ID_FORM = "id_form";
    private static final String MARK_STEP_LIST = "list_step";
    private static final String MARK_ID_STEP = "id_step";
    private static final String MARK_QUESTION_LIST = "question_list";
    private static final String MARK_ID_QUESTION = "id_question";
    private static final String MARK_VALUE_LIST = "value_list";
    private static final String MARK_RESPONSE_VALUE = "response_value";
    private static final String MARK_MULTIFORM = "multiform";
    private static final String MARK_CODE_LIST = "code_list";
    private static final String MARK_CODE = "code";

    // Parameters
    private static final String PARAMETER_ACTION = "apply";
    private static final String PARAMETER_FORM = "form_select";
    private static final String PARAMETER_STEP = "step_select";
    private static final String PARAMETER_QUESTION = "question_select";
    private static final String PARAMETER_VALUE = "response_value";
    private static final String PARAMETER_MULTIFORM = "multiform";
    private static final String PARAMETER_CODE = "code_select";

    // Actions
    private static final String ACTION_SELECT_FORM = "select_form_config";
    private static final String ACTION_SELECT_STEP = "select_step_config";
    private static final String ACTION_SELECT_QUESTION = "select_question_config";
    private static final String ACTION_SELECT_MULTIFORM = "select_multiform";

    @Override
    public boolean hasConfig( )
    {
        return true;
    }

    @Override
    public void doRemoveConfig( ITask task )
    {
        FormResponseValueStateControllerConfigHome.removeByTask( task.getId( ) );
    }

    protected Map<String, Object> createModelConfig( ITaskConfig config )
    {
        FormResponseValueStateControllerConfig controllerConfig = loadConfig( config.getIdTask( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_LIST, FormHome.getFormsReferenceList( ) );
        model.put( MARK_MULTIFORM, controllerConfig.isMultiform( ) );
        model.put( MARK_CODE_LIST, getCodeReferenceList( ) );
        model.put( MARK_CODE, controllerConfig.getCode( ) );

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

        return model;
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
                case ACTION_SELECT_MULTIFORM:
                    controllerConfig.setMultiform( request.getParameter( PARAMETER_MULTIFORM ) != null );
                    controllerConfig.setForm( null );
                    controllerConfig.setStep( null );
                    controllerConfig.setQuestion( null );
                    controllerConfig.setValue( null );
                    controllerConfig.setCode( null );
                    break;
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
        if ( controllerConfig.isMultiform( ) )
        {
            controllerConfig.setCode( request.getParameter( PARAMETER_CODE ) );
        }
        else
        {
            if ( NumberUtils.isCreatable( request.getParameter( PARAMETER_QUESTION ) ) )
            {
                controllerConfig.setQuestion( QuestionHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_QUESTION ) ) ) );
            }
            controllerConfig.setCode( null );
        }
        controllerConfig.setValue( request.getParameter( PARAMETER_VALUE ) );
        FormResponseValueStateControllerConfigHome.update( controllerConfig );
    }

    protected abstract ReferenceList getResponseReferenceList( int idQuestion );

    protected FormResponseValueStateControllerConfig loadConfig( int idTask )
    {
        FormResponseValueStateControllerConfig controllerConfig = FormResponseValueStateControllerConfigHome.findByTask( idTask );
        if ( controllerConfig == null )
        {
            controllerConfig = new FormResponseValueStateControllerConfig( );
            controllerConfig.setIdTask( idTask );
            FormResponseValueStateControllerConfigHome.create( controllerConfig );
        }
        return controllerConfig;
    }

    protected ReferenceList getQuestionReferenceList( int idStep )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( -1, "" );
        if ( idStep != -1 )
        {
            List<Question> questionList = QuestionHome.getQuestionsListByStep( idStep );
            for ( Question question : questionList )
            {
                if ( canQuestionBeCondition( question ) )
                {
                    refList.addItem( question.getId( ), question.getTitle( ) );
                }
            }
        }

        return refList;
    }

    protected ReferenceList getCodeReferenceList( )
    {
        ReferenceList refList = new ReferenceList( );
        List<Question> questionList = QuestionHome.getQuestionsList( ).stream( ).filter( this::canQuestionBeCondition ).collect( Collectors.toList( ) );
        List<String> codeList = questionList.stream( ).map( Question::getCode ).distinct( ).collect( Collectors.toList( ) );
        codeList.sort( Comparator.naturalOrder( ) );
        for ( String code : codeList )
        {
            refList.addItem( code, code );
        }
        return refList;
    }

    protected abstract boolean canQuestionBeCondition( Question question );

    protected Response getResponseFromConfigAndFormResponse( FormResponseValueStateControllerConfig config, int idResponse )
    {
        Question question = null;
        if ( config.isMultiform( ) )
        {
            FormResponse formResponse = FormResponseHome.findByPrimaryKey( idResponse );
            for ( Question q : QuestionHome.findByCode( config.getCode( ) ) )
            {
                Step step = StepHome.findByPrimaryKey( q.getIdStep( ) );
                if ( step.getIdForm( ) == formResponse.getFormId( ) )
                {
                    question = q;
                    break;
                }
            }
        }
        else
        {
            question = config.getQuestion( );
        }

        if ( question == null )
        {
            return null;
        }
        List<FormQuestionResponse> responseList = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( idResponse, question.getId( ) );

        if ( CollectionUtils.isEmpty( responseList ) )
        {
            return null;
        }
        List<Response> entryResponseList = responseList.get( 0 ).getEntryResponse( );
        if ( CollectionUtils.isEmpty( entryResponseList ) )
        {
            return null;
        }
        return entryResponseList.get( 0 );
    }
}
