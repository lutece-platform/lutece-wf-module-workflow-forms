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
package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.IFormResponseDAO;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IResubmitFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseValue;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Implements IResubmitResponseService
 */
public class ResubmitFormResponseService extends AbstractFormResponseService implements IResubmitFormResponseService
{

    private static final String MESSAGE_APP_ERROR = "module.workflow.forms.message.app_error";
    private static final String PARAMETER_URL_RETURN = "url_return";

    @Inject
    private ITaskService _taskService;

    @Inject
    private IResubmitFormResponseDAO _resubmitFormResponseDAO;

    @Inject
    private IResubmitFormResponseValueDAO _resubmitFormResponseValueDAO;

    @Inject
    private IEntryDAO _entryDAO;

    @Inject
    private IFormResponseDAO formResponseDAO;

    @Inject
    @Named( "workflow-forms.taskResubmitResponseConfigService" )
    private ITaskConfigService _taskResubmitResponseConfigService;

    @Override
    public ResubmitFormResponse find( int nIdHistory, int nIdTask )
    {
        ResubmitFormResponse resubmitFormResponse = _resubmitFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        if ( resubmitFormResponse != null )
        {
            resubmitFormResponse
                    .setListResubmitReponseValues( _resubmitFormResponseValueDAO.load( resubmitFormResponse.getIdHistory( ), WorkflowUtils.getPlugin( ) ) );
        }

        return resubmitFormResponse;
    }

