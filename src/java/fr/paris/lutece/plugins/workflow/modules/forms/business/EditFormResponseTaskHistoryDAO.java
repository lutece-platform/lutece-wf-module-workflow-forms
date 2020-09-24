/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.util.sql.DAOUtil;

public class EditFormResponseTaskHistoryDAO implements IEditFormResponseTaskHistoryDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT id_history, id_task, id_question, iteration_number, previous_value, new_value FROM workflow_task_forms_editresponse_history ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_forms_editresponse_history "
            + "(id_history, id_task, id_question, iteration_number, previous_value, new_value)VALUES(?,?,?,?,?,?)";
    private static final String SQL_FILTER_IDHISTORY_IDTASK = SQL_QUERY_SELECT + "WHERE id_history = ? AND id_task = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_forms_editresponse_history WHERE id_history = ? AND id_task = ? ";
    
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
            daoUtil.setInt( ++nPos, editFormResponseTaskHistory.getQuestion( ).getIterationNumber( ) );
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

        int nIndex = 0;
        editFormResponseTaskHistory.setIdHistory( daoUtil.getInt( ++nIndex ) );
        editFormResponseTaskHistory.setIdTask( daoUtil.getInt( ++nIndex ) );
        Question question = new Question( );
        question.setId( daoUtil.getInt( ++nIndex ) );
        question.setIterationNumber( daoUtil.getInt( ++nIndex ) );
        editFormResponseTaskHistory.setQuestion( question );
        editFormResponseTaskHistory.setPreviousValue( daoUtil.getString( ++nIndex ) );
        editFormResponseTaskHistory.setNewValue( daoUtil.getString( ++nIndex ) );

        return editFormResponseTaskHistory;
    }
    
    @Override
    public void deleteByIdHistoryAndTask( int nIdHistory, int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.setInt( 2, nIdTask );
            daoUtil.executeUpdate( );
        }
    }
}
