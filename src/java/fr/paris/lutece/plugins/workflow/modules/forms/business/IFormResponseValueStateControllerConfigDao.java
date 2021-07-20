package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IFormResponseValueStateControllerConfigDao
{
    /**
     * Insert new record
     * 
     * @param config
     *            the CompleteFormResponse Object
     * @param plugin
     *            the plugin
     */
    void insert( FormResponseValueStateControllerConfig config, Plugin plugin );

    /**
     * update record
     * 
     * @param config
     *            the FormResponseValueStateControllerConfig Object
     * @param plugin
     *            the plugin
     */
    void store( FormResponseValueStateControllerConfig config, Plugin plugin );
    
    /**
     * Load a list of FormResponseValueStateControllerConfig by id task
     * 
     * @param nIdTask
     *            the id task
     * @param plugin
     *            the plugin
     * @return a FormResponseValueStateControllerConfig
     */
    FormResponseValueStateControllerConfig loadByIdTask( int nIdTask, Plugin plugin );
    
    /**
     * Remove FormResponseValueStateControllerConfig by id task
     * 
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     */
    void deleteByIdTask( int nIdTask, Plugin plugin );
}
