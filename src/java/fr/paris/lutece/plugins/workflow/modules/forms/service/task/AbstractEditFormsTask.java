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
package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseTaskHistory;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;

/**
 * This abstract class is represents a task for the plugin-forms
 *
 */
public abstract class AbstractEditFormsTask extends SimpleTask
{
    private final IFormsTaskService _formsTaskService;
    private final IEditFormResponseTaskService _editFormResponseTaskService;
    private final IEditFormResponseTaskHistoryService _editFormResponseTaskHistoryService;
    
    @Inject
    @Named( "workflow-forms.editFormResponseConfigService" )
    private ITaskConfigService _taskEditFormConfigService;
    
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
    public AbstractEditFormsTask( IFormsTaskService formsTaskService, IEditFormResponseTaskService editFormResponseTaskService,
            IEditFormResponseTaskHistoryService editFormResponseTaskHistoryService)
    {
        super( );
        _formsTaskService = formsTaskService;
        _editFormResponseTaskService = editFormResponseTaskService;
        _editFormResponseTaskHistoryService = editFormResponseTaskHistoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _formsTaskService.findResourceHistory( nIdHistory );

        if ( resourceHistory != null )
        {
            FormResponse formResponse = _formsTaskService.findFormResponseFrom( resourceHistory );
            processTask( formResponse, request, locale );
            saveTaskInformation( nIdHistory );
        }

    }

    private void processTask( FormResponse formResponse, HttpServletRequest request, Locale locale )
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

    private void saveTaskInformation( int nIdHistory )
    {
        for ( EditableResponse editableResponse : _listChangedResponse )
        {
            EditFormResponseTaskHistory editFormResponseTaskHistory = new EditFormResponseTaskHistory( );
            editFormResponseTaskHistory.setQuestion( editableResponse.getQuestion( ) );
            editFormResponseTaskHistory.setIdTask( getId( ) );
            editFormResponseTaskHistory.setIdHistory( nIdHistory );

            editFormResponseTaskHistory.setPreviousValue( _formsTaskService.createPreviousNewValue( editableResponse.getResponseSaved( ) ) );
            editFormResponseTaskHistory.setNewValue( _formsTaskService.createPreviousNewValue( editableResponse.getResponseFromForm( ) ) );

            _editFormResponseTaskHistoryService.create( editFormResponseTaskHistory );
        }
    }

    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        ResourceHistory history = new ResourceHistory( );
        history.setId( nIdHistory );
        _editFormResponseTaskHistoryService.removeAllByHistoryAndTask( history, this );
    }
    
    @Override
    public void doRemoveConfig( )
    {
        _taskEditFormConfigService.remove( getId( ) );
    }
}
