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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.workflow.modules.forms.business.AbstractCompleteFormResponseValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlGroup;
import fr.paris.lutece.plugins.forms.business.ControlGroupHome;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.LogicalOperator;
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
import fr.paris.lutece.plugins.forms.web.http.IterationHttpServletRequestWrapper;
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
@ApplicationScoped
@Named( "workflow-forms.formsTaskService" )
public class FormsTaskService implements IFormsTaskService
{
    private static final String NULL = "null";
    private static final String SEPARATOR = ", ";
    private static final int DEFAULT_ITERATION_NUMBER = -1;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> buildFormStepDisplayTreeList( final HttpServletRequest request, final List<Step> listStep, final List<Question> listQuestionToDisplay,
                                                      final FormResponse formResponse, final DisplayType displayType )
    {
        return buildFormStepDisplayTreeList( request, listStep, listQuestionToDisplay, formResponse, displayType, 0 );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> buildFormStepDisplayTreeList( final HttpServletRequest request, final List<Step> listStep, final List<Question> listQuestionToDisplay,
                                                      final FormResponse formResponse, final DisplayType displayType, final int nIdGroupToIterate )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );

        listFormQuestionResponse = listFormQuestionResponse.stream()
                .filter(formQuestionResponse -> listQuestionToDisplay.stream().anyMatch(questionToDisplay -> formQuestionResponse.getQuestion().getId() == questionToDisplay.getId()
                        && (formQuestionResponse.getQuestion().getIterationNumber() == questionToDisplay.getIterationNumber()
                        || questionToDisplay.getIterationNumber() == AbstractCompleteFormResponseValue.DEFAULT_ITERATION_NUMBER)))
                .collect(Collectors.toList());

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

