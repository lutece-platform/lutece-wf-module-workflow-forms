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
public class ResubmitFormResponseMarkerProvider implements IMarkerProvider 
{
	private static final String ID = "workflow-forms.resubmitFormResponseMarkerProvider";
	
	// Services
    @Inject
    private ITaskService _taskService;
    
    @Inject
    @Named(value="workflow-forms.resubmitFormResponseTaskInfoProvider")
    private ITaskInfoProvider _resubmitFormResponseTaskInfoProvider;
    
	// Messages
    private static final String MESSAGE_TITLE = "module.workflow.forms.marker.provider.resubmit_form.title";
    private static final String MESSAGE_MARKER_URL_DESCRIPTION = "module.workflow.forms.marker.provider.resubmit_form.url.description";
    private static final String MESSAGE_MARKER_MSG_DESCRIPTION = "module.workflow.forms.marker.provider.resubmit_form.msg.description";
    private static final String MESSAGE_MARKER_ENTRIES_DESCRIPTION = "module.workflow.forms.marker.provider.resubmit_form.entries.description";
	
    // Markers
    private static final String MARK_RESUBMIT_FORM_URL = "resubmit_form_url";
	private static final String MARK_RESUBMIT_FORM_MESSAGE = "resubmit_form_message";
	private static final String MARK_RESUBMIT_FORM_ENTRIES = "resubmit_form__entries";
    
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

        InfoMarker notifyGruMarkerUrl = new InfoMarker( MARK_RESUBMIT_FORM_URL );
        notifyGruMarkerUrl.setDescription( MESSAGE_MARKER_URL_DESCRIPTION );
        listMarkers.add( notifyGruMarkerUrl );

        InfoMarker notifyGruMarkerMsg = new InfoMarker( MARK_RESUBMIT_FORM_MESSAGE );
        notifyGruMarkerMsg.setDescription( MESSAGE_MARKER_MSG_DESCRIPTION );
        listMarkers.add( notifyGruMarkerMsg );

        InfoMarker notifyGruMarkerEntries = new InfoMarker( MARK_RESUBMIT_FORM_ENTRIES );
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
            if ( taskOther.getTaskType( ).getKey( ).equals( _resubmitFormResponseTaskInfoProvider.getTaskType( ).getKey( ) ) )
            {
                String strJsonInfos = _resubmitFormResponseTaskInfoProvider.getTaskResourceInfo( resourceHistory.getId( ), taskOther.getId( ), request ) ;

                JSONObject jsonInfos = JSONObject.fromObject( strJsonInfos );
                String strUrl = jsonInfos.getString( MARK_RESUBMIT_FORM_URL );
                String strMsg = jsonInfos.getString( MARK_RESUBMIT_FORM_MESSAGE );
                String strEntries = jsonInfos.getString( MARK_RESUBMIT_FORM_ENTRIES );


                InfoMarker notifyGruMarkerUrl = new InfoMarker( MARK_RESUBMIT_FORM_URL );
                notifyGruMarkerUrl.setValue( strUrl );
                listMarkers.add( notifyGruMarkerUrl );

                InfoMarker notifyGruMarkerMsg = new InfoMarker( MARK_RESUBMIT_FORM_MESSAGE );
                notifyGruMarkerMsg.setValue( strMsg );
                listMarkers.add( notifyGruMarkerMsg );

                InfoMarker notifyGruMarkerEntries = new InfoMarker( MARK_RESUBMIT_FORM_ENTRIES );
                notifyGruMarkerEntries.setValue( strEntries );
                listMarkers.add( notifyGruMarkerEntries );

                break;
            }
        }

        return listMarkers;
    }
}
