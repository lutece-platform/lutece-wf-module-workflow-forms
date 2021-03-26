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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;

/**
 * This abstract class is represents a task for the plugin-forms
 *
 */
public abstract class AbstractFormsTask extends SimpleTask
{
    protected final IFormsTaskService _formsTaskService;

    /**
     * Constructor
     * 
     * @param formsTaskService
     *            the form task service
     */
    public AbstractFormsTask( IFormsTaskService formsTaskService )
    {
        super( );

        _formsTaskService = formsTaskService;
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

    /**
     * Processes the task for the specified form response
     * 
     * @param formResponse
     *            the form response
     * @param request
     *            the request
     * @param locale
     *            the locale
     */
    protected abstract void processTask( FormResponse formResponse, HttpServletRequest request, Locale locale );

    /**
     * Process the task to save the information and create history
     * 
     * @param nIdHistory
     *            idHistory
     */
    protected abstract void saveTaskInformation( int nIdHistory );

}
