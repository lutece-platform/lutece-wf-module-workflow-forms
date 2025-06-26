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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TaskTypeProducer 
{
	@Produces
    @ApplicationScoped
    @Named( "workflow-forms.modifyUpdateStatusTypeTask" )
    public ITaskType produceModifyUpdateStatusTypeTask( 
    		@ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.key" ) String key,
            @ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-forms.modifyUpdateStatusTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.editFormResponseTypeTask" )
	public ITaskType produceEditFormResponseTypeTask(
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.editFormResponseTypeAutoUpdateTask" )
	public ITaskType produceEditFormResponseTypeAutoUpdateTask(
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.editFormResponseTypeAutoUpdateTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.editFormResponseFoTypeTask" )
	public ITaskType produceEditFormResponseFoTypeTask(
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.editFormResponseFoTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.taskDuplicateFormResponse" )
	public ITaskType produceTaskDuplicateFormResponse(
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.taskDuplicateFormResponse.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.resubmitFormResponseTypeTask" )
	public ITaskType produceResubmitFormResponseTypeTask(
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.resubmitFormResponseTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.completeFormResponseTypeTask" )
	public ITaskType produceCompleteFormResponseTypeTask(
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.completeFormResponseTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.modifyUpdateDateTypeTask" )
	public ITaskType produceModifyUpdateDateTypeTask(
	        @ConfigProperty( name = "workflow-forms.modifyUpdateDateTypeTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.modifyUpdateDateTypeTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.modifyUpdateDateTypeTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.modifyUpdateDateTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.modifyUpdateDateTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.modifyUpdateDateTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, null, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	@Produces
	@ApplicationScoped
	@Named( "workflow-forms.linkedValuesFormResponseTypeTask" )
	public ITaskType produceLinkedValuesFormResponseTypeTask(
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.key" ) String key,
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.titleI18nKey" ) String titleI18nKey,
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.beanName" ) String beanName,
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.configBeanName" ) String configBeanName,
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.configRequired", defaultValue = "false" ) boolean configRequired,
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
	        @ConfigProperty( name = "workflow-forms.linkedValuesFormResponseTypeTask.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
	{
	    return buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
	}

	private ITaskType buildTaskType( String strKey, String strTitleI18nKey, String strBeanName, String strConfigBeanName, 
			boolean bIsConfigRequired, boolean bIsFormTaskRequired, boolean bIsTaskForAutomaticAction )
    {
        TaskType taskType = new TaskType( );
        taskType.setKey( strKey );
        taskType.setTitleI18nKey( strTitleI18nKey );
        taskType.setBeanName( strBeanName );
        taskType.setConfigBeanName( strConfigBeanName );
        taskType.setConfigRequired( bIsConfigRequired );
        taskType.setFormTaskRequired( bIsFormTaskRequired );
        taskType.setTaskForAutomaticAction( bIsTaskForAutomaticAction );
        return taskType;
    }
	
}
