package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormResponseValueStateControllerConfigHome
{
    private static IFormResponseValueStateControllerConfigDao _dao = SpringContextService.getBean( "worklow-forms.formResponseValueStateControllerConfigDao" );
    private static Plugin _plugin = WorkflowUtils.getPlugin( );
            
    /**
     * Private constructor
     */
    private FormResponseValueStateControllerConfigHome( )
    {
        super( );
    }
    
    /**
     * Insert a new record in the table.
     * 
     * @param config
     */
    public static void create( FormResponseValueStateControllerConfig config )
    {
        _dao.insert( config, _plugin );
    }

    /**
     * Update the record in the table.
     * 
     * @param config
     */
    public static void update( FormResponseValueStateControllerConfig config )
    {
        _dao.store( config, _plugin );
    }

    /**
     * Delete a record from the table
     * 
     * @param nIdTask
     */
    public static void removeByTask( int nIdTask )
    {
        _dao.deleteByIdTask( nIdTask, _plugin );
    } 
    
    /**
     * Load the data from the table.
     * 
     * @param nIdTask
     * @return
     */
    public static FormResponseValueStateControllerConfig findByTask( int nIdTask )
    {
        return _dao.loadByIdTask( nIdTask, _plugin );
    }
}
