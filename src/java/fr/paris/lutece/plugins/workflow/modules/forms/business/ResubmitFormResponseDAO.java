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
 * ResubmitFormResponseDAO
 *
 */
public class ResubmitFormResponseDAO implements IResubmitFormResponseDAO
{
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_history, id_task, message, is_complete, date_completed "
            + " FROM workflow_task_resubmit_response ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_history = ? AND id_task = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_TASK = SQL_QUERY_SELECT_ALL + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO workflow_task_resubmit_response ( id_history, id_task, message, is_complete, date_completed ) "
            + " VALUES ( ?,?,?,?,? ) ";
    private static final String SQL_QUERY_DELETE_BY_ID_HISTORY = " DELETE FROM workflow_task_resubmit_response WHERE id_history = ? AND id_task = ? ";
    private static final String SQL_QUERY_DELETE_BY_TASK = " DELETE FROM workflow_task_resubmit_response WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE workflow_task_resubmit_response SET message = ?, is_complete = ?, date_completed = ? WHERE id_history = ? AND id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ResubmitFormResponse resubmitFormResponse, Plugin plugin )
    {
        int nIndex = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( nIndex++, resubmitFormResponse.getIdHistory( ) );
            daoUtil.setInt( nIndex++, resubmitFormResponse.getIdTask( ) );
            daoUtil.setString( nIndex++, resubmitFormResponse.getMessage( ) );
            daoUtil.setBoolean( nIndex++, resubmitFormResponse.isComplete( ) );
            if ( resubmitFormResponse.getDateCompleted( ) != null )
            {
                daoUtil.setTimestamp( nIndex++, new Timestamp( resubmitFormResponse.getDateCompleted( ).getTime( ) ) );
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
    public void store( ResubmitFormResponse resubmitFormResponse, Plugin plugin )
    {
        int nIndex = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setString( nIndex++, resubmitFormResponse.getMessage( ) );
            daoUtil.setBoolean( nIndex++, resubmitFormResponse.isComplete( ) );
            if ( resubmitFormResponse.getDateCompleted( ) != null )
            {
                daoUtil.setTimestamp( nIndex++, new Timestamp( resubmitFormResponse.getDateCompleted( ).getTime( ) ) );
            }
            else
            {
                daoUtil.setNull( nIndex++, Types.TIMESTAMP );
            }

            daoUtil.setInt( nIndex++, resubmitFormResponse.getIdHistory( ) );
            daoUtil.setInt( nIndex++, resubmitFormResponse.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResubmitFormResponse load( int nIdHistory, int nIdTask, Plugin plugin )
    {
        ResubmitFormResponse resubmitFormResponse = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdHistory );
            daoUtil.setInt( nIndex++, nIdTask );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nIndex = 1;

                resubmitFormResponse = dataToObject( daoUtil );
            }

        }
        return resubmitFormResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResubmitFormResponse> loadByIdTask( int nIdTask, Plugin plugin )
    {
        List<ResubmitFormResponse> listResubmitFormResponses = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ResubmitFormResponse resubmitFormResponse = dataToObject( daoUtil );
                listResubmitFormResponses.add( resubmitFormResponse );
            }

        }
        return listResubmitFormResponses;
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

    private ResubmitFormResponse dataToObject( DAOUtil daoUtil )
    {
        int nIndex = 0;

        ResubmitFormResponse resubmitFormResponse = new ResubmitFormResponse( );
        resubmitFormResponse.setIdHistory( daoUtil.getInt( ++nIndex ) );
        resubmitFormResponse.setIdTask( daoUtil.getInt( ++nIndex ) );
        resubmitFormResponse.setMessage( daoUtil.getString( ++nIndex ) );
        resubmitFormResponse.setIsComplete( daoUtil.getBoolean( ++nIndex ) );
        if ( daoUtil.getObject( ++nIndex ) != null )
        {
            resubmitFormResponse.setDateCompleted( new Date( daoUtil.getTimestamp( nIndex ).getTime( ) ) );
        }
        return resubmitFormResponse;
    }
}
