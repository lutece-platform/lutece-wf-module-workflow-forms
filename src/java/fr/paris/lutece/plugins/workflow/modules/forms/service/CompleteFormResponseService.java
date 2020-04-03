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

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;

public class CompleteFormResponseService extends AbstractFormResponseService implements ICompleteFormResponseService
{

    private static final String MESSAGE_APP_ERROR = "module.workflow.forms.message.app_error";
    private static final String PARAMETER_URL_RETURN = "url_return";

    @Inject
    private ICompleteFormResponseDAO _completeFormResponseDAO;

    @Inject
    private ICompleteFormResponseValueDAO _completeFormResponseValueDAO;

    @Inject
    private IEntryDAO _entryDAO;

    @Inject
    @Named( "workflow-forms.taskCompleteResponseConfigService" )
    private ITaskConfigService _taskCompleteResponseConfigService;

    @Inject
    private ITaskService _taskService;

    @Override
    public List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse )
    {
        List<Question> listQuestionForm = QuestionHome.getListQuestionByIdForm( formResponse.getFormId( ) );
        return listQuestionForm.stream( ).filter( question -> question.getEntry( ).isUsedInCompleteFormResponse( ) ).collect( Collectors.toList( ) );
    }

    @Override
    public CompleteFormResponse find( int nIdHistory, int nIdTask )
    {
        CompleteFormResponse resubmitFormResponse = _completeFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        if ( resubmitFormResponse != null )
        {
            resubmitFormResponse
                    .setListCompleteReponseValues( _completeFormResponseValueDAO.load( resubmitFormResponse.getIdHistory( ), WorkflowUtils.getPlugin( ) ) );
        }

        return resubmitFormResponse;
    }

    @Override
    public List<Entry> getInformationListEntries( int nIdHistory )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );

        List<CompleteFormResponseValue> listEditRecordValues = _completeFormResponseValueDAO.load( nIdHistory, plugin );
        List<Entry> listEntries = new ArrayList<>( );
        for ( CompleteFormResponseValue value : listEditRecordValues )
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
    public void create( CompleteFormResponse completeFormResponse )
    {
        if ( completeFormResponse != null )
        {
            Plugin plugin = WorkflowUtils.getPlugin( );
            _completeFormResponseDAO.insert( completeFormResponse, plugin );

            for ( CompleteFormResponseValue value : completeFormResponse.getListCompleteReponseValues( ) )
            {
                value.setIdHistory( completeFormResponse.getIdHistory( ) );
                _completeFormResponseValueDAO.insert( value, plugin );
            }
        }
    }

    @Override
    public void update( CompleteFormResponse completeFormResponse )
    {
        if ( completeFormResponse != null )
        {
            Plugin plugin = WorkflowUtils.getPlugin( );
            _completeFormResponseDAO.store( completeFormResponse, plugin );
            // Remove its edit record values first
            _completeFormResponseValueDAO.delete( completeFormResponse.getIdHistory( ), plugin );

            for ( CompleteFormResponseValue value : completeFormResponse.getListCompleteReponseValues( ) )
            {
                value.setIdHistory( completeFormResponse.getIdHistory( ) );
                _completeFormResponseValueDAO.insert( value, plugin );
            }
        }
    }

    @Override
    public void removeByIdHistory( int nIdHistory, int nIdTask )
    {
        CompleteFormResponse resubmitFormResponse = find( nIdHistory, nIdTask );

        if ( resubmitFormResponse != null )
        {
            Plugin plugin = WorkflowUtils.getPlugin( );
            _completeFormResponseValueDAO.delete( resubmitFormResponse.getIdHistory( ), plugin );
            _completeFormResponseDAO.deleteByIdHistory( nIdHistory, nIdTask, plugin );
        }
    }

    @Override
    public void removeByIdTask( int nIdTask )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );
        List<CompleteFormResponse> listResponse = _completeFormResponseDAO.loadByIdTask( nIdTask, plugin );
        for ( CompleteFormResponse editRecord : listResponse )
        {
            _completeFormResponseValueDAO.delete( editRecord.getIdHistory( ), plugin );
        }

        _completeFormResponseDAO.deleteByIdTask( nIdTask, plugin );
    }

    @Override
    public boolean isRecordStateValid( CompleteFormResponse completeFormResponse, Locale locale )
    {
        ITask task = _taskService.findByPrimaryKey( completeFormResponse.getIdTask( ), locale );
        CompleteFormResponseTaskConfig config = _taskCompleteResponseConfigService.findByPrimaryKey( completeFormResponse.getIdTask( ) );

        return isRecordStateValid( task, config, completeFormResponse.getIdHistory( ) );
    }

    @Override
    public List<Question> getListQuestionToEdit( FormResponse formResponse, List<CompleteFormResponseValue> listEditRecordValues )
    {
        List<Integer> idEntries = listEditRecordValues.stream( ).map( CompleteFormResponseValue::getIdEntry ).map( EntryHome::findByPrimaryKey )
                .map( Entry::getIdEntry ).collect( Collectors.toList( ) );

        List<Question> listQuestions = findListQuestionUsedCorrectForm( formResponse );
        return listQuestions.stream( ).filter( question -> idEntries.contains( question.getEntry( ).getIdEntry( ) ) ).collect( Collectors.toList( ) );
    }

    @Override
    public boolean doEditResponseData( HttpServletRequest request, CompleteFormResponse completeFormResponse ) throws SiteMessageException
    {
        FormResponse response = _formsTaskService.getFormResponseFromIdHistory( completeFormResponse.getIdHistory( ) );
        if ( response == null )
        {
            _formsTaskService.setSiteMessage( request, MESSAGE_APP_ERROR, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );

            return false;
        }
        List<Question> listQuestions = getListQuestionToEdit( response, completeFormResponse.getListCompleteReponseValues( ) );

        doEditResponseData( request, response, listQuestions );
        return true;
    }

    @Override
    public void doChangeResponseState( CompleteFormResponse completeFormResponse, Locale locale )
    {
        ITask task = _taskService.findByPrimaryKey( completeFormResponse.getIdTask( ), locale );
        CompleteFormResponseTaskConfig config = _taskCompleteResponseConfigService.findByPrimaryKey( completeFormResponse.getIdTask( ) );

        if ( task != null && config != null )
        {
            doChangeResponseState( task, config.getIdStateAfterEdition( ), completeFormResponse.getIdHistory( ), locale );
        }
    }

    @Override
    public void doCompleteResponse( CompleteFormResponse completeFormResponse )
    {
        completeFormResponse.setIsComplete( true );
        update( completeFormResponse );
    }
}
