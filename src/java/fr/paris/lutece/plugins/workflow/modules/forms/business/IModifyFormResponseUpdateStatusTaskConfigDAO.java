package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IModifyFormResponseUpdateStatusTaskConfigDAO 
{
	/**
     * Insert a new record in the table.
     * 
     * @param configValue
     * @param plugin
     */
    void insert( ModifyFormResponseUpdateStatusTaskConfig configValue, Plugin plugin );

    /**
     * Update the record in the table.
     * 
     * @param configValue
     * @param plugin
     */
    void store( ModifyFormResponseUpdateStatusTaskConfig configValue, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     * @param plugin
     */
    void delete( int nKey, Plugin plugin );

    /**
     * Load the data from the table.
     * 
     * @param nKey
     * @param plugin
     * @return
     */
    ModifyFormResponseUpdateStatusTaskConfig load( int nKey, Plugin plugin );
}
