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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.service.ICompleteFormResponseService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;

public class CompleteFormResponseTask extends Task
{

    private static final String PARAMETER_IDS_ENTRY = "ids_entry";

    @Inject
    @Named( "workflow-forms.taskCompleteResponseConfigService" )
    private ITaskConfigService _taskCompleteResponseConfigService;

    @Inject
    private ICompleteFormResponseService _completeFormResponseService;

    @Inject
    private IStateService _stateService;

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        boolean bCreate = false;
        List<CompleteFormResponseValue> listCompleteFormResponseValues = new ArrayList<>( );

        CompleteFormResponse completeFormResponse = _completeFormResponseService.find( nIdResourceHistory, getId( ) );

        if ( completeFormResponse == null )
        {
            completeFormResponse = new CompleteFormResponse( );
            completeFormResponse.setIdHistory( nIdResourceHistory );
            completeFormResponse.setIdTask( getId( ) );
            bCreate = true;
        }

        if ( request != null )
        {
            String [ ] listIdsEntry = request.getParameterValues( PARAMETER_IDS_ENTRY );

            if ( listIdsEntry != null )
            {
                for ( String strIdEntry : listIdsEntry )
                {
                    if ( StringUtils.isNotBlank( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
                    {
                        int nIdEntry = Integer.parseInt( strIdEntry );
                        CompleteFormResponseValue editRecordValue = new CompleteFormResponseValue( );
                        editRecordValue.setIdEntry( nIdEntry );

                        listCompleteFormResponseValues.add( editRecordValue );
                    }
                }
            }
        }

        CompleteFormResponseTaskConfig config = _taskCompleteResponseConfigService.findByPrimaryKey( getId( ) );
        String strMessage = config.getDefaultMessage( );

        completeFormResponse.setMessage( StringUtils.isNotBlank( strMessage ) ? strMessage : StringUtils.EMPTY );
        completeFormResponse.setListCompleteReponseValues( listCompleteFormResponseValues );
        completeFormResponse.setIsComplete( false );

        if ( bCreate )
        {
            _completeFormResponseService.create( completeFormResponse );
        }
        else
        {
            _completeFormResponseService.update( completeFormResponse );
        }
    }

    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        _completeFormResponseService.removeByIdHistory( nIdHistory, getId( ) );
    }

    @Override
    public void doRemoveConfig( )
    {
        _completeFormResponseService.removeByIdTask( getId( ) );
        _taskCompleteResponseConfigService.remove( getId( ) );
    }

    @Override
    public String getTitle( Locale locale )
    {
        String strTitle = StringUtils.EMPTY;
        CompleteFormResponseTaskConfig config = _taskCompleteResponseConfigService.findByPrimaryKey( getId( ) );

        if ( config != null && config.getIdStateAfterEdition( ) != -1 )
        {
            State state = _stateService.findByPrimaryKey( config.getIdStateAfterEdition( ) );

            if ( state != null )
            {
                strTitle = state.getName( );
            }
        }

        return strTitle;
    }

    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        return null;
    }

    @Override
    public void init( )
    {
        // Nothing to init
    }
}
