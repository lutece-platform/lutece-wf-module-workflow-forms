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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
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
import fr.paris.lutece.util.sql.TransactionManager;
import org.apache.commons.lang3.StringUtils;

/**
 * Base class shared by the task services that let an agent ask an end user to modifify part of an
 * already submitted {@link FormResponse}.
 * <p>
 * It holds the common methods to handle the modification.
 *
 * @param <R>
 *            the specific response type handled by the concrete task service
 */
public abstract class AbstractFormResponseService<R>
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
    
    @Inject
    private WorkflowService _workflowService;

    protected static final String ATTRIBUTE_SUBMITTED_RESPONSES = "workflow-forms.submittedFormQuestionResponses";
    protected static final String ATTRIBUTE_ID_GROUP_TO_ITERATE = "workflow-forms.idGroupToIterate";

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

            _workflowService.doProcessAutomaticReflexiveActions( response.getId( ), FormResponse.RESOURCE_TYPE, action.getStateAfter( ).getId( ),
                    resourceWorkflow.getExternalParentId( ), locale, null );
            // if new state have action automatic
            _workflowService.executeActionAutomatic( response.getId( ), FormResponse.RESOURCE_TYPE, action.getWorkflow( ).getId( ),
                    resourceWorkflow.getExternalParentId( ), null );
        }
    }

    protected void doEditResponseData( HttpServletRequest request, FormResponse response, List<Question> listQuestions, int idTask, int idHistory )
    {
        List<EditableResponse> listEditableResponse = _formsTaskService.createEditableResponses( response, listQuestions, request );
        List<EditableResponse> listChangedResponse = _formsTaskService.findChangedResponses( listEditableResponse );
        List<FormQuestionResponse> listChangedResponseToSave = new ArrayList<>( );

        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
        try
        {
            for ( EditableResponse editableResponse : listChangedResponse )
            {
                listChangedResponseToSave.add( editableResponse.getResponseFromForm( ) );
                createTaskHistory( editableResponse, idTask, idHistory );
            }

            _editFormResponseTaskService.persistResponses( response, listChangedResponseToSave );
            _formsTaskService.removeHiddenConditionalTargetResponses( request, response, listQuestions );
            _formsTaskService.removeOrphanIterations( response, listQuestions );

            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            throw e;
        }
        _editFormResponseTaskService.fireFormResponseUpdate( response );
    }

    protected abstract void createTaskHistory( EditableResponse editableResponse, int idTask, int idHistory );

    /**
     * Get the list of editable Questions for the given specific response object.
     *
     * @param formResponse
     *            the FormResponse being edited
     * @param response
     *            the specific response object
     * @return the List of Questions that are editable for this response
     */
    protected abstract List<Question> getListQuestionToEditFromResponse( FormResponse formResponse, R response );

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

    /**
     * Check whether the values from the given List of FormQuestionResponse are valid
     * 
     * @param listFormQuestionResponse
     *            the List of FormQuestionResponse to check
     * @return true if all the Responses have valid values, returns false otherwise
     */
    protected boolean areFormResponsesValid( List<FormQuestionResponse> listFormQuestionResponse )
    {
        return _formsTaskService.areFormQuestionResponsesValid( listFormQuestionResponse );
    }

    /**
     * Gets the List of FormQuestionResponse containing the values the user previously tried to submit, on this
     * request.
     *
     * @param request
     *            the HTTP request
     * @return the List of FormQuestionResponse, or an empty List if nothing was submitted yet
     */
    @SuppressWarnings( "unchecked" )
    public List<FormQuestionResponse> getSubmittedFormResponseList( HttpServletRequest request )
    {
        List<FormQuestionResponse> listSubmitted = (List<FormQuestionResponse>) request.getAttribute( ATTRIBUTE_SUBMITTED_RESPONSES );
        return listSubmitted != null ? listSubmitted : Collections.emptyList( );
    }

    /**
     * Adds an iteration to the given group.
     *
     * @param request
     *            the HTTP request
     * @param response
     *            the specific response object (ResubmitFormResponse, CompleteFormResponse...)
     * @param idHistory
     *            the id of the resource history the FormResponse is attached to
     * @param nIdGroupToIterate
     *            the id of the FormDisplay (group) to add an iteration to
     */
    public void doAddIterationResponse( HttpServletRequest request, R response, int idHistory, int nIdGroupToIterate )
    {
        final FormResponse formResponse = _formsTaskService.getFormResponseFromIdHistory( idHistory );
        if ( formResponse == null )
        {
            return;
        }

        final List<Question> listQuestionToEditFromResponse = this.getListQuestionToEditFromResponse(formResponse, response);
        final List<Question> listQuestions = _formsTaskService.expandWithSubmittedIterations( request, listQuestionToEditFromResponse);
        request.setAttribute( ATTRIBUTE_SUBMITTED_RESPONSES, _formsTaskService.getSubmittedFormQuestionResponses( request, formResponse, listQuestions ) );
        request.setAttribute( ATTRIBUTE_ID_GROUP_TO_ITERATE, nIdGroupToIterate );
    }

    /**
     * Gets the id of the group to be given one extra empty iteration on this render.
     *
     * @param request
     *            the HTTP request
     * @return the id of the FormDisplay (group) to iterate, or 0 if none
     */
    public int getIdGroupToIterate( HttpServletRequest request )
    {
        Object idGroupToIterate = request.getAttribute( ATTRIBUTE_ID_GROUP_TO_ITERATE );
        return idGroupToIterate instanceof Integer ? (Integer) idGroupToIterate : 0;
    }

    /**
     * Removes the given iteration from the given group.
     *
     * @param request the HTTP request
     * @param response the specific response object (ResubmitFormResponse, CompleteFormResponse...)
     * @param idHistory the id of the resource history the FormResponse is attached to
     * @param strGroupAndIterationToRemove the "<idGroup>_<index>" identifying the group iteration to remove
     */
    public void doRemoveIterationResponse( HttpServletRequest request, R response, int idHistory, String strGroupAndIterationToRemove )
    {
        final FormResponse formResponse = _formsTaskService.getFormResponseFromIdHistory( idHistory );
        if ( formResponse == null )
        {
            return;
        }
        List<Question> listQuestions = getListQuestionToEditFromResponse( formResponse, response );
        listQuestions = _formsTaskService.expandWithSubmittedIterations( request, listQuestions );
        List<FormQuestionResponse> listSubmitted = _formsTaskService.getSubmittedFormQuestionResponses( request, formResponse, listQuestions );

        final String [ ] arrayInfo = StringUtils.split( strGroupAndIterationToRemove, FormsConstants.SEPARATOR_UNDERSCORE );
        if ( arrayInfo != null && arrayInfo.length == 2 && StringUtils.isNumeric( arrayInfo [1] ) )
        {
            int nRemovedIteration = Integer.parseInt( arrayInfo [1] );
            List<FormQuestionResponse> listReindexed = new ArrayList<>( );
            for ( FormQuestionResponse fqr : listSubmitted )
            {
                int nIteration = fqr.getQuestion( ).getIterationNumber( );
                if ( nIteration == nRemovedIteration )
                {
                    // don't index the value of the removed iteration
                    continue;
                }
                if ( nIteration > nRemovedIteration )
                {
                    fqr.getQuestion( ).setIterationNumber( nIteration - 1 );
                }
                listReindexed.add( fqr );
            }
            listSubmitted = listReindexed;
        }

        request.setAttribute( ATTRIBUTE_SUBMITTED_RESPONSES, listSubmitted );
    }
}