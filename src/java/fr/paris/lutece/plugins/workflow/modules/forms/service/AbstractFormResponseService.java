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
package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

public class AbstractFormResponseService
{
    @Inject
    private IStateService _stateService;

    @Inject
    private IActionService _actionService;

    @Inject
    private IResourceWorkflowService _resourceWorkflowService;

    @Inject
    private IEditFormResponseTaskService _editFormResponseTaskService;

    @Inject
    protected IFormsTaskService _formsTaskService;

    protected void doChangeResponseState( ITask task, int idStateAfterEdition, int idHistory, Locale locale )
    {
        State state = _stateService.findByPrimaryKey( idStateAfterEdition );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

        if ( state != null && action != null )
        {
            FormResponse response = _formsTaskService.getFormResponseFromIdHistory( idHistory );

            // Update Resource
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( response.getId( ), FormResponse.RESOURCE_TYPE,
                    action.getWorkflow( ).getId( ) );
            resourceWorkflow.setState( state );
            _resourceWorkflowService.update( resourceWorkflow );

            WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( response.getId( ), FormResponse.RESOURCE_TYPE, action.getStateAfter( ).getId( ),
                    resourceWorkflow.getExternalParentId( ), locale );
            // if new state have action automatic
            WorkflowService.getInstance( ).executeActionAutomatic( response.getId( ), FormResponse.RESOURCE_TYPE, action.getWorkflow( ).getId( ),
                    resourceWorkflow.getExternalParentId( ) );
        }
    }

    protected void doEditResponseData( HttpServletRequest request, FormResponse response, List<Question> listQuestions )
    {
        List<EditableResponse> listEditableResponse = _formsTaskService.createEditableResponses( response, listQuestions, request );
        List<EditableResponse> listChangedResponse = _formsTaskService.findChangedResponses( listEditableResponse );
        List<FormQuestionResponse> listChangedResponseToSave = new ArrayList<>( );

        for ( EditableResponse editableResponse : listChangedResponse )
        {
            listChangedResponseToSave.add( editableResponse.getResponseFromForm( ) );
        }

        _editFormResponseTaskService.saveResponses( response, listChangedResponseToSave );
    }

    protected boolean isRecordStateValid( ITask task, TaskConfig config, int idHistory )
    {
        boolean bIsValid = false;
        if ( task != null && config != null )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( action != null && action.getStateAfter( ) != null )
            {
                FormResponse formResponse = _formsTaskService.getFormResponseFromIdHistory( idHistory );

                // Update Resource
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( formResponse.getId( ), FormResponse.RESOURCE_TYPE,
                        action.getWorkflow( ).getId( ) );

                if ( resourceWorkflow != null && resourceWorkflow.getState( ) != null
                        && resourceWorkflow.getState( ).getId( ) == action.getStateAfter( ).getId( ) )
                {
                    bIsValid = true;
                }
            }
        }
        return bIsValid;
    }
}
