/*
 * Copyright (c) 2002-2025, City of Paris
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

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

public class ModifyFormResponseUpdateStatusTaskConfigDAO implements ITaskConfigDAO<ModifyFormResponseUpdateStatusTaskConfig>
{
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_task, status FROM workflow_task_update_status ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_update_status ( id_task,status ) VALUES ( ?,? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_update_status WHERE id_task = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_update_status SET id_task = ?, status = ? WHERE id_task = ?";

    @Override
    public void insert( ModifyFormResponseUpdateStatusTaskConfig configValue )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, configValue.getIdTask( ) );
            daoUtil.setBoolean( nIndex++, configValue.isPublished( ) );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void store( ModifyFormResponseUpdateStatusTaskConfig configValue )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, configValue.getIdTask( ) );
            daoUtil.setBoolean( nIndex++, configValue.isPublished( ) );
            daoUtil.setInt( nIndex++, configValue.getIdTask( ) );
            AppLogService.debug( configValue.isPublished( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public ModifyFormResponseUpdateStatusTaskConfig load( int nIdTask )
    {
        ModifyFormResponseUpdateStatusTaskConfig configValue = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nIndex = 0;
                configValue = new ModifyFormResponseUpdateStatusTaskConfig( );
                configValue.setIdTask( daoUtil.getInt( ++nIndex ) );
                configValue.setPublished( daoUtil.getBoolean( ++nIndex ) );
            }
        }
        return configValue;
    }

    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

}