    @Override
    public List<Entry> getInformationListEntries( int nIdHistory )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );

        List<ResubmitFormResponseValue> listEditRecordValues = _resubmitFormResponseValueDAO.load( nIdHistory, plugin );
        List<Entry> listEntries = new ArrayList<>( );
        for ( ResubmitFormResponseValue value : listEditRecordValues )
        {
            Entry entry = _entryDAO.load( value.getIdEntry( ), plugin );

            if ( entry != null )
            {
                listEntries.add( entry );
            }
        }
        return listEntries;
    }

    @Override
    public List<Entry> getFormListEntries( int nIdRecord, String strResourceType )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );
        FormResponse response = formResponseDAO.load( nIdRecord, plugin );
        return _entryDAO.findEntriesWithoutParent( plugin, response.getFormId( ), Form.RESOURCE_TYPE );
    }

    @Override
    public List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse )
    {
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
        List<Question> listFormQuestion = QuestionHome.getListQuestionByIdForm( form.getId( ) );

        listFormQuestion = listFormQuestion.stream( ).filter( question -> question.getEntry( ).isUsedInCorrectFormResponse( ) ).collect( Collectors.toList( ) );

        List<FormQuestionResponse> listFormQuestionResponses = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );

        List<Question> listQuestionWithResponse = new ArrayList<>( );

        for ( Question question : listFormQuestion )
        {
            FormQuestionResponse formQuestionResponse = listFormQuestionResponses.stream( ).filter( fqr -> fqr.getQuestion( ).getId( ) == question.getId( ) )
                    .findFirst( ).orElse( null );

            if ( formQuestionResponse != null && CollectionUtils.isNotEmpty( formQuestionResponse.getEntryResponse( ) ) )
            {
                listQuestionWithResponse.add( question );
            }
        }
        return listQuestionWithResponse;
    }

    @Override
    public void removeByIdHistory( int nIdHistory, int nIdTask )
    {
        ResubmitFormResponse resubmitFormResponse = find( nIdHistory, nIdTask );

        if ( resubmitFormResponse != null )
        {
            Plugin plugin = WorkflowUtils.getPlugin( );
            _resubmitFormResponseValueDAO.delete( resubmitFormResponse.getIdHistory( ), plugin );
            _resubmitFormResponseDAO.deleteByIdHistory( nIdHistory, nIdTask, plugin );
        }
    }

    @Override
    public void removeByIdTask( int nIdTask )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );
        List<ResubmitFormResponse> listResponse = _resubmitFormResponseDAO.loadByIdTask( nIdTask, plugin );
        for ( ResubmitFormResponse editRecord : listResponse )
        {
            _resubmitFormResponseValueDAO.delete( editRecord.getIdHistory( ), plugin );
        }

        _resubmitFormResponseDAO.deleteByIdTask( nIdTask, plugin );
    }

    @Override
    public void create( ResubmitFormResponse resubmitFormResponse )
    {
        if ( resubmitFormResponse != null )
        {
            Plugin plugin = WorkflowUtils.getPlugin( );
            _resubmitFormResponseDAO.insert( resubmitFormResponse, plugin );

            for ( ResubmitFormResponseValue value : resubmitFormResponse.getListResubmitReponseValues( ) )
            {
                value.setIdHistory( resubmitFormResponse.getIdHistory( ) );
                _resubmitFormResponseValueDAO.insert( value, plugin );
            }
        }
    }

    @Override
    public void update( ResubmitFormResponse resubmitFormResponse )
    {
        if ( resubmitFormResponse != null )
        {
            Plugin plugin = WorkflowUtils.getPlugin( );
            _resubmitFormResponseDAO.store( resubmitFormResponse, plugin );
            // Remove its edit record values first
            _resubmitFormResponseValueDAO.delete( resubmitFormResponse.getIdHistory( ), plugin );

            for ( ResubmitFormResponseValue value : resubmitFormResponse.getListResubmitReponseValues( ) )
            {
                value.setIdHistory( resubmitFormResponse.getIdHistory( ) );
                _resubmitFormResponseValueDAO.insert( value, plugin );
            }
        }
    }

    @Override
    public boolean isRecordStateValid( ResubmitFormResponse resubmitFormResponse, Locale locale )
    {
        ITask task = _taskService.findByPrimaryKey( resubmitFormResponse.getIdTask( ), locale );
        ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( resubmitFormResponse.getIdTask( ) );

        return isRecordStateValid( task, config, resubmitFormResponse.getIdHistory( ) );
    }

    @Override
    public List<Question> getListQuestionToEdit( FormResponse formResponse, List<ResubmitFormResponseValue> listEditRecordValues )
    {
        List<Entry> listEntries = new ArrayList<>( );
        for ( ResubmitFormResponseValue value : listEditRecordValues )
        {
            Entry entry = EntryHome.findByPrimaryKey( value.getIdEntry( ) );
            listEntries.add( entry );
        }
        List<Integer> idEntries = listEntries.stream( ).map( Entry::getIdEntry ).collect( Collectors.toList( ) );

        List<Question> listQuestions = findListQuestionUsedCorrectForm( formResponse );
        return listQuestions.stream( ).filter( question -> idEntries.contains( question.getEntry( ).getIdEntry( ) ) ).collect( Collectors.toList( ) );
    }

    @Override
    public boolean doEditResponseData( HttpServletRequest request, ResubmitFormResponse resubmitFormResponse ) throws SiteMessageException
    {
        FormResponse response = _formsTaskService.getFormResponseFromIdHistory( resubmitFormResponse.getIdHistory( ) );
        if ( response == null )
        {
            _formsTaskService.setSiteMessage( request, MESSAGE_APP_ERROR, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );

            return false;
        }
        List<Question> listQuestions = getListQuestionToEdit( response, resubmitFormResponse.getListResubmitReponseValues( ) );

        doEditResponseData( request, response, listQuestions );
        return true;
    }

    @Override
    public void doChangeResponseState( ResubmitFormResponse resubmitFormResponse, Locale locale )
    {
        ITask task = _taskService.findByPrimaryKey( resubmitFormResponse.getIdTask( ), locale );
        ResubmitFormResponseTaskConfig config = _taskResubmitResponseConfigService.findByPrimaryKey( resubmitFormResponse.getIdTask( ) );

        if ( task != null && config != null )
        {
            doChangeResponseState( task, config.getIdStateAfterEdition( ), resubmitFormResponse.getIdHistory( ), locale );
        }
    }

    @Override
    public void doCompleteResponse( ResubmitFormResponse resubmitFormResponse )
    {
        resubmitFormResponse.setIsComplete( true );
        update( resubmitFormResponse );
    }
}
