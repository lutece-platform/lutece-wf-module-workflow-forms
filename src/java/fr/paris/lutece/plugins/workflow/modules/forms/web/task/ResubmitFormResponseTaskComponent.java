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
package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskHistory;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IResubmitFormResponseTaskHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class ResubmitFormResponseTaskComponent extends AbstractFormResponseTaskComponent
{

    // Templates
    private static final String TEMPLATE_TASK_RESUBMIT_RESPONSE_CONFIG = "admin/plugins/workflow/modules/forms/task_resubmit_response_config.html";
    private static final String TEMPLATE_TASK_RESUBMIT_RESPONSE_INFORMATION = "admin/plugins/workflow/modules/forms/task_resubmit_response_information.html";

    // Marks
    private static final String MARK_CONFIG = "config";
    private static final String MARK_LIST_STATES = "list_states";
    private static final String MARK_EDIT_RESPONSE = "edit_response";
    private static final String MARK_LIST_ENTRIES = "list_entries";
    private static final String MARK_LIST_HISTORIES = "list_history";

    @Inject
    @Named( "workflow-forms.taskResubmitResponseConfigService" )
    private ITaskConfigService _taskResubmitResponseConfigService;

    @Inject
    private IResubmitFormResponseService _resubmitResponseService;

    @Inject
    private IFormsTaskService _formsTaskService;

    @Inject
    private IResubmitFormResponseTaskHistoryService _resubmitFormResponseTaskHistoryService;

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, _taskResubmitResponseConfigService.findByPrimaryKey( task.getId( ) ) );
        model.put( MARK_LIST_STATES, _formsTaskService.getListStates( task.getAction( ).getId( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_RESUBMIT_RESPONSE_CONFIG, locale, model );

        return template.getHtml( );
    }

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {

        FormResponse formResponse = _formsTaskService.findFormResponseFrom( nIdResource, strResourceType );
        List<Question> listQuestions = _resubmitResponseService.findListQuestionUsedCorrectForm( formResponse );

        List<Step> listStep = listQuestions.stream( ).map( Question::getStep ).map( Step::getId ).distinct( ).map( StepHome::findByPrimaryKey )
                .collect( Collectors.toList( ) );

        List<String> listStepDisplayTree = _formsTaskService.buildFormStepDisplayTreeList( request, listStep, listQuestions, formResponse,
                DisplayType.RESUBMIT_BACKOFFICE );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_STEP_LIST, listStepDisplayTree );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM, locale, model );

        return template.getHtml( );
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        ResubmitFormResponse resubmitFormResponse = _resubmitResponseService.find( nIdHistory, task.getId( ) );
        ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( task.getId( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_EDIT_RESPONSE, resubmitFormResponse );

        if ( resubmitFormResponse != null )
        {
            model.put( MARK_LIST_ENTRIES, _resubmitResponseService.getInformationListEntries( nIdHistory ) );
            if ( resubmitFormResponse.isComplete( ) )
            {
                List<ResubmitFormResponseTaskHistory> historyList = _resubmitFormResponseTaskHistoryService.load( nIdHistory, task.getId( ) );
                model.put( MARK_LIST_HISTORIES, historyList );
            }
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_RESUBMIT_RESPONSE_INFORMATION, locale, model );

        return template.getHtml( );
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }
}
