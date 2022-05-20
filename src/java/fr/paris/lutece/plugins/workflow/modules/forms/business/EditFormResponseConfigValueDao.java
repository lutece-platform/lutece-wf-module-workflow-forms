/*
 * Copyright (c) 2002-2022, City of Paris
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implements {@link IEditFormResponseConfigValueDao}
 */
public class EditFormResponseConfigValueDao implements IEditFormResponseConfigValueDao
{
    public static final String BEAN_NAME = "worklow-forms.editFormResponseConfigValueDao";

    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_config_value,id_config,id_form,id_step,id_question,response,code FROM workflow_task_editformresponse_config_value ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_config_value = ?";
    private static final String SQL_QUERY_SELECT_BY_CONFIG = SQL_QUERY_SELECT_ALL + " WHERE id_config = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_editformresponse_config_value ( id_config,id_form,id_step,id_question,response,code ) VALUES ( ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_editformresponse_config_value WHERE id_config_value = ?";
    private static final String SQL_QUERY_DELETE_BY_CONFIG = "DELETE FROM workflow_task_editformresponse_config_value WHERE id_config = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_editformresponse_config_value SET id_config_value=?, id_config=?, id_form = ?, id_step=?, id_question=?, response=?, code=? WHERE id_config_value = ?";

    @Override
    public void insert( EditFormResponseConfigValue configValue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, configValue.getIdConfig( ) );
            if ( configValue.getForm( ) != null )
            {
                daoUtil.setInt( ++nIndex, configValue.getForm( ).getId( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.INTEGER );
            }
            if ( configValue.getStep( ) != null )
            {
                daoUtil.setInt( ++nIndex, configValue.getStep( ).getId( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.INTEGER );
            }
            if ( configValue.getQuestion( ) != null )
            {
                daoUtil.setInt( ++nIndex, configValue.getQuestion( ).getId( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.INTEGER );
            }
            if ( configValue.getResponse( ) != null )
            {
                daoUtil.setString( ++nIndex, configValue.getResponse( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.VARCHAR );
            }
            daoUtil.setString( ++nIndex, configValue.getCode( ) );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                configValue.setIdConfigValue( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    @Override
    public void store( EditFormResponseConfigValue configValue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, configValue.getIdConfigValue( ) );
            daoUtil.setInt( ++nIndex, configValue.getIdConfig( ) );
            if ( configValue.getForm( ) != null )
            {
                daoUtil.setInt( ++nIndex, configValue.getForm( ).getId( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.INTEGER );
            }
            if ( configValue.getStep( ) != null )
            {
                daoUtil.setInt( ++nIndex, configValue.getStep( ).getId( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.INTEGER );
            }
            if ( configValue.getQuestion( ) != null )
            {
                daoUtil.setInt( ++nIndex, configValue.getQuestion( ).getId( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.INTEGER );
            }
            if ( configValue.getResponse( ) != null )
            {
                daoUtil.setString( ++nIndex, configValue.getResponse( ) );
            }
            else
            {
                daoUtil.setNull( ++nIndex, Types.VARCHAR );
            }
            daoUtil.setString( ++nIndex, configValue.getCode( ) );
            daoUtil.setInt( ++nIndex, configValue.getIdConfigValue( ) );
            daoUtil.executeUpdate( );
        }

    }

    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, nKey );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<EditFormResponseConfigValue> selectByConfigId( int nConfigId, Plugin plugin )
    {
        List<EditFormResponseConfigValue> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CONFIG, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, nConfigId );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                EditFormResponseConfigValue config = dataToObject( daoUtil );
                list.add( config );
            }
        }
        return list;
    }

    @Override
    public EditFormResponseConfigValue load( int nKey, Plugin plugin )
    {
        EditFormResponseConfigValue config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                config = dataToObject( daoUtil );
            }
        }
        return config;
    }

    @Override
    public void deleteByConfigId( int nConfigId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_CONFIG, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, nConfigId );
            daoUtil.executeUpdate( );
        }

    }

    private EditFormResponseConfigValue dataToObject( DAOUtil daoUtil )
    {
        EditFormResponseConfigValue config = new EditFormResponseConfigValue( );
        int nIndex = 0;
        config.setIdConfigValue( daoUtil.getInt( ++nIndex ) );
        config.setIdConfig( daoUtil.getInt( ++nIndex ) );

        int idForm = daoUtil.getInt( ++nIndex );
        if ( idForm > 0 )
        {
            config.setForm( FormHome.findByPrimaryKey( idForm ) );
        }
        int idStep = daoUtil.getInt( ++nIndex );
        if ( idStep > 0 )
        {
            config.setStep( StepHome.findByPrimaryKey( idStep ) );
        }
        int idQuestion = daoUtil.getInt( ++nIndex );
        if ( idQuestion > 0 )
        {
            config.setQuestion( QuestionHome.findByPrimaryKey( idQuestion ) );
        }
        config.setResponse( daoUtil.getString( ++nIndex ) );
        config.setCode( daoUtil.getString( ++nIndex ) );
        return config;
    }
}
