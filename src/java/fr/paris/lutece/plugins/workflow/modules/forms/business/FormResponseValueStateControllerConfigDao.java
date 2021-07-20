package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.sql.Statement;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class FormResponseValueStateControllerConfigDao implements IFormResponseValueStateControllerConfigDao
{
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id, id_task, id_form, id_step, id_question, response_value FROM workflow_state_controller_form_response_value ";
    private static final String SQL_QUERY_SELECT_BY_TASK = SQL_QUERY_SELECT_ALL + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_state_controller_form_response_value ( id_task, id_form, id_step, id_question, response_value ) VALUES ( ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_state_controller_form_response_value SET id_task = ?, id_form = ?, id_step = ?, id_question = ?, response_value = ? WHERE id = ? ";
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_state_controller_form_response_value WHERE id_task = ? ";
    
    @Override
    public void insert( FormResponseValueStateControllerConfig config, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int index = 0;
            daoUtil.setInt( ++index, config.getIdTask( ) );
            if ( config.getForm( ) != null )
            {
                daoUtil.setInt( ++index, config.getForm( ).getId( ) );
            }
            else
            {
                daoUtil.setInt( ++index, -1 );
            }
            if ( config.getStep( ) != null )
            {
                daoUtil.setInt( ++index, config.getStep( ).getId( ) );
            }
            else
            {
                daoUtil.setInt( ++index, -1 );
            }
            if ( config.getQuestion( ) != null )
            {
                daoUtil.setInt( ++index, config.getQuestion( ).getId( ) );
            }
            else
            {
                daoUtil.setInt( ++index, -1 );
            }
            daoUtil.setString( ++index, config.getValue( ) );
            
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void store( FormResponseValueStateControllerConfig config, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int index = 0;
            daoUtil.setInt( ++index, config.getIdTask( ) );
            if ( config.getForm( ) != null )
            {
                daoUtil.setInt( ++index, config.getForm( ).getId( ) );
            }
            else
            {
                daoUtil.setInt( ++index, -1 );
            }
            if ( config.getStep( ) != null )
            {
                daoUtil.setInt( ++index, config.getStep( ).getId( ) );
            }
            else
            {
                daoUtil.setInt( ++index, -1 );
            }
            if ( config.getQuestion( ) != null )
            {
                daoUtil.setInt( ++index, config.getQuestion( ).getId( ) );
            }
            else
            {
                daoUtil.setInt( ++index, -1 );
            }
            daoUtil.setString( ++index, config.getValue( ) );
            
            daoUtil.setInt( ++index, config.getId( ) );
            daoUtil.executeUpdate( );
        }

    }

    @Override
    public FormResponseValueStateControllerConfig loadByIdTask( int nIdTask, Plugin plugin )
    {
        FormResponseValueStateControllerConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );
            
            if ( daoUtil.next( ) )
            {
                config = dataToObject( daoUtil );
            }
        }
        return config;
    }

    @Override
    public void deleteByIdTask( int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    private FormResponseValueStateControllerConfig dataToObject( DAOUtil daoUtil )
    {
        int index = 0;
        FormResponseValueStateControllerConfig config = new FormResponseValueStateControllerConfig( );
        config.setId( daoUtil.getInt( ++index ) );
        config.setIdTask( daoUtil.getInt( ++index ) );
        config.setForm( FormHome.findByPrimaryKey( daoUtil.getInt( ++index ) ) );
        config.setStep( StepHome.findByPrimaryKey( daoUtil.getInt( ++index ) ) );
        config.setQuestion( QuestionHome.findByPrimaryKey( daoUtil.getInt( ++index ) ) );
        config.setValue( daoUtil.getString( ++index ) );
        return config;
    }
}
