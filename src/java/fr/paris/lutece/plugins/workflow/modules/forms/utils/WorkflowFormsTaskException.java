package fr.paris.lutece.plugins.workflow.modules.forms.utils;

/**
 * Exception for errors while processing Resubmit & Complete tasks. 
 *
 */
public class WorkflowFormsTaskException extends Exception
{

    private static final long serialVersionUID = 1278211791642369632L;

    public WorkflowFormsTaskException( String message )
    {
        super( message );
    }
}
