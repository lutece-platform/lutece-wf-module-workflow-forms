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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.FormResponseService;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfigValue;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class is a task to edit a form response
 *
 */
@Dependent
@Named( "workflow-forms.editFormResponseAutoUpdateTask" )
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
            listFormQuestionResponse = config.isMultiform( )
                    ? listFormQuestionResponse.stream( ).filter( fqr -> fqr.getQuestion( ).getCode( ).equals( configValue.getCode( ) ) )
                            .collect( Collectors.toList( ) )
                    : listFormQuestionResponse.stream( ).filter( fqr -> fqr.getQuestion( ).getId( ) == configValue.getQuestion( ).getId( ) )
                            .collect( Collectors.toList( ) );
            FormQuestionResponse questionResponse = listFormQuestionResponse.isEmpty( ) ? null : listFormQuestionResponse.get( 0 );
            if ( questionResponse == null )
            {
                questionResponse = new FormQuestionResponse( );
                Response response = new Response( );
                Question question = configValue.getQuestion( );
                response.setEntry( question.getEntry( ) );
                questionResponse.setEntryResponse( new ArrayList<Response>( ) );
                questionResponse.setQuestion( question );
                questionResponse.setIdStep( question.getIdStep( ) );
                questionResponse.setEntryResponse( Arrays.asList( response ) );
                questionResponse.setIdFormResponse( formResponse.getId( ) );
            }
            questionResponse.getEntryResponse( ).get( 0 ).setResponseValue( configValue.getResponse( ) );

            for ( Field field : questionResponse.getQuestion( ).getEntry( ).getFields( ) )
            {
                if ( field.getValue( ).equals( configValue.getResponse( ) ) )
                {
                    questionResponse.getEntryResponse( ).get( 0 ).setField( field );
                }
            }
            listFormQuestionResponseToSave.add( questionResponse );
        }
        _editFormResponseTaskService.saveResponses( formResponse, listFormQuestionResponseToSave );
        FormResponseService.getInstance( ).saveFormResponse( formResponse );
    }
}
