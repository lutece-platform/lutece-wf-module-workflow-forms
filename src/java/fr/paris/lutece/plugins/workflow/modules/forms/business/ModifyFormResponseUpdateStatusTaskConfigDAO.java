package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.sql.Statement;
import java.sql.Timestamp;

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

public class ModifyFormResponseUpdateStatusTaskConfigDAO implements ITaskConfigDAO<ModifyFormResponseUpdateStatusTaskConfig>
{
	private static final String SQL_QUERY_SELECT_ALL = "SELECT id_task, status FROM workflow_task_update_status ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL  + " WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_update_status ( id_task,status ) VALUES ( ?,? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_update_status WHERE id_task = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_update_status SET id_task = ?, status = ? WHERE id_task = ?";


	@Override
	public void insert(ModifyFormResponseUpdateStatusTaskConfig configValue ) {
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, configValue.getIdTask( ) );
            daoUtil.setBoolean( nIndex++, configValue.isPublished( ) );
            daoUtil.executeUpdate( );
        }
	}

	@Override
	public void store(ModifyFormResponseUpdateStatusTaskConfig configValue) {
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, configValue.getIdTask( ) );
            daoUtil.setBoolean( nIndex++, configValue.isPublished( ) );
            daoUtil.setInt( nIndex++, configValue.getIdTask() );
            AppLogService.debug( configValue.isPublished( ) );
            
            daoUtil.executeUpdate( );
        }
	}

	@Override
	public ModifyFormResponseUpdateStatusTaskConfig load(int nIdTask) {
		ModifyFormResponseUpdateStatusTaskConfig configValue = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
            	int nIndex = 0;
        		configValue = new ModifyFormResponseUpdateStatusTaskConfig();
            	configValue.setIdTask( daoUtil.getInt( ++nIndex ) );
            	configValue.setPublished( daoUtil.getBoolean( ++nIndex ) );
            }
        }
        return configValue;
	}

	@Override
	public void delete(int nIdTask) {
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
	        daoUtil.setInt( 1, nIdTask );
	        daoUtil.executeUpdate( );
	        daoUtil.free( );
        }
	}

}
