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
    @Named(value="workflow-forms.completeFormResponseTaskInfoProvider")
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
                String strJsonInfos = _completeFormResponseTaskInfoProvider.getTaskResourceInfo( resourceHistory.getId( ), taskOther.getId( ), request ) ;

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
