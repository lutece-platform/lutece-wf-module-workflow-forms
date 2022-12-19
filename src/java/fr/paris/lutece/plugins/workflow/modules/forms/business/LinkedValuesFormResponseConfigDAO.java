/*
 * Copyright (c) 2002-2022, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;

/**
 * This class provides Data Access methods for LinkedValuesFormResponseConfig objects
 */
public final class LinkedValuesFormResponseConfigDAO implements ITaskConfigDAO<LinkedValuesFormResponseConfig>
{
    // Constants
    private static final String SQL_QUERY_SELECT    = "SELECT id_config, id_task FROM workflow_task_linkedvaluesformresponse_config WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT    = "INSERT INTO workflow_task_linkedvaluesformresponse_config ( id_config, id_task ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE    = "DELETE FROM workflow_task_linkedvaluesformresponse_config WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE    = "UPDATE workflow_task_linkedvaluesformresponse_config SET id_config = ?, id_task = ? WHERE id_config = ?";

    @Override
    public void delete( int nIdTask )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void insert( LinkedValuesFormResponseConfig config )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, config.getIdConfig( ) );
            daoUtil.setInt( ++nIndex, config.getIdTask( ) );
    
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                config.setIdConfig( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public LinkedValuesFormResponseConfig load( int nIdTask )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );
    
            LinkedValuesFormResponseConfig linkedValuesFormResponseConfig = null;
    
            if ( daoUtil.next( ) )
            {
                linkedValuesFormResponseConfig = new LinkedValuesFormResponseConfig( );
    
                linkedValuesFormResponseConfig.setIdConfig( daoUtil.getInt( "id_config" ) );
                linkedValuesFormResponseConfig.setIdTask( daoUtil.getInt( "id_task" ) );
            }
    
            return linkedValuesFormResponseConfig;
        }
    }

    @Override
    public void store( LinkedValuesFormResponseConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, config.getIdConfig( ) );
            daoUtil.setInt( ++nIndex, config.getIdTask( ) );
            daoUtil.setInt( ++nIndex, config.getIdConfig( ) );
    
            daoUtil.executeUpdate( );
        }
    }

}
