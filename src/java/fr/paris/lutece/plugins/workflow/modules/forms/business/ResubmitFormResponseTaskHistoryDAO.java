/*
 * Copyright (c) 2002-2021, City of Paris
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

public class ResubmitFormResponseTaskHistoryDAO implements IResubmitFormResponseTaskHistoryDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT id_history, id_task, id_question, iteration_number, previous_value, new_value FROM workflow_task_resubmit_response_history ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_resubmit_response_history "
            + "(id_history, id_task, id_question, iteration_number, previous_value, new_value) VALUES (?,?,?,?,?,?)";
    private static final String SQL_FILTER_IDHISTORY_IDTASK = SQL_QUERY_SELECT + "WHERE id_history = ? AND id_task = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_resubmit_response_history WHERE id_history = ? AND id_task = ? ";

    @Override
    public synchronized void insert( ResubmitFormResponseTaskHistory resubmitFormResponseTaskHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, resubmitFormResponseTaskHistory.getIdHistory( ) );
            daoUtil.setInt( ++nPos, resubmitFormResponseTaskHistory.getIdTask( ) );
            daoUtil.setInt( ++nPos, resubmitFormResponseTaskHistory.getQuestion( ).getId( ) );
            daoUtil.setInt( ++nPos, resubmitFormResponseTaskHistory.getQuestion( ).getIterationNumber( ) );
            daoUtil.setString( ++nPos, resubmitFormResponseTaskHistory.getPreviousValue( ) );
            daoUtil.setString( ++nPos, resubmitFormResponseTaskHistory.getNewValue( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<ResubmitFormResponseTaskHistory> selectEditFormResponseHistoryByIdHistoryAndIdTask( int nIdHistory, int nIdTask )
    {

        List<ResubmitFormResponseTaskHistory> listResult = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_FILTER_IDHISTORY_IDTASK, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.setInt( 2, nIdTask );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listResult.add( getResubmitFormResponseTaskHistoryValues( daoUtil ) );
            }
        }
        return listResult;
    }

    /**
     * set a ResubmitFormResponseTaskHistory with database value
     * 
     * @param daoUtil
     * @return a ResubmitFormResponseTaskHistory setted
     */
    private ResubmitFormResponseTaskHistory getResubmitFormResponseTaskHistoryValues( DAOUtil daoUtil )
    {
        ResubmitFormResponseTaskHistory resubmitFormResponseTaskHistory = new ResubmitFormResponseTaskHistory( );

        int nIndex = 0;
        resubmitFormResponseTaskHistory.setIdHistory( daoUtil.getInt( ++nIndex ) );
        resubmitFormResponseTaskHistory.setIdTask( daoUtil.getInt( ++nIndex ) );
        Question question = new Question( );
        question.setId( daoUtil.getInt( ++nIndex ) );
        question.setIterationNumber( daoUtil.getInt( ++nIndex ) );
        resubmitFormResponseTaskHistory.setQuestion( question );
        resubmitFormResponseTaskHistory.setPreviousValue( daoUtil.getString( ++nIndex ) );
        resubmitFormResponseTaskHistory.setNewValue( daoUtil.getString( ++nIndex ) );

        return resubmitFormResponseTaskHistory;
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
