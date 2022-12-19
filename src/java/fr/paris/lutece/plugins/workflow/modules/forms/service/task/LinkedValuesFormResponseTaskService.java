/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfigValue;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;

/**
 * 
 * LinkedValuesFormResponseTaskService
 *
 */
public class LinkedValuesFormResponseTaskService implements ILinkedValuesFormResponseTaskService
{

    public static final String BEAN_NAME = "workflow-forms.linkedValuesFormResponseTaskService";

    @Inject
    @Named( LinkedValuesFormResponseConfigService.BEAN_NAME )
    private ITaskConfigService _taskLinkedValuesFormResponseConfigService;

    @Override
    public void updateFormResponse( int nIdFormResponse, LinkedValuesFormResponseConfigValue configValue )
    {
        Question questionTarget = QuestionHome.findByPrimaryKey( configValue.getIdQuestionTarget( ) );
        boolean isResponseSourceContainsValue = isResponseContainsValue( nIdFormResponse, configValue.getIdQuestionSource( ), configValue.getQuestionSourceValue( ) );
        boolean isResponseTargetContainsValue = isResponseContainsValue( nIdFormResponse, configValue.getIdQuestionTarget( ), configValue.getQuestionTargetValue( ) );

        if ( questionTarget != null && isResponseSourceContainsValue && !isResponseTargetContainsValue )
        {
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( questionTarget.getEntry( ).getEntryType( ) );
            FormQuestionResponse formQuestionResponse = getFormQuestionResponse( nIdFormResponse, questionTarget, createResponse( questionTarget, configValue.getQuestionTargetValue( ) ) );
            formQuestionResponse.setIdFormResponse( nIdFormResponse );

            entryDataService.save( formQuestionResponse );
        }
    }

    @Override
    public boolean isResponseContainsValue( int nIdFormResponse, int nIdQuestion, String strQuestionResponseValue )
    {
        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( nIdFormResponse, nIdQuestion );

        if ( listFormQuestionResponse != null )
        {
            for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
            {
                for ( Response response : formQuestionResponse.getEntryResponse( ) )
                {
                    if ( strQuestionResponseValue.equals( response.getResponseValue( ) ) )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return formQuestionReponse
     * 
     * @param nIdFormResponse
     * @param question
     * @param response
     * @return formQuestionReponse
     */
    private FormQuestionResponse getFormQuestionResponse( int nIdFormResponse, Question question, Response response )
    {
        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( nIdFormResponse, question.getId( ) );

        if ( listFormQuestionResponse.isEmpty( ) )
        {
            FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
            formQuestionResponse.setQuestion( question );
            formQuestionResponse.setIdStep( question.getIdStep( ) );
            formQuestionResponse.setEntryResponse( Collections.singletonList( response ) );

            return formQuestionResponse;
        }
        
        listFormQuestionResponse.get( 0 ).getEntryResponse( ).add( response );
        return listFormQuestionResponse.get( 0 );
    }

    /**
     * Init new response
     * 
     * @param question
     * @param strResponseValue
     * @return new reponse
     */
    private Response createResponse( Question question, String strResponseValue )
    {
        Response response = new Response( );

        response.setEntry( question.getEntry( ) );
        response.setResponseValue( strResponseValue );
        response.setIterationNumber( 0 );

        for ( Field field : question.getEntry( ).getFields( ) )
        {
            if ( field.getValue( ).equals( strResponseValue ) )
            {
                response.setField( field );
            }
        }
        return response;
    }

}
