/*
 * Copyright (c) 2002-2020, City of Paris
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
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseTaskHistory;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class is a task to edit a form response
 *
 */
public class EditFormResponseTask extends AbstractFormsTask
{
    // Message
    private static final String MESSAGE_TASK_TITLE = "module.workflow.forms.task.editFormResponse.title";

    private static final String NULL = "null";
    private static final String SEPARATOR = ", ";

    @Inject
    @Named( "workflow-forms.editFormResponseConfigService" )
    private ITaskConfigService _taskEditFormConfigService;

    private final IEditFormResponseTaskService _editFormResponseTaskService;
    private final IEditFormResponseTaskHistoryService _editFormResponseTaskHistoryService;

    private List<EditableResponse> _listChangedResponse = new ArrayList<>( );

    /**
     * Constructor
     * 
     * @param formsTaskService
     *            the form task service
     * @param editFormResponseTaskService
     *            the edit form response task service
     * @param editFormResponseTaskHistoryService
     *            the edit form response task history service (returns history of forms workflow)
     */
    @Inject
    public EditFormResponseTask( IFormsTaskService formsTaskService, IEditFormResponseTaskService editFormResponseTaskService,
            IEditFormResponseTaskHistoryService editFormResponseTaskHistoryService )
    {
        super( formsTaskService );

        _editFormResponseTaskService = editFormResponseTaskService;
        _editFormResponseTaskHistoryService = editFormResponseTaskHistoryService;
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
        List<Question> listQuestion = _editFormResponseTaskService.findQuestionsToEdit( this, formResponse );
        List<EditableResponse> listEditableResponse = _formsTaskService.createEditableResponses( formResponse, listQuestion, request );
        _listChangedResponse = _formsTaskService.findChangedResponses( listEditableResponse );
        List<FormQuestionResponse> listChangedResponseToSave = new ArrayList<>( );

        for ( EditableResponse editableResponse : _listChangedResponse )
        {
            listChangedResponseToSave.add( editableResponse.getResponseFromForm( ) );
        }

        _editFormResponseTaskService.saveResponses( formResponse, listChangedResponseToSave );
    }

    @Override
    protected void saveTaskInformation( int nIdHistory )
    {
        for ( EditableResponse editableResponse : _listChangedResponse )
        {
            EditFormResponseTaskHistory editFormResponseTaskHistory = new EditFormResponseTaskHistory( );
            editFormResponseTaskHistory.setQuestion( editableResponse.getQuestion( ) );
            editFormResponseTaskHistory.setIdTask( getId( ) );
            editFormResponseTaskHistory.setIdHistory( nIdHistory );

            String previousValue = StringUtils.EMPTY;
            editFormResponseTaskHistory.setPreviousValue( createPreviousNewValue( editableResponse.getResponseSaved( ), previousValue ) );

            String newValue = StringUtils.EMPTY;
            editFormResponseTaskHistory.setNewValue( createPreviousNewValue( editableResponse.getResponseFromForm( ), newValue ) );

            _editFormResponseTaskHistoryService.create( editFormResponseTaskHistory );
        }
    }

    /**
     * Create a string with previous or new value to set in history
     * 
     * @param responseForm
     * @param value
     * @return a value ready to be inserted in history
     */
    private String createPreviousNewValue( FormQuestionResponse responseForm, String value )
    {
        if ( responseForm == null )
        {
            return value;
        }
        for ( int i = 0; i < responseForm.getEntryResponse( ).size( ); i++ )
        {
            Response response = responseForm.getEntryResponse( ).get( i );

            if ( response.getFile( ) != null )
            {
                File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );

                if ( file != null )
                {
                    value = response.getFile( ).getTitle( );
                }
            }
            else
            {
                if ( response.getToStringValueResponse( ) == null || response.getToStringValueResponse( ).equalsIgnoreCase( NULL ) )
                {
                    value = StringUtils.EMPTY;
                }
                else
                {
                    value += response.getToStringValueResponse( );
                }
            }

            if ( i + 1 != responseForm.getEntryResponse( ).size( ) )
            {
                value += SEPARATOR;
            }
        }
        return value;
    }

    @Override
    public void doRemoveConfig( )
    {
        _taskEditFormConfigService.remove( getId( ) );
    }
}
