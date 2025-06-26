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
package fr.paris.lutece.plugins.workflow.modules.forms.service;

import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.DuplicateFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ModifyFormResponseUpdateStatusTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TaskConfigServiceProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.taskResubmitResponseConfigService" )
    public ITaskConfigService produceTaskResubmitResponseConfigService(
    		@Named( "worklow-forms.taskResubmitFormResponseConfigDAO" ) ITaskConfigDAO<ResubmitFormResponseTaskConfig> resubmitFormResponseTaskConfigDAO )
    {
		TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) resubmitFormResponseTaskConfigDAO );
        return taskService;
    }

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.taskCompleteResponseConfigService" )
    public ITaskConfigService produceTaskCompleteResponseConfigService(
    		@Named( "worklow-forms.taskCompleteFormResponseConfigDAO" ) ITaskConfigDAO<CompleteFormResponseTaskConfig> completeFormResponseTaskConfigDAO )
    {
		TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) completeFormResponseTaskConfigDAO );
        return taskService;
    }

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.modifyFormResponseUpdateStatusTaskService" )
    public ITaskConfigService produceModifyFormResponseUpdateStatusTaskService(
    		@Named( "worklow-forms.modifyFormResponseUpdateStatusTaskConfigDAO" ) ITaskConfigDAO<ModifyFormResponseUpdateStatusTaskConfig> modifyFormResponseUpdateStatusTaskConfigDAO )
    {
		TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) modifyFormResponseUpdateStatusTaskConfigDAO );
        return taskService;
    }

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.taskDuplicateFormResponseConfigService" )
    public ITaskConfigService produceTaskDuplicateFormResponseConfigService(
    		@Named( "workflow-forms.duplicateFormResponseTaskConfigDAO" ) ITaskConfigDAO<DuplicateFormResponseTaskConfig> duplicateFormResponseTaskConfigDAO )
    {
		TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) duplicateFormResponseTaskConfigDAO );
        return taskService;
    }
}
