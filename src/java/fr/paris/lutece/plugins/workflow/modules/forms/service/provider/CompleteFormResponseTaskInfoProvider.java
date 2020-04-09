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
package fr.paris.lutece.plugins.workflow.modules.forms.service.provider;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.service.ICompleteFormResponseService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;

public class CompleteFormResponseTaskInfoProvider extends AbstractCompleteFormResponseTaskInfoProvider
{

    public static final String MARK_COMPLETE_FORM_URL = "complete_form_url";
    public static final String MARK_COMPLETE_FORM_MESSAGE = "complete_form_message";
    public static final String MARK_COMPLETE_FORM_ENTRIES = "complete_form__entries";

    @Inject
    private ICompleteFormResponseDAO _completeFormResponseDAO;

    @Inject
    private ICompleteFormResponseService _completeFormResponseService;

    @Override
    protected String getInfoEntries( int nIdHistory )
    {
        List<Entry> entries = _completeFormResponseService.getInformationListEntries( nIdHistory );
        if ( CollectionUtils.isEmpty( entries ) )
        {
            return StringUtils.EMPTY;
        }
        return entries.stream( ).map( Entry::getTitle ).collect( Collectors.joining( ", " ) );
    }

    @Override
    protected String getInfoMessage( int nIdHistory, int nIdTask )
    {
        CompleteFormResponse completeFormResponse = _completeFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );
        if ( completeFormResponse == null )
        {
            return StringUtils.EMPTY;
        }
        return completeFormResponse.getMessage( );
    }

    @Override
    protected String getMarkEntries( )
    {
        return MARK_COMPLETE_FORM_ENTRIES;
    }

    @Override
    protected String getMarkMsg( )
    {
        return MARK_COMPLETE_FORM_MESSAGE;
    }

    @Override
    protected String getMarkUrl( )
    {
        return MARK_COMPLETE_FORM_URL;
    }
}