                StepDisplayTree stepDisplayTree = new StepDisplayTree( nIdStep, listQuestionToDisplay, formResponse );
                if ( nIdGroupToIterate > 0 )
                {
                    stepDisplayTree.iterate( nIdGroupToIterate );
                }
                listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, listFormQuestionResponse, request.getLocale( ), displayType ) );
            }
        }

        return listFormDisplayTrees;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> buildFormStepDisplayTree( final HttpServletRequest request, final List<Step> listStep, final List<Question> listQuestionToDisplay,
                                                  final List<FormQuestionResponse> listFormQuestionResponse, final FormResponse formResponse, final DisplayType displayType )
    {
        return buildFormStepDisplayTree( request, listStep, listQuestionToDisplay, listFormQuestionResponse, formResponse, displayType, 0 );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> buildFormStepDisplayTree( final HttpServletRequest request, final List<Step> listStep, final List<Question> listQuestionToDisplay,
                                                  final List<FormQuestionResponse> listFormQuestionResponse, final FormResponse formResponse,
                                                  final DisplayType displayType, final int nIdGroupToIterate )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listStep ) )
        {
            // synchronize the already submitted responses when modifying the iterations
            this.mergeSubmittedResponsesIntoFormResponse( formResponse, listFormQuestionResponse );

            for ( final Step step : listStep )
            {
                int nIdStep = step.getId( );
                StepDisplayTree stepDisplayTree = new StepDisplayTree(nIdStep, listQuestionToDisplay, formResponse);
                if ( nIdGroupToIterate > 0 )
                {
                    stepDisplayTree.iterate( nIdGroupToIterate );
                }
                listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, listFormQuestionResponse, request.getLocale( ), displayType ) );
            }
        }
        return listFormDisplayTrees;
    }

    @Override
    public List<EditableResponse> findChangedResponses( final List<EditableResponse> listEditableResponse )
    {
        final List<EditableResponse> listChangedResponse = new ArrayList<>( );

        for ( final EditableResponse editableResponse : listEditableResponse )
        {
            final IEntryDataService dataService = EntryServiceManager.getInstance( )
                    .getEntryDataService( editableResponse.getQuestion( ).getEntry( ).getEntryType( ) );

            final FormQuestionResponse savedResponse = editableResponse.getResponseSaved( );
            final FormQuestionResponse formResponse = editableResponse.getResponseFromForm( );

            final boolean bSavedEmpty = CollectionUtils.isEmpty( savedResponse == null ? null : savedResponse.getEntryResponse( ) );
            final boolean bFromFormEmpty = CollectionUtils.isEmpty( formResponse == null ? null : formResponse.getEntryResponse( ) );

            boolean bChanged;
            if ( bSavedEmpty && bFromFormEmpty )
            {
                // no value, or hidden fields
                bChanged = false;
            }
            else if ( bSavedEmpty != bFromFormEmpty )
            {
                // one of the list is empty, so a changed occured
                bChanged = true;
            }
            else
            {
                bChanged = dataService.isResponseChanged( savedResponse, formResponse );
            }

            if ( bChanged )
            {
                listChangedResponse.add( editableResponse );
            }
        }

        return listChangedResponse;
    }

    @Override
    public List<EditableResponse> createEditableResponses( final FormResponse formResponse, final List<Question> listQuestion, final HttpServletRequest request )
    {
        final List<EditableResponse> listEditableResponse = new ArrayList<>( );

        for ( final Question question : listQuestion )
        {
            if ( !isConditionalTargetDisplayed( formResponse, question, request ) )
            {
                continue;
            }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeHiddenConditionalTargetResponses( final HttpServletRequest request, final FormResponse formResponse, final List<Question> listQuestions )
    {
        for ( final Question question : listQuestions )
        {
            if ( !isConditionalTargetDisplayed( formResponse, question, request ) )
            {
                final FormQuestionResponse savedResponse = findSavedResponse( formResponse, question );
                if ( savedResponse != null )
                {
                    FormQuestionResponseHome.remove( savedResponse );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeOrphanIterations( final FormResponse formResponse, final List<Question> listSubmittedQuestions )
    {
        for ( final FormQuestionResponse formQuestionResponse : this.findOrphanIterationResponses( formResponse, listSubmittedQuestions ) )
        {
            FormQuestionResponseHome.remove( formQuestionResponse );
        }
    }

    /**
     * Compute the persisted responses whose iteration is beyond the highest iteration submitted for their
     * question
     * <p>
     * A question absent from the submitted list is left untouched : only the
     * questions actually edited on this request have their surplus iterations reclaimed.
     *
     * @param formResponse
     *            the persisted form response being edited
     * @param listSubmittedQuestions
     *            the questions (with their iteration number) present in the submitted responses
     * @return the persisted responses to remove, in encounter order
     */
    public List<FormQuestionResponse> findOrphanIterationResponses( final FormResponse formResponse, final List<Question> listSubmittedQuestions )
    {
        final Map<Integer, Integer> maxSubmittedIteration = new HashMap<>( );
        for ( final Question question : listSubmittedQuestions )
        {
            maxSubmittedIteration.merge( question.getId( ), question.getIterationNumber( ), Math::max );
        }

        final List<FormQuestionResponse> listOrphan = new ArrayList<>( );
        for ( final FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            for ( final FormQuestionResponse formQuestionResponse : new ArrayList<>( formResponseStep.getQuestions( ) ) )
            {
                final Integer nMaxSubmitted = maxSubmittedIteration.get( formQuestionResponse.getQuestion( ).getId( ) );
                if ( nMaxSubmitted != null && formQuestionResponse.getQuestion( ).getIterationNumber( ) > nMaxSubmitted )
                {
                    listOrphan.add( formQuestionResponse );
                }
            }
        }
        return listOrphan;
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
            SiteMessageService.setMessage( request, strMessage, null, null, null,
                null, nTypeMessage, null, strUrlReturn );
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormQuestionResponse> getSubmittedFormQuestionResponses( HttpServletRequest request, FormResponse formResponse, List<Question> listQuestions )
    {
        final List<FormQuestionResponse> submittedFormResponses = new ArrayList<>( );

        for ( final Question question : listQuestions )
        {
            final IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            final FormQuestionResponse responseFromForm = entryDataService.createResponseFromRequest( question, request, true );
            responseFromForm.setIdFormResponse( formResponse.getId( ) );

            if ( !isConditionalTargetDisplayed( formResponse, question, request ) )
            {
                responseFromForm.setEntryResponse( new ArrayList<>( ) );
                responseFromForm.setError( null );
            }

            submittedFormResponses.add( responseFromForm );
        }
        return submittedFormResponses;
    }

    /**
     * Check whether a conditional target question is currently displayed
     *
     * @param formResponse
     *            the FormResponse being edited
     * @param question
     *            the conditional target question
     * @param request
     *            the HTTP request being submitted
     * @return {@code true} if the question is displayed, {@code false} otherwise
     */
    private boolean isConditionalTargetDisplayed( final FormResponse formResponse, final Question question, final HttpServletRequest request )
    {
        final FormDisplay formDisplay = FormDisplayHome.getFormDisplayByFormStepAndComposite( formResponse.getFormId( ), question.getIdStep( ), question.getId( ) );
        if ( formDisplay == null || formDisplay.getDisplayControl( ) == null )
        {
            return true;
        }

        final List<Control> listControl = ControlHome.getControlByControlTargetAndType( formDisplay.getId( ), ControlType.CONDITIONAL );
        if ( CollectionUtils.isEmpty( listControl ) )
        {
            return true;
        }

        // check if the control is a OR, or an AND
        final ControlGroup controlGroup = ControlGroupHome.findByPrimaryKey( listControl.get( 0 ).getIdControlGroup( ) ).orElse( null );
        final boolean bOr = controlGroup != null && LogicalOperator.OR.getLabel( ).equals( controlGroup.getLogicalOperator( ).getLabel( ) );

        int nValid = 0;
        int nNotValid = 0;
        boolean bHasUnevaluable = false;

        for ( final Control control : listControl )
        {
            final List<FormQuestionResponse> listControllerResponses = buildControllerResponses( control, request, question.getIterationNumber( ) );
            final IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );
            if ( listControllerResponses == null || validator == null )
            {
                bHasUnevaluable = true;
                continue;
            }
            if ( validator.validate( listControllerResponses, control ) )
            {
                nValid++;
            }
            else
            {
                nNotValid++;
            }
        }

        return this.isDisplayedFromControlOutcomes( bOr, nValid, nNotValid, bHasUnevaluable );
    }

    /**
     * Decide whether a conditional target is displayed or not
     *
     * @param bOr
     *            {@code true} if the controls are combined with OR, {@code false} for AND
     * @param nValid
     *            number of controls that evaluated to "displayed"
     * @param nNotValid
     *            number of controls that evaluated to "not displayed"
     * @param bHasUnevaluable
     *            {@code true} if at least one control could not be evaluated
     * @return {@code true} if the target is displayed (kept), {@code false} if it is hidden (removed)
     */
    public boolean isDisplayedFromControlOutcomes( final boolean bOr, final int nValid, final int nNotValid, final boolean bHasUnevaluable )
    {
        if ( bOr )
        {
            return nValid > 0 || bHasUnevaluable;
        }
        return nNotValid == 0;
    }

    /**
     * Get the submitted responses from inside the iteration group, or, from outside (0)
     *
     * @param control
     *            the conditional control
     * @param request
     *            the HTTP request being submitted
     * @param nTargetIteration
     *            the iteration of the conditional target being evaluated
     * @return the controlling responses, or {@code null} if any controlling question is absent from the request
     *         (the control then cannot be evaluated)
     */
    private List<FormQuestionResponse> buildControllerResponses( final Control control, final HttpServletRequest request, final int nTargetIteration )
    {
        if ( CollectionUtils.isEmpty( control.getListIdQuestion( ) ) )
        {
            return null;
        }

        final List<FormQuestionResponse> listResponses = new ArrayList<>( );
        for ( final Integer nIdQuestion : control.getListIdQuestion( ) )
        {
            final Question controllerQuestion = QuestionHome.findByPrimaryKey( nIdQuestion );
            if ( controllerQuestion == null || controllerQuestion.getEntry( ) == null )
            {
                return null;
            }

            final FormQuestionResponse controllerResponse = readControllerResponse( controllerQuestion, request, nTargetIteration );
            if ( controllerResponse == null )
            {
                return null;
            }
            listResponses.add( controllerResponse );
        }
        return listResponses;
    }

    /**
     * Reads a controlling question's submitted response.
     *
     * @param controllerQuestion
     *            the controlling question
     * @param request
     *            the HTTP request being submitted
     * @param nTargetIteration
     *            the iteration of the conditional target being evaluated
     * @return the first non-empty response found, or {@code null} if the controller carries no submitted value
     */
    private FormQuestionResponse readControllerResponse( final Question controllerQuestion, final HttpServletRequest request, final int nTargetIteration )
    {
        final IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( controllerQuestion.getEntry( ) );
        if ( entryTypeService == null )
        {
            return null;
        }

        FormQuestionResponse controllerResponse = readControllerResponseIteration( entryTypeService, controllerQuestion, request, nTargetIteration );
        if ( controllerResponse == null && nTargetIteration != 0 )
        {
            controllerResponse = readControllerResponseIteration( entryTypeService, controllerQuestion, request, 0 );
        }
        return controllerResponse;
    }

    /**
     * Reads a controlling question's submitted response iteration.
     *
     * @param entryTypeService
     *            the entry type service of the controlling question's entry
     * @param controllerQuestion
     *            the controlling question
     * @param request
     *            the HTTP request being submitted
     * @param nIteration
     *            the iteration to read the controller at
     * @return the response if it holds a submitted value at this iteration, {@code null} otherwise
     */
    private FormQuestionResponse readControllerResponseIteration(final IEntryTypeService entryTypeService, final Question controllerQuestion,
                                                                 final HttpServletRequest request, final int nIteration )
    {
        final HttpServletRequest iterationRequest = new IterationHttpServletRequestWrapper( request, nIteration );
        final List<Response> listResponse = new ArrayList<>( );
        entryTypeService.getResponseData( controllerQuestion.getEntry( ), iterationRequest, listResponse, request.getLocale( ) );

        if ( !this.hasSubmittedValue( listResponse ) )
        {
            return null;
        }
        final Question controllerAtIteration = new Question( controllerQuestion );
        controllerAtIteration.setIterationNumber( nIteration );
        final FormQuestionResponse response = new FormQuestionResponse( );
        response.setQuestion( controllerAtIteration );
        response.setEntryResponse( listResponse );
        return response;
    }

    /**
     * Check if there is submitted value(s) in a list of responses.
     *
     * @param listResponse
     *            the responses read from the request
     * @return {@code true} if at least one response holds a value
     */
    private boolean hasSubmittedValue( final List<Response> listResponse )
    {
        return !CollectionUtils.isEmpty( listResponse ) && listResponse.stream( )
                .anyMatch(response -> ( response.getField( ) != null && response.getField( ).getIdField() > 0 )
                                                || StringUtils.isNotBlank( response.getResponseValue( ) )
                                                || response.getFile( ) != null );
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Question> expandWithConditionalTargetQuestions( List<Question> listQuestion )
    {
        final Map<Integer, Question> mapResult = new LinkedHashMap<>( );
        listQuestion.forEach( q -> mapResult.put( q.getId( ), q ) );

        // handle potential recursive controlling
        final Deque<Question> toProcess = new ArrayDeque<>( listQuestion );

        while ( !toProcess.isEmpty( ) )
        {
            final Question question = toProcess.poll( );

            for ( final Control control : ControlHome.getControlByQuestion( question.getId( ) ) )
            {
                if ( !ControlType.CONDITIONAL.getLabel( ).equals( control.getControlType( ) ) )
                {
                    continue;
                }
                for ( final Question target : resolveTargetQuestions( control.getIdControlTarget( ) ) )
                {
                    if ( mapResult.putIfAbsent( target.getId( ), target ) == null )
                    {
                        toProcess.add( target );
                    }
                }
            }
        }
        return new ArrayList<>( mapResult.values( ) );
    }

    /**
     * Recursively find questions in target's children
     *
     * @param nIdControlTarget
     *            the id of the target FormDisplay
     * @return the List of Question found under that target, possibly empty
     */
    private List<Question> resolveTargetQuestions( final int nIdControlTarget )
    {
        final List<Question> listResult = new ArrayList<>( );
        final FormDisplay formDisplay = FormDisplayHome.findByPrimaryKey( nIdControlTarget );
        if ( formDisplay == null )
        {
            return listResult;
        }

        if ( CompositeDisplayType.QUESTION.getLabel( ).equals( formDisplay.getCompositeType( ) ) )
        {
            final Question question = QuestionHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
            if ( question != null )
            {
                question.setIterationNumber( DEFAULT_ITERATION_NUMBER );
                listResult.add( question );
            }
        }
        else
        {
            for ( final FormDisplay child : FormDisplayHome.getFormDisplayListByParent( formDisplay.getStepId( ), formDisplay.getId( ) ) )
            {
                if ( CompositeDisplayType.QUESTION.getLabel( ).equals( child.getCompositeType( ) ) )
                {
                    final Question question = QuestionHome.findByPrimaryKey( child.getCompositeId( ) );
                    if ( question != null )
                    {
                        question.setIterationNumber( DEFAULT_ITERATION_NUMBER );
                        listResult.add( question );
                    }
                }
                else
                {
                    listResult.addAll( resolveTargetQuestions( child.getId( ) ) );
                }
            }
        }
        return listResult;
    }

    /**
     * {@inheritDoc}
     * <p>
     */
    @Override
    public boolean isConditionalTarget( final int nIdForm, final Question question )
    {
        final FormDisplay formDisplay = FormDisplayHome.getFormDisplayByFormStepAndComposite( nIdForm, question.getIdStep( ), question.getId( ) );
        return formDisplay != null && formDisplay.getDisplayControl( ) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Question> expandWithSubmittedIterations( final HttpServletRequest request, final List<Question> listQuestions )
    {
        final List<Question> listExpanded = new ArrayList<>( );

        for ( final Question question : listQuestions )
        {
            final int nIdEntry = question.getEntry( ).getIdEntry( );
            final Pattern iterationPattern = Pattern.compile( "^" + IEntryTypeService.PREFIX_ITERATION_ATTRIBUTE + "(\\d+)_" + IEntryTypeService.PREFIX_ATTRIBUTE + nIdEntry + "(?:_.*)?$" );
            final TreeSet<Integer> setIterations = new TreeSet<>( );
            final Enumeration<String> paramNames = request.getParameterNames( );
            while ( paramNames.hasMoreElements( ) )
            {
                final String strParam = paramNames.nextElement();
                final Matcher iterationMatcher = iterationPattern.matcher(strParam);
                if (  iterationMatcher.matches( ) )
                {
                    final String strIndex = iterationMatcher.group( 1 );
                    if ( StringUtils.isNumeric( strIndex ) )
                    {
                        setIterations.add( Integer.parseInt( strIndex ) );
                    }
                }
            }

            if ( setIterations.isEmpty( ) )
            {
                if ( question.getIterationNumber( ) == DEFAULT_ITERATION_NUMBER )
                {
                    final Question questionIteration = new Question( question );
                    questionIteration.setIterationNumber( 0 );
                    listExpanded.add( questionIteration );
                }
                else
                {
                    listExpanded.add( question );
                }
            }
            else
            {
                for ( final Integer nIteration : setIterations )
                {
                    final Question questionIteration = new Question( question );
                    questionIteration.setIterationNumber( nIteration );
                    listExpanded.add( questionIteration );
                }
            }
        }

        return listExpanded;
    }

    /**
     * Merge submitted responses into existing form resonse
     * @param formResponse
     *           the form response to be merged
     * @param listSubmitted
     *           the submitted reponses to merge
     */
    private void mergeSubmittedResponsesIntoFormResponse( final FormResponse formResponse, final List<FormQuestionResponse> listSubmitted )
    {
        if ( CollectionUtils.isEmpty( listSubmitted ) || formResponse == null || CollectionUtils.isEmpty( formResponse.getSteps( ) ) )
        {
            return;
        }

        final Set<Integer> editedQuestionIds = listSubmitted.stream( ).map( s -> s.getQuestion( ).getId( ) ).collect( Collectors.toSet( ) );

        for ( final FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            final int nIdStep = formResponseStep.getStep( ).getId( );
            final List<FormQuestionResponse> listMerged = new ArrayList<>( );

            for ( final FormQuestionResponse existing : formResponseStep.getQuestions( ) )
            {
                if ( !editedQuestionIds.contains( existing.getQuestion( ).getId( ) ) )
                {
                    listMerged.add( existing );
                }
            }

            for ( final FormQuestionResponse submitted : listSubmitted )
            {
                if ( submitted.getQuestion( ).getIdStep( ) == nIdStep )
                {
                    listMerged.add( submitted );
                }
            }

            formResponseStep.setQuestions( listMerged );
        }
    }
}
