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

import java.util.Collection;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.util.ReferenceList;

/**
 * This interface represents a service for the tasks of the plugin-forms
 *
 */
public interface IFormsTaskService
{
    /**
     * Finds the resource history with the specified id
     * 
     * @param nIdResourceHistory
     *            the id of the resource history to find
     * @return the resource history
     */
    ResourceHistory findResourceHistory( int nIdResourceHistory );

    /**
     * Finds a form response from the specified resource history
     * 
     * @param resourceHistory
     *            the resource history
     * @return the form response
     */
    default FormResponse findFormResponseFrom( ResourceHistory resourceHistory )
    {
        return findFormResponseFrom( resourceHistory.getIdResource( ), resourceHistory.getResourceType( ) );
    }

    /**
     * Finds a form response from the specified resource id and resource type
     * 
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @return the form response
     */
    FormResponse findFormResponseFrom( int nIdResource, String strResourceType );

    /**
     * Finds a form response from the specified resource id and resource type
     * 
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @return the form response
     */
    FormResponse findFormResponseWithoutSteps( int nIdResource, String strResourceType );

    List<String> buildFormStepDisplayTreeList( HttpServletRequest request, List<Step> listStep, List<Question> listQuestionToDisplay, FormResponse formResponse,
            DisplayType displayType );

    /**
     * Get a List of Strings containing the models and values displayed to the user, for each existing Step
     * 
     * @param request
     *            the HTTP request
     * @param listStep
     *            the List of Steps being processed
     * @param listQuestionToDisplay
     *            the List of Question that should be resubmitted by the user
     * @param listFormQuestionResponse
     *            the List of new FormQuestionResponse being submitted by the user
     * @param formResponse
     *            the FormResponse being processed
     * @param displayType
     *            the DisplayType used in the template
     * @return a List of Strings containing the complete template 
     */
    List<String> buildFormStepDisplayTree( HttpServletRequest request, List<Step> listStep, List<Question> listQuestionToDisplay,
            List<FormQuestionResponse> listFormQuestionResponse, FormResponse formResponse, DisplayType displayType );

    /**
     * Finds the responses that have changed
     * 
     * @param listEditableResponse
     *            the list of editable responses
     * @return the list of responses that have changed
     */
    List<EditableResponse> findChangedResponses( List<EditableResponse> listEditableResponse );

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
    List<EditableResponse> createEditableResponses( FormResponse formResponse, List<Question> listQuestion, HttpServletRequest request );

    /**
     * Finds the responses of the specified question from the specified form response
     * 
     * @param formResponse
     *            the form response
     * @param question
     *            the question
     * @return the list of responses
     */
    List<FormQuestionResponse> findResponses( FormResponse formResponse, Question question );

    /**
     * Get the list of states
     * 
     * @param nIdAction
     *            the id action
     * @return a ReferenceList
     */
    ReferenceList getListStates( int nIdAction );

    /**
     * Set the site message
     * 
     * @param request
     *            the HTTP request
     * @param strMessage
     *            the message
     * @param nTypeMessage
     *            the message type
     * @param strUrlReturn
     *            the url return
     * @throws SiteMessageException
     *             the site message
     */
    void setSiteMessage( HttpServletRequest request, String strMessage, int nTypeMessage, String strUrlReturn ) throws SiteMessageException;

    /**
     * Get the FormResponse from a given id history
     * 
     * @param nIdHistory
     *            the id history
     * @return the FormResponse
     */
    FormResponse getFormResponseFromIdHistory( int nIdHistory );

    /**
     * Create a string with previous or new value to set in history
     * 
     * @param responseForm
     * @param value
     * @return a value ready to be inserted in history
     */
    String createPreviousNewValue( FormQuestionResponse responseForm );

    /**
     * Get the List of responses being submitted
     * 
     * @param request
     *            the HTTP request
     * @param formResponse
     *            the FormReponse being submitted
     * @param listQuestions
     *            the List of Question associated with the responses being submitted
     * @return a List of FormQuestionResponse containing the new Responses' values
     */
    List<FormQuestionResponse> getSubmittedFormQuestionResponses( HttpServletRequest request, FormResponse formResponse, List<Question> listQuestions );

    /**
     * Check whether the values from the given FormQuestionResponse satisfy the Validators (regEx, file type, etc.) associated with them
     * 
     * @param formQuestionResponses
     *            the List of FormQuestionResponse to check
     * @return true if all the Responses are valid, returns false otherwise
     */
    boolean areFormQuestionResponsesValid( List<FormQuestionResponse> formQuestionResponses );
}
