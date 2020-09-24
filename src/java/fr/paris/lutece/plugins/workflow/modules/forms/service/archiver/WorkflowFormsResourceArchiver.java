package fr.paris.lutece.plugins.workflow.modules.forms.service.archiver;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflow.modules.archive.IResourceArchiver;
import fr.paris.lutece.plugins.workflow.modules.archive.service.IArchiveProcessingService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;

/**
 * {@link IResourceArchiver} for all data of module workflow-forms
 */
public class WorkflowFormsResourceArchiver implements IResourceArchiver
{
    public static final String BEAN_NAME = "workflow-forms.workflowFormsResourceArchiver";
    
    @Inject
    @Named( WorkflowFormsDeleteArchiveProcessingService.BEAN_NAME )
    private IArchiveProcessingService _deleteArchiveProcessingService;
    
    @Override
    public void archiveResource( ArchivalType archivalType, ResourceWorkflow resourceWorkflow )
    {
        if ( !FormResponse.RESOURCE_TYPE.equals( resourceWorkflow.getResourceType( ) ) )
        {
            return;
        }
        switch( archivalType )
        {
            case DELETE:
                _deleteArchiveProcessingService.archiveResource( resourceWorkflow );
                break;
            default:
                break;
        }
    }
    
    @Override
    public String getBeanName( )
    {
        return BEAN_NAME;
    }
}
