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
package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfigValue;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfigValueHome;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * 
 * LinkedValuesFormResponseConfigService
 *
 */
@ApplicationScoped
@Named( LinkedValuesFormResponseConfigService.BEAN_NAME )
public class LinkedValuesFormResponseConfigService extends TaskConfigService
{
    
    public static final String BEAN_NAME = "workflow-forms.linkedValuesFormResponseConfigService";
    
    @Inject
    public LinkedValuesFormResponseConfigService( @Named( "worklow-forms.linkedValuesFormResponseConfigDAO" ) ITaskConfigDAO<LinkedValuesFormResponseConfig> linkedValuesFormResponseConfigDAO ) 
	{
       setTaskConfigDAO( (ITaskConfigDAO) linkedValuesFormResponseConfigDAO ); 
    }
    
    @Override
    public LinkedValuesFormResponseConfig findByPrimaryKey( int nIdTask )
    {
        LinkedValuesFormResponseConfig config = super.findByPrimaryKey( nIdTask );
        if ( config != null )
        {
            config.setListConfigValues( LinkedValuesFormResponseConfigValueHome.findByIdConfig( config.getIdConfig( ) ) );
        }
        return config;
    }

    @Override
    public void create( ITaskConfig config )
    {
        LinkedValuesFormResponseConfig conf = (LinkedValuesFormResponseConfig) config;
        super.create( conf );
        
        for ( LinkedValuesFormResponseConfigValue configValue : conf.getListConfigValues( ) )
        {
            configValue.setIdConfig( conf.getIdConfig( ) );
            LinkedValuesFormResponseConfigValueHome.create( configValue );
        }
    }

    @Override
    public void update( ITaskConfig config )
    {
        LinkedValuesFormResponseConfig conf = (LinkedValuesFormResponseConfig) config;
        LinkedValuesFormResponseConfigValueHome.removeByIdConfig( conf.getIdConfig( ) );
        super.update( config );

        for ( LinkedValuesFormResponseConfigValue configValue : conf.getListConfigValues( ) )
        {
            configValue.setIdConfig( conf.getIdConfig( ) );
            LinkedValuesFormResponseConfigValueHome.create( configValue );
        }
    }

    @Override
    public void remove( int nIdTask )
    {
        LinkedValuesFormResponseConfig config = super.findByPrimaryKey( nIdTask );
        if ( config != null )
        {
            LinkedValuesFormResponseConfigValueHome.removeByIdConfig( config.getIdConfig( ) );
        }
        super.remove( nIdTask );
    }
}
