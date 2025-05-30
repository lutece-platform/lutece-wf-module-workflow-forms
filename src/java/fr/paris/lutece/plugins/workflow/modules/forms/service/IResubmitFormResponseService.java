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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseValue;
import fr.paris.lutece.portal.service.message.SiteMessageException;

/**
 * Service for ResubmitFormResponseTask
 */
public interface IResubmitFormResponseService
{

    /**
     * Find an ResubmitFormResponse
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     * @return a ResubmitFormResponse
     */
    ResubmitFormResponse find( int nIdHistory, int nIdTask );

    /**
     * Get the list of entries for information
     * 
     * @param nIdHistory
     *            the id edit record
     * @return a list of entries
     */
    List<Entry> getInformationListEntries( int nIdHistory );

    /**
     * Get the list of entries for the form
     * 
     * @param nIdRecord
     *            the id record
     * @param strResourceType
     *            the yype of resources
     * @return a list of entries
     */
    List<Entry> getFormListEntries( int nIdRecord, String strResourceType );

    /**
     * Find the list of question which can be used in the correct form task.
     * 
     * @param formResponse
     * @return list of question
     */
    List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse );

    /**
     * Remove a ResubmitFormResponse
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     */
    void removeByIdHistory( int nIdHistory, int nIdTask );

    /**
     * Remove a ResubmitFormResponse by id task
     * 
     * @param nIdTask
     *            the id task
     */
    void removeByIdTask( int nIdTask );

    /**
     * Create a ResubmitFormResponse
     * 
     * @param resubmitFormResponse
     *            to create
     */
    void create( ResubmitFormResponse resubmitFormResponse );

    /**
     * Update a ResubmitFormResponse
     * 
     * @param resubmitFormResponse
     *            to update
     */
    void update( ResubmitFormResponse resubmitFormResponse );

    /**
     * Check if the response has the same state before executing the action
     * 
     * @param resubmitFormResponse
     *            the edit record
     * @param locale
     *            the locale
     * @return true if the record has a valid state, false otherwise
     */
    boolean isRecordStateValid( ResubmitFormResponse resubmitFormResponse, Locale locale );

    /**
     * Get the list of questions to edit
     * 
     * @param formResponse
     *            the formResponse
     * @param listEditRecordValues
     *            the list of edit record values
     * @return a list of entries
     */
    List<Question> getListQuestionToEdit( FormResponse formResponse, List<ResubmitFormResponseValue> listEditRecordValues );

    /**
     * Do edit the response
     * 
     * @param request
     *            the HTTP request
     * @param resubmitFormResponse
     *            the response
     * @return true if the user the record must be updated, false otherwise
     * @throws SiteMessageException
     *             site message if there is a problem
     */
    boolean doEditResponseData( HttpServletRequest request, ResubmitFormResponse resubmitFormResponse, int idTask, int idHistory ) throws SiteMessageException;

    /**
     * Do change the Response
     * 
     * @param resubmitFormResponse
     *            the response
     * @param locale
     *            the locale
     */
    void doChangeResponseState( ResubmitFormResponse resubmitFormResponse, Locale locale );

    /**
     * Do change the Response to complete
     * 
     * @param resubmitFormResponse
     *            the Response
     */
    void doCompleteResponse( ResubmitFormResponse resubmitFormResponse );

    /**
     * Get the List of FormQuestionResponse containing the new Responses that the user is trying to submit
     * 
     * @return A List of FormQuestionResponse
     */
    List<FormQuestionResponse> getSubmittedFormResponseList( );
}
