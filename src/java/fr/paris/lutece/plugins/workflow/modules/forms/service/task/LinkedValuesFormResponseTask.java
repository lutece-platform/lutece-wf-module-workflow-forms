/*
 * Copyright (c) 2002-2022, City of Paris
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfigValue;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * 
 * LinkedValuesFormResponseTask
 *
 */
public class LinkedValuesFormResponseTask extends SimpleTask
{

    @Inject
    @Named( LinkedValuesFormResponseConfigService.BEAN_NAME )
    private ITaskConfigService                   _linkedValuesFormResponseConfigService;

    @Inject
    @Named( LinkedValuesFormResponseTaskService.BEAN_NAME )
    private ILinkedValuesFormResponseTaskService _linkedValuesFormResponseTaskService;

    @Inject
    @Named( "workflow-forms.formsTaskService" )
    private IFormsTaskService                    _formsTaskService;


    private static final String                  MESSAGE_TASK_TITLE = "module.workflow.forms.task.linkedValuesFormResponse.config.title";

    @Override
    public void processTask( int nIdHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _formsTaskService.findResourceHistory( nIdHistory );

        if ( resourceHistory != null )
        {
            FormResponse formResponse = _formsTaskService.findFormResponseFrom( resourceHistory );
            LinkedValuesFormResponseConfig config = _linkedValuesFormResponseConfigService.findByPrimaryKey( getId( ) );

            for ( LinkedValuesFormResponseConfigValue configValue : config.getListConfigValues( ) )
            {
                _linkedValuesFormResponseTaskService.updateFormResponse( formResponse.getId( ), configValue );
            }
        }
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_TITLE, locale );
    }

    @Override
    public void doRemoveConfig( )
    {
        _linkedValuesFormResponseConfigService.remove( getId( ) );
    }

}
