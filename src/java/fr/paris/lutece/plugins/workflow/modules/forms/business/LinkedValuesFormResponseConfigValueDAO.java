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

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

/**
 * This class provides Data Access methods for LinkedValuesFormResponseConfigValue objects
 */
@ApplicationScoped
@Named( LinkedValuesFormResponseConfigValueDAO.BEAN_NAME )
public class LinkedValuesFormResponseConfigValueDAO implements ILinkedValuesFormResponseConfigValueDAO
{
    public static final String BEAN_NAME = "worklow-forms.linkedValuesFormResponseConfigValueDAO";
    
    // Constants
    private static final String SQL_QUERY_SELECT    = "SELECT id_config_value, id_config, id_form, id_question_source, question_source_value, id_question_target, question_target_value FROM workflow_task_linkedvaluesformresponse_config_value WHERE id_config_value = ?";
    private static final String SQL_QUERY_INSERT    = "INSERT INTO workflow_task_linkedvaluesformresponse_config_value ( id_config_value, id_config, id_form, id_question_source, question_source_value, id_question_target, question_target_value ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE    = "DELETE FROM workflow_task_linkedvaluesformresponse_config_value WHERE id_config_value = ? ";
    private static final String SQL_QUERY_UPDATE    = "UPDATE workflow_task_linkedvaluesformresponse_config_value SET id_config_value = ?, id_config = ?, id_form = ?, id_question_source = ?, question_source_value = ?, id_question_target = ?, question_target_value = ? WHERE id_config_value = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_config_value, id_config, id_form, id_question_source, question_source_value, id_question_target, question_target_value FROM workflow_task_linkedvaluesformresponse_config_value";
    private static final String SQL_QUERY_SELECT_BY_IDCONFIG = "SELECT id_config_value, id_config, id_form, id_question_source, question_source_value, id_question_target, question_target_value FROM workflow_task_linkedvaluesformresponse_config_value WHERE id_config = ?";
    private static final String SQL_QUERY_DELETE_BY_IDCONFIG    = "DELETE FROM workflow_task_linkedvaluesformresponse_config_value WHERE id_config = ? ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdConfigValue( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdConfig( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdForm( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdQuestionSource( ) );
            daoUtil.setString( ++nIndex, linkedValuesFormResponseConfigValue.getQuestionSourceValue( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdQuestionTarget( ) );
            daoUtil.setString( ++nIndex, linkedValuesFormResponseConfigValue.getQuestionTargetValue( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                linkedValuesFormResponseConfigValue.setIdConfigValue( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LinkedValuesFormResponseConfigValue load( int nId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );
    
            LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue = null;
    
            if ( daoUtil.next( ) )
            {
                int nIndex = 0;
                linkedValuesFormResponseConfigValue = new LinkedValuesFormResponseConfigValue( );
    
                linkedValuesFormResponseConfigValue.setIdConfigValue( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdConfig( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdForm( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdQuestionSource( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setQuestionSourceValue( daoUtil.getString( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdQuestionTarget( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setQuestionTargetValue( daoUtil.getString( ++nIndex ) );
            }
    
            return linkedValuesFormResponseConfigValue;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nLinkedValuesFormResponseConfigValueId  )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nLinkedValuesFormResponseConfigValueId );
            daoUtil.executeUpdate( );
        }
    }
    
    @Override
    public void deleteByIdConfig( int nIdConfig )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_IDCONFIG, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdConfig );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdConfigValue( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdConfig( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdForm( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdQuestionSource( ) );
            daoUtil.setString( ++nIndex, linkedValuesFormResponseConfigValue.getQuestionSourceValue( ) );
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdQuestionTarget( ) );
            daoUtil.setString( ++nIndex, linkedValuesFormResponseConfigValue.getQuestionTargetValue( ) );
            
            daoUtil.setInt( ++nIndex, linkedValuesFormResponseConfigValue.getIdConfigValue( ) );
    
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<LinkedValuesFormResponseConfigValue> selectLinkedValuesFormResponseConfigValuesList(  )
    {
        List<LinkedValuesFormResponseConfigValue> listLinkedValuesFormResponseConfigValues = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                int nIndex = 0;
                LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue = new LinkedValuesFormResponseConfigValue( );
                linkedValuesFormResponseConfigValue.setIdConfigValue( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdConfig( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdForm( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdQuestionSource( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setQuestionSourceValue( daoUtil.getString( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdQuestionTarget( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setQuestionTargetValue( daoUtil.getString( ++nIndex ) );
                listLinkedValuesFormResponseConfigValues.add( linkedValuesFormResponseConfigValue );
            }
    
            return listLinkedValuesFormResponseConfigValues;
        }
    }

    @Override
    public List<LinkedValuesFormResponseConfigValue> selectLinkedValuesFormResponseConfigValuesByIdConfig( int nIdConfig )
    {
        List<LinkedValuesFormResponseConfigValue> listLinkedValuesFormResponseConfigValues = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_IDCONFIG, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdConfig );
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                int nIndex = 0;
                LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue = new LinkedValuesFormResponseConfigValue( );
                linkedValuesFormResponseConfigValue.setIdConfigValue( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdConfig( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdForm( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdQuestionSource( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setQuestionSourceValue( daoUtil.getString( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setIdQuestionTarget( daoUtil.getInt( ++nIndex ) );
                linkedValuesFormResponseConfigValue.setQuestionTargetValue( daoUtil.getString( ++nIndex ) );
                listLinkedValuesFormResponseConfigValues.add( linkedValuesFormResponseConfigValue );
            }
    
            return listLinkedValuesFormResponseConfigValues;
        }
    }

}
