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
package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.forms.business.ModifyFormResponseUpdateStatusTaskConfig;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

@ApplicationScoped
@Named( "workflow-forms.modifyFormResponseUpdateStatusTaskComponent" )
public class ModifyFormResponseUpdateStatusTaskComponent extends NoFormTaskComponent
{
    // Mark
    private static final String MARK_CONFIG = "config";

    // Templates
    private static final String TEMPLATE_TASK_FORM_EDITSTATUS_CONFIG = "admin/plugins/workflow/modules/forms/task_edit_form_response_status.html";

    private ModifyFormResponseUpdateStatusTaskConfig _config;
    
    @Inject
    public ModifyFormResponseUpdateStatusTaskComponent( @Named( "workflow-forms.modifyUpdateStatusTypeTask" ) ITaskType taskType,
    		@Named( "workflow-forms.modifyFormResponseUpdateStatusTaskService" ) ITaskConfigService taskConfigService )
    {
        setTaskType( taskType );
        setTaskConfigService( taskConfigService );
    }

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        _config = getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, _config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM_EDITSTATUS_CONFIG, locale, model );

        return template.getHtml( );
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

}
