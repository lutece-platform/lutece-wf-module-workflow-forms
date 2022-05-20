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
package fr.paris.lutece.plugins.workflow.modules.forms.service.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.service.taskinfo.ITaskInfoProvider;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import net.sf.json.JSONObject;

/**
 * This class represents a NotifyGru marker provider for the Resubmit Form task
 *
 */
public class CompleteFormResponseMarkerProvider implements IMarkerProvider
{
    private static final String ID = "workflow-forms.completeFormResponseMarkerProvider";

    // Services
    @Inject
    private ITaskService _taskService;

    @Inject
    @Named( value = "workflow-forms.completeFormResponseTaskInfoProvider" )
    private ITaskInfoProvider _completeFormResponseTaskInfoProvider;

    // Messages
    private static final String MESSAGE_TITLE = "module.workflow.forms.marker.provider.complete_form.title";
    private static final String MESSAGE_MARKER_URL_DESCRIPTION = "module.workflow.forms.marker.provider.complete_form.url.description";
    private static final String MESSAGE_MARKER_MSG_DESCRIPTION = "module.workflow.forms.marker.provider.complete_form.msg.description";
    private static final String MESSAGE_MARKER_ENTRIES_DESCRIPTION = "module.workflow.forms.marker.provider.complete_form.entries.description";

    @Override
    public String getId( )
    {
        return ID;
    }

    @Override
    public String getTitleI18nKey( )
    {
        return MESSAGE_TITLE;
    }

    @Override
    public Collection<InfoMarker> provideMarkerDescriptions( )
    {
        List<InfoMarker> listMarkers = new ArrayList<>( );

        InfoMarker notifyGruMarkerUrl = new InfoMarker( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_URL );
        notifyGruMarkerUrl.setDescription( MESSAGE_MARKER_URL_DESCRIPTION );
        listMarkers.add( notifyGruMarkerUrl );

        InfoMarker notifyGruMarkerMsg = new InfoMarker( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_MESSAGE );
        notifyGruMarkerMsg.setDescription( MESSAGE_MARKER_MSG_DESCRIPTION );
        listMarkers.add( notifyGruMarkerMsg );

        InfoMarker notifyGruMarkerEntries = new InfoMarker( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_ENTRIES );
        notifyGruMarkerEntries.setDescription( MESSAGE_MARKER_ENTRIES_DESCRIPTION );
        listMarkers.add( notifyGruMarkerEntries );

        return listMarkers;
    }

    @Override
    public Collection<InfoMarker> provideMarkerValues( ResourceHistory resourceHistory, ITask task, HttpServletRequest request )
    {
        List<InfoMarker> listMarkers = new ArrayList<>( );

        Locale locale = Locale.getDefault( );
        if ( request != null )
        {
            locale = request.getLocale( );
        }

        for ( ITask taskOther : _taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), locale ) )
        {
            if ( taskOther.getTaskType( ).getKey( ).equals( _completeFormResponseTaskInfoProvider.getTaskType( ).getKey( ) ) )
            {
                String strJsonInfos = _completeFormResponseTaskInfoProvider.getTaskResourceInfo( resourceHistory.getId( ), taskOther.getId( ), request );

                JSONObject jsonInfos = JSONObject.fromObject( strJsonInfos );
                String strUrl = jsonInfos.getString( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_URL );
                String strMsg = jsonInfos.getString( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_MESSAGE );
                String strEntries = jsonInfos.getString( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_ENTRIES );

                InfoMarker notifyGruMarkerUrl = new InfoMarker( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_URL );
                notifyGruMarkerUrl.setValue( strUrl );
                listMarkers.add( notifyGruMarkerUrl );

                InfoMarker notifyGruMarkerMsg = new InfoMarker( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_MESSAGE );
                notifyGruMarkerMsg.setValue( strMsg );
                listMarkers.add( notifyGruMarkerMsg );

                InfoMarker notifyGruMarkerEntries = new InfoMarker( CompleteFormResponseTaskInfoProvider.MARK_COMPLETE_FORM_ENTRIES );
                notifyGruMarkerEntries.setValue( strEntries );
                listMarkers.add( notifyGruMarkerEntries );

                break;
            }
        }

        return listMarkers;
    }
}
