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

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class FormResponseValueStateControllerConfigDao implements IFormResponseValueStateControllerConfigDao
{
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id, id_task, id_form, id_step, id_question, response_value,is_multiform,code FROM workflow_state_controller_form_response_value ";
    private static final String SQL_QUERY_SELECT_BY_TASK = SQL_QUERY_SELECT_ALL + " WHERE id_task = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_state_controller_form_response_value ( id_task, id_form, id_step, id_question, response_value,is_multiform,code ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_state_controller_form_response_value SET id_task = ?, id_form = ?, id_step = ?, id_question = ?, response_value = ?, is_multiform = ?, code = ? WHERE id = ? ";
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
            daoUtil.setBoolean( ++index, config.isMultiform( ) );
            daoUtil.setString( ++index, config.getCode( ) );

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
            daoUtil.setBoolean( ++index, config.isMultiform( ) );
            daoUtil.setString( ++index, config.getCode( ) );

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
        config.setMultiform( daoUtil.getBoolean( ++index ) );
        config.setCode( daoUtil.getString( ++index ) );
        return config;
    }
}
