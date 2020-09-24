/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.forms.service.archiver;

import java.util.List;

import javax.inject.Inject;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.AbstractArchiveProcessingService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.ICompleteFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskHistoryService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Service for archival of type delete of plugin-workflow.
 */
public class WorkflowFormsDeleteArchiveProcessingService extends AbstractArchiveProcessingService
{
    public static final String BEAN_NAME = "workflow-forms.workflowFormsDeleteArchiveProcessingService";

    private static final String TASK_TYPE_EDITFORMS = "editFormResponseTypeTask";
    private static final String TASK_TYPE_COMPLETEFORMS = "completeFormResponseTypeTask";
    private static final String TASK_TYPE_RESUBMITFORMS = "resubmitFormResponseTypeTask";

    @Inject
    private IResubmitFormResponseService _resubmitFormResponseService;

    @Inject
    private ICompleteFormResponseService _completeFormResponseService;

    @Inject
    private IEditFormResponseTaskHistoryService _editFormResponseTaskHistoryService;

    @Inject
    private FormService _formService;

    @Override
    public void archiveResource( ResourceWorkflow resourceWorkflow )
    {
        List<ResourceHistory> historyList = _resourceHistoryService.getAllHistoryByResource( resourceWorkflow.getIdResource( ),
                resourceWorkflow.getResourceType( ), resourceWorkflow.getWorkflow( ).getId( ) );

        archiveTaskEditForms( historyList );
        archiveTaskCompleteForms( historyList );
        archiveTaskResubmitForms( historyList );
        archiveFormResponse( resourceWorkflow );
    }

    private void archiveTaskEditForms( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_EDITFORMS );
            for ( ITask task : taskList )
            {
                _editFormResponseTaskHistoryService.removeAllByHistoryAndTask( history, task );
            }
        }
    }

    private void archiveTaskCompleteForms( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_COMPLETEFORMS );
            for ( ITask task : taskList )
            {
                _completeFormResponseService.removeByIdHistory( history.getId( ), task.getId( ) );
            }
        }
    }

    private void archiveTaskResubmitForms( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_RESUBMITFORMS );
            for ( ITask task : taskList )
            {
                _resubmitFormResponseService.removeByIdHistory( history.getId( ), task.getId( ) );
            }
        }
    }

    private void archiveFormResponse( ResourceWorkflow resourceWorkflow )
    {
        int formResponseId = resourceWorkflow.getIdResource( );
        FormResponse formResponse = FormResponseHome.loadById( formResponseId );

        for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponseId ) )
        {
            FormQuestionResponseHome.remove( formQuestionResponse );
        }

        FormResponseStepHome.removeByFormResponse( formResponseId );

        FormResponseHome.remove( formResponseId );

        _formService.fireFormResponseEventDelete( formResponse );
    }
}
