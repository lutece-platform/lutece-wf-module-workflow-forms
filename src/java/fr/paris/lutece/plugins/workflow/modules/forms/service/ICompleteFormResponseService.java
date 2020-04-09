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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.portal.service.message.SiteMessageException;

public interface ICompleteFormResponseService
{

    /**
     * Find the list of question without response.
     * 
     * @param formResponse
     * @return list of question
     */
    List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse );

    /**
     * Find an CompleteFormResponse
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     * @return a CompleteFormResponse
     */
    CompleteFormResponse find( int nIdHistory, int nIdTask );

    /**
     * Get the list of entries for information
     * 
     * @param nIdHistory
     *            the id edit record
     * @return a list of entries
     */
    List<Entry> getInformationListEntries( int nIdHistory );

    /**
     * Create a CompleteFormResponse
     * 
     * @param completeFormResponse
     *            to create
     */
    void create( CompleteFormResponse completeFormResponse );

    /**
     * Update a CompleteFormResponse
     * 
     * @param completeFormResponse
     *            to update
     */
    void update( CompleteFormResponse completeFormResponse );

    /**
     * Remove a CompleteFormResponse
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     */
    void removeByIdHistory( int nIdHistory, int nIdTask );

    /**
     * Remove a CompleteFormResponse by id task
     * 
     * @param nIdTask
     *            the id task
     */
    void removeByIdTask( int nIdTask );

    /**
     * Check if the response has the same state before executing the action
     * 
     * @param completeFormResponse
     *            the edit record
     * @param locale
     *            the locale
     * @return true if the record has a valid state, false otherwise
     */
    boolean isRecordStateValid( CompleteFormResponse completeFormResponse, Locale locale );

    /**
     * Get the list of questions to edit
     * 
     * @param formResponse
     *            the form response
     * @param listEditRecordValues
     *            the list of edit record values
     * @return a list of entries
     */
    List<Question> getListQuestionToEdit( FormResponse formResponse, List<CompleteFormResponseValue> listEditRecordValues );

    /**
     * Do edit the response
     * 
     * @param request
     *            the HTTP request
     * @param completeFormResponse
     *            the response
     * @return true if the user the record must be updated, false otherwise
     * @throws SiteMessageException
     *             site message if there is a problem
     */
    boolean doEditResponseData( HttpServletRequest request, CompleteFormResponse completeFormResponse ) throws SiteMessageException;

    /**
     * Do change the Response
     * 
     * @param completeFormResponse
     *            the response
     * @param locale
     *            the locale
     */
    void doChangeResponseState( CompleteFormResponse completeFormResponse, Locale locale );

    /**
     * Do change the Response to complete
     * 
     * @param completeFormResponse
     *            the Response
     */
    void doCompleteResponse( CompleteFormResponse completeFormResponse );
}
