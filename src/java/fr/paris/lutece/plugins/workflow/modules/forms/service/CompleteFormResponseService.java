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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.AbstractCompleteFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskHistory;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.ICompleteFormResponseTaskHistoryService;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;

@ApplicationScoped
@Named( "workflow-forms.taskCompleteResponseService" )
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

    @Inject
    private ICompleteFormResponseTaskHistoryService _completeFormResponseTaskHistoryService;

    // List of FormQuestionResponse to store the user's new Responses 
    private List<FormQuestionResponse> _submittedFormResponses;

    @Override
    public List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse )
    {
        List<FormDisplay> listFormDisplay = FormDisplayHome.getFormDisplayByForm(formResponse.getFormId() );

        List<Question> listBaseQuestionForm = QuestionHome.getListQuestionByIdForm( formResponse.getFormId( ) );
        listBaseQuestionForm = listBaseQuestionForm.stream( ).filter( ( Question question ) -> {
            Field fieldUsedCompleteResponse = question.getEntry( ).getFieldByCode( FormsConstants.PARAMETER_USED_COMPLETE_RESPONSE );
            return fieldUsedCompleteResponse != null && Boolean.valueOf( fieldUsedCompleteResponse.getValue( ) );
        } ).collect( Collectors.toList( ) );

        Map<Integer,Integer> idDisplayGroupNIterationMax = new HashMap<>();

        List<FormQuestionResponse> listFormQuestionResponses = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );

        for ( FormQuestionResponse questionResponse : listFormQuestionResponses)
        {
            FormDisplay formdisplay = listFormDisplay.stream().filter( display -> display.getCompositeId() == questionResponse.getQuestion().getId()).findFirst().orElse( null );
            if ( formdisplay!=null && formdisplay.getParentId() > 0)
            {
                Integer maxValueIter = idDisplayGroupNIterationMax.get( formdisplay.getParentId() );
                if( maxValueIter==null || maxValueIter<questionResponse.getQuestion().getIterationNumber() )
                {
                    idDisplayGroupNIterationMax.put( formdisplay.getParentId() , questionResponse.getQuestion().getIterationNumber() );
                }
            }
        }

        List<Question> listQuestionForm = new ArrayList<>();
        for( Question question : listBaseQuestionForm)
        {

            FormDisplay formdisplay = listFormDisplay.stream().filter( display -> display.getCompositeId() == question.getId()).findFirst().orElse( null );
            if( formdisplay != null && formdisplay.getParentId() > 0 )
            {
                int nbIteration = idDisplayGroupNIterationMax.get( formdisplay.getParentId() );

                for( int i =0 ; i<=nbIteration; i++)
                {
                    Question copyQuestion = new Question( question );
                    copyQuestion.setIterationNumber( i );
                    listQuestionForm.add( copyQuestion );
                }
            }
            else
            {
                listQuestionForm.add( question );
            }
        }
        return listQuestionForm;
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
        List<Question> listQuestions = findListQuestionUsedCorrectForm( formResponse );


        return listQuestions.stream().filter(question ->
                        listEditRecordValues.stream().anyMatch(completeFormResponseValue ->
                                completeFormResponseValue.getIdEntry() == question.getEntry().getIdEntry()
                                        && (completeFormResponseValue.getIterationNumber() == AbstractCompleteFormResponseValue.DEFAULT_ITERATION_NUMBER
                                        || completeFormResponseValue.getIterationNumber() == question.getIterationNumber())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean doEditResponseData( HttpServletRequest request, CompleteFormResponse completeFormResponse, int idTask, int idHistory )
            throws SiteMessageException
    {
        FormResponse response = _formsTaskService.getFormResponseFromIdHistory( completeFormResponse.getIdHistory( ) );
        if ( response == null )
        {
            _formsTaskService.setSiteMessage( request, MESSAGE_APP_ERROR, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );

            return false;
        }
        List<Question> listQuestions = getListQuestionToEdit( response, completeFormResponse.getListCompleteReponseValues( ) );
        // Get the values of the newly submitted Responses
        _submittedFormResponses = _formsTaskService.getSubmittedFormQuestionResponses( request, response, listQuestions );
        // Check if the new Responses are valid
        if ( !areFormResponsesValid( _submittedFormResponses ) )
        {
            return false;
        }
        // Reset the content of the List
        _submittedFormResponses = Collections.emptyList( );
        doEditResponseData( request, response, listQuestions, idTask, idHistory );
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
        completeFormResponse.setDateCompleted( new Date( System.currentTimeMillis( ) ) );
        update( completeFormResponse );
    }

    @Override
    protected void createTaskHistory( EditableResponse editableResponse, int idTask, int idHistory )
    {
        CompleteFormResponseTaskHistory history = new CompleteFormResponseTaskHistory( );
        history.setIdTask( idTask );
        history.setIdHistory( idHistory );
        history.setQuestion( editableResponse.getQuestion( ) );
        history.setNewValue( _formsTaskService.createPreviousNewValue( editableResponse.getResponseFromForm( ) ) );

        _completeFormResponseTaskHistoryService.create( history );
    }

    @Override
    public List<FormQuestionResponse> getSubmittedFormResponseList( )
    {
        return _submittedFormResponses;
    }
}
