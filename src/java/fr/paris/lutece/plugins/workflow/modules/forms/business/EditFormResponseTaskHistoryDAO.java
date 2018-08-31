package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.util.sql.DAOUtil;

public class EditFormResponseTaskHistoryDAO implements IEditFormResponseTaskHistoryDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT id_history, id_task, id_question, previous_value, new_value FROM workflow_task_forms_editresponse_history ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_forms_editresponse_history "
            + "(id_history, id_task, id_question, previous_value, new_value)VALUES(?,?,?,?,?)";
    private static final String SQL_FILTER_IDHISTORY_IDTASK = SQL_QUERY_SELECT + "WHERE id_history = ? AND id_task = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( EditFormResponseTaskHistory editFormResponseTaskHistory )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) );
        try
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, editFormResponseTaskHistory.getIdHistory( ) );
            daoUtil.setInt( ++nPos, editFormResponseTaskHistory.getIdTask( ) );
            daoUtil.setInt( ++nPos, editFormResponseTaskHistory.getQuestion( ).getId( ) );
            daoUtil.setString( ++nPos, editFormResponseTaskHistory.getPreviousValue( ) );
            daoUtil.setString( ++nPos, editFormResponseTaskHistory.getNewValue( ) );

            daoUtil.executeUpdate( );
        }
        finally
        {
            daoUtil.close( );
        }
    }

    @Override
    public List<EditFormResponseTaskHistory> selectEditFormResponseHistoryByIdHistoryAndIdTask( int nIdHistory, int nIdTask )
    {

        List<EditFormResponseTaskHistory> listResult = new ArrayList<>( );
        DAOUtil daoUtil = new DAOUtil( SQL_FILTER_IDHISTORY_IDTASK, WorkflowUtils.getPlugin( ) );
        daoUtil.setInt( 1, nIdHistory );
        daoUtil.setInt( 2, nIdTask );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            listResult.add( getEditFormResponseTaskHistoryValues( daoUtil ) );
        }

        daoUtil.close( );

        return listResult;
    }

    /**
     * set a EditFormResponseTaskHistory with database value
     * 
     * @param daoUtil
     * @return a EditFormResponseTaskHistory setted
     */
    private EditFormResponseTaskHistory getEditFormResponseTaskHistoryValues( DAOUtil daoUtil )
    {
        EditFormResponseTaskHistory editFormResponseTaskHistory = new EditFormResponseTaskHistory( );

        int nIndex = 1;
        editFormResponseTaskHistory.setIdHistory( daoUtil.getInt( nIndex++ ) );
        editFormResponseTaskHistory.setIdTask( daoUtil.getInt( nIndex++ ) );
        Question question = new Question( );
        question.setId( daoUtil.getInt( nIndex++ ) );
        editFormResponseTaskHistory.setQuestion( question );
        editFormResponseTaskHistory.setPreviousValue( daoUtil.getString( nIndex++ ) );
        editFormResponseTaskHistory.setNewValue( daoUtil.getString( nIndex++ ) );

        return editFormResponseTaskHistory;
    }
}
