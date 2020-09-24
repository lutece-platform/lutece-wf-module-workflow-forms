package fr.paris.lutece.plugins.workflow.modules.forms.service.archiver;

import java.util.List;

import javax.inject.Inject;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.AbstractArchiveProcessingService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.ICompleteFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskHistoryService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Service for archival of type delete of plugin-workflow.
 */
public class WorkflowFormsDeleteArchiveProcessingService extends AbstractArchiveProcessingService
{
    public static final String BEAN_NAME = "workflow-forms.workflowFormsDeleteArchiveProcessingService";
    
    private static final String TASK_TYPE_EDITFORMS = "editFormResponseTypeTask";
    private static final String TASK_TYPE_COMPLETEFORMS = "completeFormResponseTypeTask";
    private static final String TASK_TYPE_RESUBMITFORMS = "resubmitFormResponseTypeTask";
    
    @Inject
    private IResubmitFormResponseService _resubmitFormResponseService;
    
    @Inject
    private ICompleteFormResponseService _completeFormResponseService;
    
    @Inject
    private IEditFormResponseTaskHistoryService _editFormResponseTaskHistoryService;
    
    @Inject
    private FormService _formService;
    
    @Override
    public void archiveResource( ResourceWorkflow resourceWorkflow )
    {
        List<ResourceHistory> historyList = _resourceHistoryService.getAllHistoryByResource( resourceWorkflow.getIdResource( ), resourceWorkflow.getResourceType( ), resourceWorkflow.getWorkflow( ).getId( ) );
        
        archiveTaskEditForms( historyList );
        archiveTaskCompleteForms( historyList );
        archiveTaskResubmitForms( historyList );
        archiveFormResponse( resourceWorkflow );
    }

    private void archiveTaskEditForms( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_EDITFORMS );
            for ( ITask task : taskList )
            {
                _editFormResponseTaskHistoryService.removeAllByHistoryAndTask( history, task );
            }
        }
    }
    
    private void archiveTaskCompleteForms( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_COMPLETEFORMS );
            for ( ITask task : taskList )
            {
                _completeFormResponseService.removeByIdHistory( history.getId( ), task.getId( ) );
            }
        }
    }
    
    private void archiveTaskResubmitForms( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_RESUBMITFORMS );
            for ( ITask task : taskList )
            {
                _resubmitFormResponseService.removeByIdHistory( history.getId( ), task.getId( ) );
            }
        }
    }
    
    private void archiveFormResponse( ResourceWorkflow resourceWorkflow )
    {
        int formResponseId = resourceWorkflow.getIdResource( );
        FormResponse formResponse = FormResponseHome.loadById( formResponseId );
        
        for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponseId ) )
        {
            FormQuestionResponseHome.remove( formQuestionResponse );
        }

        FormResponseStepHome.removeByFormResponse( formResponseId );
        
        FormResponseHome.remove( formResponseId );
        
        _formService.fireFormResponseEventDelete( formResponse );
    }
}
