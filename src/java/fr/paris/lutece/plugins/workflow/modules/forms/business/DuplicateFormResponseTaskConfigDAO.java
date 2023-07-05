package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class DuplicateFormResponseTaskConfigDAO implements ITaskConfigDAO<DuplicateFormResponseTaskConfig> 
{
	// Constants 
    private static final String SQL_QUERY_SELECT = "SELECT id_task FROM workflow_task_duplicate_form_response WHERE id_task = ?"; 
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_duplicate_form_response ( id_task ) VALUES ( ? ) "; 
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_duplicate_form_response WHERE id_task = ? "; 
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_duplicate_form_response SET id_task = ? WHERE id_task = ?"; 

    /** 
     * {@inheritDoc} 
     */ 
    @Override 
    public void insert( DuplicateFormResponseTaskConfig config ) 
    { 
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin( ) ) ) 
        { 
            int nIndex = 0; 
            daoUtil.setInt( ++nIndex, config.getIdTask( ) ); 
 
            daoUtil.executeUpdate( ); 
            daoUtil.free( ); 
        } 
    } 
 
    /** 
     * {@inheritDoc} 
     */ 
    @Override 
    public void store( DuplicateFormResponseTaskConfig config ) 
    { 
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) ) 
        { 
            int nIndex = 0; 
            daoUtil.setInt( ++nIndex, config.getIdTask( ) ); 
 
            daoUtil.setInt( ++nIndex, config.getIdTask( ) ); 
 
            daoUtil.executeUpdate( ); 
            daoUtil.free( ); 
        } 
    } 
 
    /** 
     * {@inheritDoc} 
     */ 
    @Override 
    public DuplicateFormResponseTaskConfig load( int nIdTask ) 
    { 
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, WorkflowUtils.getPlugin( ) ) ) 
        { 
            daoUtil.setInt( 1, nIdTask ); 
            daoUtil.executeQuery( ); 
 
            DuplicateFormResponseTaskConfig taskConfig = null; 
 
            if ( daoUtil.next( ) ) 
            { 
            	taskConfig = new DuplicateFormResponseTaskConfig( ); 
                int nIndex = 0; 
 
                taskConfig.setIdTask( daoUtil.getInt( ++nIndex ) );
 
            } 
 
            daoUtil.free( ); 
 
            return taskConfig; 
        } 
    }
 
    /** 
     * {@inheritDoc} 
     */ 
    @Override 
    public void delete( int nIdTask ) 
    { 
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) ) 
        { 
            daoUtil.setInt( 1, nIdTask ); 
            daoUtil.executeUpdate( ); 
            daoUtil.free( ); 
        } 
    }
}