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
package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;


import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormResponseService;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfigValue;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This class is a task to edit a form response
 *
 */
public class EditFormResponseAutoUpdateTask extends SimpleTask
{
    private final IFormsTaskService _formsTaskService;

    @Inject
    private IEditFormResponseTaskService _editFormResponseTaskService;

    @Inject
    @Named( "workflow-forms.editFormResponseConfigService" )
    private ITaskConfigService _taskEditFormConfigService;

    // Message
    private static final String MESSAGE_TASK_TITLE = "module.workflow.forms.task.editFormResponseAutoUpdate.title";

    /**
     * Constructor
     * 
     * @param formsTaskService
     *            the form task service
     */
    @Inject
    public EditFormResponseAutoUpdateTask( IFormsTaskService formsTaskService )
    {
        _formsTaskService = formsTaskService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale local )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, local );
    }

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        EditFormResponseConfig config = _taskEditFormConfigService.findByPrimaryKey( getId( ) );
        List<EditFormResponseConfigValue> configValueList = config.getListConfigValues( );
        ResourceHistory resourceHistory = _formsTaskService.findResourceHistory( nIdResourceHistory );
        
        if ( resourceHistory == null )
        {
            return;
        }
        
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        if ( formResponse == null )
        {
            return;
        }
        
        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome
                .getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
        List<FormQuestionResponse> listFormQuestionResponseToSave = new ArrayList<>( );
        for ( EditFormResponseConfigValue configValue : configValueList )
        {
            // the whole list is filtered again for each configured value : narrowing the list being iterated left the second value and the ones
            // after it nothing to match, and they all fell into the branch that builds a response from nothing
            List<FormQuestionResponse> listMatching = config.isMultiform( )
                    ? listFormQuestionResponse.stream( ).filter( fqr -> fqr.getQuestion( ).getCode( ).equals( configValue.getCode( ) ) )
                            .collect( Collectors.toList( ) )
                    : listFormQuestionResponse.stream( ).filter( fqr -> fqr.getQuestion( ).getId( ) == configValue.getQuestion( ).getId( ) )
                            .collect( Collectors.toList( ) );
            FormQuestionResponse questionResponse = listMatching.isEmpty( ) ? null : listMatching.get( 0 );

            if ( questionResponse == null )
            {
                Question question = findQuestion( configValue, config.isMultiform( ), formResponse );

                if ( question == null || question.getEntry( ) == null )
                {
                    // in multiform the configuration names a code and holds no question at all : reading one from it threw, and the whole
                    // workflow action failed with it, leaving the response in the state it came from
                    AppLogService.error( "Workflow forms : task {} writes nothing on the response {}, the form {} carries no question for {}", getId( ),
                            formResponse.getId( ), formResponse.getFormId( ),
                            config.isMultiform( ) ? "the code " + configValue.getCode( ) : "this configuration" );

                    continue;
                }

                questionResponse = new FormQuestionResponse( );
                Response response = new Response( );
                response.setEntry( question.getEntry( ) );
                questionResponse.setQuestion( question );
                questionResponse.setIdStep( question.getIdStep( ) );
                questionResponse.setEntryResponse( Arrays.asList( response ) );
                questionResponse.setIdFormResponse( formResponse.getId( ) );
            }

            if ( !setAnswer( questionResponse, configValue.getResponse( ) ) )
            {
                // the value was written even when no choice of this question carried it, giving a response that names an answer the form does
                // not offer. Multiform makes it reachable : the forms carrying one code need not offer the same choices
                AppLogService.error( "Workflow forms : task {} writes nothing on the response {}, the question {} offers no answer valued {}", getId( ),
                        formResponse.getId( ), questionResponse.getQuestion( ).getId( ), configValue.getResponse( ) );

                continue;
            }

            listFormQuestionResponseToSave.add( questionResponse );
        }
        _editFormResponseTaskService.saveResponses( formResponse, listFormQuestionResponseToSave );
        FormResponseService.getInstance( ).saveFormResponse( formResponse );
    }

    /**
     * The question a configured value designates for this response.
     *
     * In multiform the configuration names a technical code and keeps no question : several forms carry that code, so the one that answers is the
     * question of the form this response was filled on, the way the task that lets an agent edit a response already resolves it.
     *
     * @param configValue
     *            the configured value
     * @param bMultiform
     *            whether the configuration designates its question by code
     * @param formResponse
     *            the response being processed
     * @return the question, or null when the form carries none
     */
    private Question findQuestion( EditFormResponseConfigValue configValue, boolean bMultiform, FormResponse formResponse )
    {
        if ( !bMultiform )
        {
            return configValue.getQuestion( );
        }

        for ( Question question : QuestionHome.findByCode( configValue.getCode( ) ) )
        {
            Step step = StepHome.findByPrimaryKey( question.getIdStep( ) );

            if ( step != null && step.getIdForm( ) == formResponse.getFormId( ) )
            {
                return question;
            }
        }

        return null;
    }

    /**
     * Writes the configured value on the response, and the choice carrying it when the question offers choices.
     *
     * A question without a choice holds free text, which the configuration screen lets an administrator type : such a value is written as it is. A
     * question with choices only accepts one of them, otherwise the response would name an answer the form does not offer.
     *
     * @param questionResponse
     *            the response to write on
     * @param strValue
     *            the configured value
     * @return false when the question has choices and none of them carries this value
     */
    private boolean setAnswer( FormQuestionResponse questionResponse, String strValue )
    {
        List<Field> listChoice = questionResponse.getQuestion( ).getEntry( ).getFields( ).stream( )
                .filter( field -> IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) ).collect( Collectors.toList( ) );
        Response response = questionResponse.getEntryResponse( ).get( 0 );

        if ( listChoice.isEmpty( ) )
        {
            response.setResponseValue( strValue );

            return true;
        }

        for ( Field field : listChoice )
        {
            // genatt_field.value is nullable, and comparing from the field threw on the first one holding none
            if ( StringUtils.equals( field.getValue( ), strValue ) )
            {
                response.setResponseValue( strValue );
                response.setField( field );

                return true;
            }
        }

        return false;
    }
}
