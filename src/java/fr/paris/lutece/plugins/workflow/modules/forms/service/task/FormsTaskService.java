/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class is a service for the tasks of the plugin-forms
 *
 */
public class FormsTaskService implements IFormsTaskService
{
    private final IResourceHistoryService _resourceHistoryService;

    @Inject
    private IActionService _actionService;

    @Inject
    private IStateService _stateService;

    /**
     * Constructor
     * 
     * @param resourceHistoryService
     *            the resource history service
     */
    @Inject
    public FormsTaskService( IResourceHistoryService resourceHistoryService )
    {
        _resourceHistoryService = resourceHistoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory findResourceHistory( int nIdResourceHistory )
    {
        return _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormResponse findFormResponseFrom( int nIdResource, String strResourceType )
    {
        return loadFormResponse( nIdResource, strResourceType, true );
    }

    @Override
    public FormResponse findFormResponseWithoutSteps( int nIdResource, String strResourceType )
    {
        return loadFormResponse( nIdResource, strResourceType, false );
    }

    private FormResponse loadFormResponse( int nIdResource, String strResourceType, boolean loadSteps )
    {
        FormResponse formResponse = null;

        if ( FormResponse.RESOURCE_TYPE.equals( strResourceType ) )
        {
            if ( loadSteps )
            {
                formResponse = FormResponseHome.findByPrimaryKey( nIdResource );
            }
            else
            {
                formResponse = FormResponseHome.loadById( nIdResource );
            }
        }
        else
        {
            throw new AppException( "This task must be used with a form" );
        }

        return formResponse;
    }

    @Override
    public List<String> buildFormStepDisplayTreeList( HttpServletRequest request, List<Step> listStep, List<Question> listQuestionToDisplay,
            FormResponse formResponse, DisplayType displayType )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
        List<Integer> listQuestionToDisplayId = listQuestionToDisplay.stream( ).map( Question::getId ).collect( Collectors.toList( ) );

        listFormQuestionResponse = listFormQuestionResponse.stream( )
                .filter( formQuestionResponse -> listQuestionToDisplayId.contains( formQuestionResponse.getQuestion( ).getId( ) ) )
                .collect( Collectors.toList( ) );

        if ( !CollectionUtils.isEmpty( listStep ) )
        {
            for ( Step step : listStep )
            {
                int nIdStep = step.getId( );

                StepDisplayTree stepDisplayTree = new StepDisplayTree( nIdStep, formResponse, listQuestionToDisplayId );
                listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, listFormQuestionResponse, request.getLocale( ), displayType ) );
            }
        }

        return listFormDisplayTrees;
    }

    @Override
    public List<EditableResponse> findChangedResponses( List<EditableResponse> listEditableResponse )
    {
        List<EditableResponse> listChangedResponse = new ArrayList<>( );

        for ( EditableResponse editableResponse : listEditableResponse )
        {
            IEntryDataService dataService = EntryServiceManager.getInstance( )
                    .getEntryDataService( editableResponse.getQuestion( ).getEntry( ).getEntryType( ) );

            if ( dataService.isResponseChanged( editableResponse.getResponseSaved( ), editableResponse.getResponseFromForm( ) ) )
            {
                listChangedResponse.add( editableResponse );
            }
        }

        return listChangedResponse;
    }

    @Override
    public List<EditableResponse> createEditableResponses( FormResponse formResponse, List<Question> listQuestion, HttpServletRequest request )
    {
        List<EditableResponse> listEditableResponse = new ArrayList<>( );

        for ( Question question : listQuestion )
        {
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            FormQuestionResponse responseFromForm = entryDataService.createResponseFromRequest( question, request, false );
            responseFromForm.setIdFormResponse( formResponse.getId( ) );
            FormQuestionResponse responseSaved = findSavedResponse( formResponse, question );

            EditableResponse editableResponse = new EditableResponse( responseSaved, responseFromForm );
            listEditableResponse.add( editableResponse );
        }

        return listEditableResponse;
    }

    private FormQuestionResponse findSavedResponse( FormResponse formResponse, Question question )
    {
        FormQuestionResponse formQuestionResponse = null;

        List<FormQuestionResponse> listResponseSaved = findResponses( formResponse, question );

        for ( FormQuestionResponse responseSaved : listResponseSaved )
        {
            if ( responseSaved.getQuestion( ).getIterationNumber( ) == question.getIterationNumber( ) )
            {
                formQuestionResponse = responseSaved;
                break;
            }
        }

        return formQuestionResponse;
    }

    @Override
    public List<FormQuestionResponse> findResponses( FormResponse formResponse, Question question )
    {
        List<FormQuestionResponse> listFormQuestionResponse = new ArrayList<>( );

        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            if ( formResponseStep.getStep( ).getId( ) == question.getIdStep( ) )
            {
                for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
                {
                    if ( formQuestionResponse.getQuestion( ).getId( ) == question.getId( ) )
                    {
                        listFormQuestionResponse.add( formQuestionResponse );
                    }
                }
            }
        }

        return listFormQuestionResponse;
    }

    @Override
    public ReferenceList getListStates( int nIdAction )
    {
        ReferenceList referenceListStates = new ReferenceList( );
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action != null ) && ( action.getWorkflow( ) != null ) )
        {
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( action.getWorkflow( ).getId( ) );

            List<State> listStates = _stateService.getListStateByFilter( stateFilter );

            referenceListStates.addItem( -1, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, "id", "name", true ) );
        }

        return referenceListStates;
    }

    @Override
    public void setSiteMessage( HttpServletRequest request, String strMessage, int nTypeMessage, String strUrlReturn ) throws SiteMessageException
    {
        if ( StringUtils.isNotBlank( strUrlReturn ) )
        {
            SiteMessageService.setMessage( request, strMessage, nTypeMessage, strUrlReturn );
        }
        else
        {
            SiteMessageService.setMessage( request, strMessage, nTypeMessage );
        }
    }

    @Override
    public FormResponse getFormResponseFromIdHistory( int nIdHistory )
    {
        FormResponse response = null;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );

        if ( resourceHistory != null && FormResponse.RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) ) )
        {
            response = FormResponseHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        }

        return response;
    }
}
