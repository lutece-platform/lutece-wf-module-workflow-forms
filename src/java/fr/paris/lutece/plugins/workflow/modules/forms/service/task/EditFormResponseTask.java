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
package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class is a task to edit a form response
 *
 */
public class EditFormResponseTask extends AbstractFormsTask
{
    // Message
    private static final String MESSAGE_TASK_TITLE = "module.workflow.forms.task.editFormResponse.title";

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
    public EditFormResponseTask( IFormsTaskService formsTaskService, IEditFormResponseTaskService editFormResponseTaskService )
    {
        super( formsTaskService );

        _editFormResponseTaskService = editFormResponseTaskService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale local )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, local );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( FormResponse formResponse, HttpServletRequest request, Locale locale )
    {
        List<Question> listQuestion = _editFormResponseTaskService.findQuestionsToEdit( formResponse );
        List<EditableResponse> listEditableResponse = createEditableResponses( formResponse, listQuestion, request );
        List<FormQuestionResponse> listChangedResponse = findChangedResponses( listEditableResponse );
        saveResponses( listChangedResponse );
        
        // TODO : save task information
    }

    /**
     * Creates the editable responses from the specified form response and questions
     * 
     * @param formResponse
     *            the form response
     * @param listQuestion
     *            the list of questions
     * @param request
     *            the request containing the user inputs
     * @return the list of editable responses
     */
    private List<EditableResponse> createEditableResponses( FormResponse formResponse, List<Question> listQuestion, HttpServletRequest request )
    {
        List<EditableResponse> listEditableResponse = new ArrayList<>( );

        for ( Question question : listQuestion )
        {
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            FormQuestionResponse responseFromForm = entryDataService.createResponseFromRequest( question, request );
            responseFromForm.setIdFormResponse( formResponse.getId( ) );
            FormQuestionResponse responseSave = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( formResponse.getId( ), question.getId( ) );

            EditableResponse editableResponse = new EditableResponse( responseSave, responseFromForm );
            listEditableResponse.add( editableResponse );
        }

        return listEditableResponse;
    }

    /**
     * Finds the responses that have changed
     * 
     * @param listEditableResponse
     *            the list of editable responses
     * @return the list of responses that have changed
     */
    private List<FormQuestionResponse> findChangedResponses( List<EditableResponse> listEditableResponse )
    {
        List<FormQuestionResponse> listChangedResponse = new ArrayList<>( );

        for ( EditableResponse editableResponse : listEditableResponse )
        {
            IEntryDataService dataService = EntryServiceManager.getInstance( ).getEntryDataService( editableResponse._question.getEntry( ).getEntryType( ) );

            if ( dataService.isResponseChanged( editableResponse._responseSaved, editableResponse._responseFromForm ) )
            {
                listChangedResponse.add( editableResponse._responseFromForm );
            }
        }

        return listChangedResponse;
    }

    /**
     * Saves the specified responses
     * 
     * @param listFormQuestionResponse
     *            the responses to save
     */
    private void saveResponses( List<FormQuestionResponse> listFormQuestionResponse )
    {
        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            Question question = formQuestionResponse.getQuestion( );
            IEntryDataService dataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            dataService.save( formQuestionResponse );
        }
    }

    /**
     * This class represents an editable response
     *
     */
    private static class EditableResponse
    {
        private final Question _question;
        private final FormQuestionResponse _responseSaved;
        private final FormQuestionResponse _responseFromForm;

        /**
         * Constructor
         * 
         * @param responseSaved
         *            the saved response
         * @param responseFromForm
         *            the response from the form
         */
        EditableResponse( FormQuestionResponse responseSaved, FormQuestionResponse responseFromForm )
        {
            _responseSaved = responseSaved;
            _responseFromForm = responseFromForm;
            
            if ( responseSaved != null )
            {
                responseFromForm.setId( responseSaved.getId( ) );
            }

            _question = _responseFromForm.getQuestion( );
        }
    }

}
