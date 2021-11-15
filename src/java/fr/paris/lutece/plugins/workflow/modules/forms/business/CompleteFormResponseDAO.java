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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * CompleteFormResponseDAO
 *
 */
public class CompleteFormResponseDAO implements ICompleteFormResponseDAO
{
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_history, id_task, message, is_complete, date_completed "
            + " FROM workflow_task_complete_response ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_history = ? AND id_task = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_TASK = SQL_QUERY_SELECT_ALL + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_complete_response ( id_history, id_task, message, is_complete, date_completed ) "
            + " VALUES ( ?,?,?,?,? ) ";
    private static final String SQL_QUERY_DELETE_BY_ID_HISTORY = " DELETE FROM workflow_task_complete_response WHERE id_history = ? AND id_task = ? ";
    private static final String SQL_QUERY_DELETE_BY_TASK = " DELETE FROM workflow_task_complete_response WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE workflow_task_complete_response SET message = ?, is_complete = ?, date_completed = ? WHERE id_history = ? AND id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( CompleteFormResponse completeFormResponse, Plugin plugin )
    {
        int nIndex = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( nIndex++, completeFormResponse.getIdHistory( ) );
            daoUtil.setInt( nIndex++, completeFormResponse.getIdTask( ) );
            daoUtil.setString( nIndex++, completeFormResponse.getMessage( ) );
            daoUtil.setBoolean( nIndex++, completeFormResponse.isComplete( ) );
            if ( completeFormResponse.getDateCompleted( ) != null )
            {
                daoUtil.setTimestamp( nIndex++, new Timestamp( completeFormResponse.getDateCompleted( ).getTime( ) ) );
            }
            else
            {
                daoUtil.setNull( nIndex++, Types.TIMESTAMP );
            }

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( CompleteFormResponse completeFormResponse, Plugin plugin )
    {
        int nIndex = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setString( nIndex++, completeFormResponse.getMessage( ) );
            daoUtil.setBoolean( nIndex++, completeFormResponse.isComplete( ) );
            if ( completeFormResponse.getDateCompleted( ) != null )
            {
                daoUtil.setTimestamp( nIndex++, new Timestamp( completeFormResponse.getDateCompleted( ).getTime( ) ) );
            }
            else
            {
                daoUtil.setNull( nIndex++, Types.TIMESTAMP );
            }

            daoUtil.setInt( nIndex++, completeFormResponse.getIdHistory( ) );
            daoUtil.setInt( nIndex++, completeFormResponse.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompleteFormResponse load( int nIdHistory, int nIdTask, Plugin plugin )
    {
        CompleteFormResponse completeFormResponse = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdHistory );
            daoUtil.setInt( nIndex++, nIdTask );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                completeFormResponse = dataToObject( daoUtil );
            }

        }
        return completeFormResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompleteFormResponse> loadByIdTask( int nIdTask, Plugin plugin )
    {
        List<CompleteFormResponse> listCompleteFormResponses = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                CompleteFormResponse completeFormResponse = dataToObject( daoUtil );
                listCompleteFormResponses.add( completeFormResponse );
            }

        }
        return listCompleteFormResponses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_HISTORY, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdHistory );
            daoUtil.setInt( nIndex++, nIdTask );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdTask( int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    private CompleteFormResponse dataToObject( DAOUtil daoUtil )
    {
        int nIndex = 0;

        CompleteFormResponse completeFormResponse = new CompleteFormResponse( );
        completeFormResponse.setIdHistory( daoUtil.getInt( ++nIndex ) );
        completeFormResponse.setIdTask( daoUtil.getInt( ++nIndex ) );
        completeFormResponse.setMessage( daoUtil.getString( ++nIndex ) );
        completeFormResponse.setIsComplete( daoUtil.getBoolean( ++nIndex ) );
        if ( daoUtil.getObject( ++nIndex ) != null )
        {
            completeFormResponse.setDateCompleted( new Date( daoUtil.getTimestamp( nIndex ).getTime( ) ) );
        }
        return completeFormResponse;
    }
}
