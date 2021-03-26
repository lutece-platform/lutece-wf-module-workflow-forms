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
package fr.paris.lutece.plugins.workflow.modules.forms.service.provider;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;

public class ResubmitFormResponseTaskInfoProvider extends AbstractCompleteFormResponseTaskInfoProvider
{
    private static final String PAGE_NAME = "workflow-resubmit-form";
    public static final String MARK_RESUBMIT_FORM_URL = "resubmit_form_url";
    public static final String MARK_RESUBMIT_FORM_MESSAGE = "resubmit_form_message";
    public static final String MARK_RESUBMIT_FORM_ENTRIES = "resubmit_form__entries";

    @Inject
    private IResubmitFormResponseDAO _resubmitFormResponseDAO;

    @Inject
    private IResubmitFormResponseService _resubmitFormResponseService;

    @Override
    protected String getInfoEntries( int nIdHistory )
    {
        List<Entry> entries = _resubmitFormResponseService.getInformationListEntries( nIdHistory );
        if ( CollectionUtils.isEmpty( entries ) )
        {
            return StringUtils.EMPTY;
        }
        return entries.stream( ).map( Entry::getTitle ).collect( Collectors.joining( ", " ) );
    }

    @Override
    protected String getInfoMessage( int nIdHistory, int nIdTask )
    {
        ResubmitFormResponse resubmitFormResponse = _resubmitFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );
        if ( resubmitFormResponse == null )
        {
            return StringUtils.EMPTY;
        }
        return resubmitFormResponse.getMessage( );
    }

    @Override
    protected String getMarkEntries( )
    {
        return MARK_RESUBMIT_FORM_ENTRIES;
    }

    @Override
    protected String getMarkMsg( )
    {
        return MARK_RESUBMIT_FORM_MESSAGE;
    }

    @Override
    protected String getMarkUrl( )
    {
        return MARK_RESUBMIT_FORM_URL;
    }

    @Override
    public String getPageName( )
    {
        return PAGE_NAME;
    }
}
