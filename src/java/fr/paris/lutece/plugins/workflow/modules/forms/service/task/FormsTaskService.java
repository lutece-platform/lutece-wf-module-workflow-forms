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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeDate;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.forms.utils.EditableResponse;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.portal.business.file.FileHome;
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
    private static final String NULL = "null";
    private static final String SEPARATOR = ", ";

    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Inject
    private IActionService _actionService;

    @Inject
    private IStateService _stateService;

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

        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( formQuestionResponse.getQuestion( ).getEntry( ) );
            if ( entryTypeService instanceof EntryTypeDate )
            {
                for ( Response response : formQuestionResponse.getEntryResponse( ) )
                {
                    response.setToStringValueResponse( entryTypeService.getResponseValueForRecap( formQuestionResponse.getQuestion( ).getEntry( ), request,
                            response, request.getLocale( ) ) );
                }
            }
        }

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
    public List<String> buildFormStepDisplayTree( HttpServletRequest request, List<Step> listStep, List<Question> listQuestionToDisplay,
            List<FormQuestionResponse> listFormQuestionResponse, FormResponse formResponse, DisplayType displayType )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        List<Integer> listQuestionToDisplayId = listFormQuestionResponse.stream( ).map( FormQuestionResponse::getQuestion ).map( Question::getId )
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

            if ( responseSaved == null )
            {
                EditableResponse editableResponse = new EditableResponse( responseSaved, responseFromForm );
                listEditableResponse.add( editableResponse );
                continue;
            }

            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) );
            if ( entryTypeService instanceof EntryTypeDate )
            {
                for ( Response response : responseSaved.getEntryResponse( ) )
                {
                    response.setToStringValueResponse(
                            entryTypeService.getResponseValueForRecap( question.getEntry( ), request, response, request.getLocale( ) ) );
                }
            }
            if ( entryTypeService instanceof AbstractEntryTypeUpload )
            {
                for ( Response response : responseSaved.getEntryResponse( ) )
                {
                    if ( response.getFile( ) != null )
                    {
                        response.setFile( FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) ) );
                    }
                }
            }
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

    @Override
    public String createPreviousNewValue( FormQuestionResponse responseForm )
    {
        String value = StringUtils.EMPTY;
        if ( responseForm == null )
        {
            return value;
        }
        for ( int i = 0; i < responseForm.getEntryResponse( ).size( ); i++ )
        {
            Response response = responseForm.getEntryResponse( ).get( i );

            if ( response.getFile( ) != null )
            {
                value = response.getFile( ).getTitle( );
            }
            else
            {
                if ( response.getToStringValueResponse( ) == null || response.getToStringValueResponse( ).equalsIgnoreCase( NULL ) )
                {
                    value = StringUtils.EMPTY;
                }
                else
                {
                    value += response.getToStringValueResponse( );
                }
            }

            if ( i + 1 != responseForm.getEntryResponse( ).size( ) )
            {
                value += SEPARATOR;
            }
        }
        return value;
    }

    @Override
    public List<FormQuestionResponse> getSubmittedFormQuestionResponses( HttpServletRequest request, FormResponse formResponse, List<Question> listQuestions )
    {
        List<FormQuestionResponse> submittedFormResponses = new ArrayList<>( );
        for ( Question question : listQuestions )
        {
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            FormQuestionResponse responseFromForm = entryDataService.createResponseFromRequest( question, request, false );
            responseFromForm.setIdFormResponse( formResponse.getId( ) );
            submittedFormResponses.add( responseFromForm );
        }
        return submittedFormResponses;
    }

    @Override
    public boolean areFormQuestionResponsesValid( List<FormQuestionResponse> listFormQuestionResponse )
    {
        boolean areAllResponsesValid = Boolean.TRUE;

        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            if ( !isResponseValid( formQuestionResponse ) )
            {
                areAllResponsesValid = Boolean.FALSE;
            }
        }
        return areAllResponsesValid;
    }

    /**
     * Check whether the given FormQuestionResponse satisfies the Validator associated with it
     * 
     * @param formQuestionResponse
     *            the FormQuestionResponse to check
     * @return true if the Response is valid, returns false otherwise
     */
    private boolean isResponseValid( FormQuestionResponse formQuestionResponse )
    {
        // Get the list of controls created for this question's validation process
        List<Control> listControl = ControlHome.getControlByQuestionAndType( formQuestionResponse.getQuestion( ).getId( ),
                ControlType.VALIDATION.getLabel( ) );

        // Check that the current response is valid with each associated control 
        for ( Control control : listControl )
        {
            IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );

            // If the given response is not valid with a control
            if ( !validator.validate( formQuestionResponse, control ) )
            {
                // Create an error to be displayed
                GenericAttributeError error = new GenericAttributeError( );
                error.setIsDisplayableError( true );
                error.setErrorMessage( control.getErrorMessage( ) );
                // Set the error on the response's Entry field
                formQuestionResponse.setError( error );

                return false;
            }
        }
        return true;
    }
}
