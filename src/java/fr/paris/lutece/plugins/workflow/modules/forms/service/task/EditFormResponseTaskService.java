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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfigValue;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class is a service for the task {@link EditFormResponseTask}
 *
 */
public class EditFormResponseTaskService implements IEditFormResponseTaskService
{

    @Inject
    @Named( "workflow-forms.editFormResponseConfigService" )
    private ITaskConfigService _taskEditFormConfigService;

    @Inject
    private IFormsTaskService _formsTaskService;

    @Inject
    private FormService _formService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Question> findQuestionsToEdit( ITask task, FormResponse formResponse )
    {
        EditFormResponseConfig config = _taskEditFormConfigService.findByPrimaryKey( task.getId( ) );
        List<Question> listFormQuestion = new ArrayList<>( );

        if ( config.isMultiform( ) )
        {
            Set<String> techCodeSet = config.getListConfigValues( ).stream( ).map( EditFormResponseConfigValue::getCode ).collect( Collectors.toSet( ) );

            for ( String codeTechnique : techCodeSet )
            {
                List<Question> questionList = QuestionHome.findByCode( codeTechnique );
                for ( Question question : questionList )
                {
                    Step step = StepHome.findByPrimaryKey( question.getIdStep( ) );
                    if ( step.getIdForm( ) == formResponse.getFormId( ) )
                    {
                        listFormQuestion.add( question );
                    }
                }
            }
        }
        else
        {
            listFormQuestion = config.getListConfigValues( ).stream( ).filter( cv -> cv.getForm( ).getId( ) == formResponse.getFormId( ) )
                    .map( EditFormResponseConfigValue::getQuestion ).collect( Collectors.toList( ) );
        }
        listFormQuestion = addIteration( formResponse, listFormQuestion );
        return listFormQuestion;
    }

    /**
     * Adds the questions for iterations
     * 
     * @param formResponse
     *            the form response containing the iterations
     * @param listQuestion
     *            the list of questions to complete
     * @return the completed list of questions
     */
    private List<Question> addIteration( FormResponse formResponse, List<Question> listQuestion )
    {
        List<Question> listQuestionIteration = new ArrayList<>( );

        for ( Question question : listQuestion )
        {
            List<FormQuestionResponse> listFormQuestionResponse = _formsTaskService.findResponses( formResponse, question );

            if ( CollectionUtils.isEmpty( listFormQuestionResponse ) )
            {
                listQuestionIteration.add( question );
            }
            else
            {
                for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
                {
                    listQuestionIteration.add( formQuestionResponse.getQuestion( ) );
                }
            }
        }

        return listQuestionIteration;
    }

    @Override
    public void saveResponses( FormResponse formResponse, List<FormQuestionResponse> listFormQuestionResponse )
    {
        persistResponses( formResponse, listFormQuestionResponse );
        fireFormResponseUpdate( formResponse );
    }

    @Override
    public void persistResponses( FormResponse formResponse, List<FormQuestionResponse> listFormQuestionResponse )
    {
        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            saveStep( formQuestionResponse );
            saveResponses( formQuestionResponse );
        }
    }

    @Override
    public void fireFormResponseUpdate( FormResponse formResponse )
    {
        _formService.fireFormResponseEventUpdate( formResponse );
    }

    /**
     * Saves the step associated to the specified form question response
     * 
     * @param formQuestionResponse
     *            the form question response
     */
    private void saveStep( FormQuestionResponse formQuestionResponse )
    {
        List<FormResponseStep> listFormResponseStep = FormResponseStepHome.findStepsByFormResponse( formQuestionResponse.getIdFormResponse( ) );
        boolean bFormResponseStepFound = listFormResponseStep.stream( )
                .anyMatch( formResponseStep -> formResponseStep.getStep( ).getId( ) == formQuestionResponse.getIdStep( ) );

        if ( !bFormResponseStepFound )
        {
            FormResponseStep formResponseStep = new FormResponseStep( );
            formResponseStep.setFormResponseId( formQuestionResponse.getIdFormResponse( ) );
            formResponseStep.setStep( StepHome.findByPrimaryKey( formQuestionResponse.getIdStep( ) ) );
            formResponseStep.setOrder( FormsConstants.ORDER_NOT_SET );

            FormResponseStepHome.create( formResponseStep );
        }
    }

    /**
     * Saves the question associated to the specified form question response
     * 
     * @param formQuestionResponse
     *            the form question response
     */
    private void saveResponses( FormQuestionResponse formQuestionResponse )
    {
        Question question = formQuestionResponse.getQuestion( );
        IEntryDataService dataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
        dataService.save( formQuestionResponse );
    }

    @Override
    public ReferenceList selectAllTechnicalCode( boolean isBo )
    {
        ReferenceList referenceList = new ReferenceList( );
        List<Question> questionList = QuestionHome.getQuestionsList( );

        List<String> codeList = questionList.stream( ).filter( q -> (q.getEntry( ) != null && q.getEntry( ).isOnlyDisplayInBack( ) == isBo) )
        		.map( Question::getCode ).distinct( )
                .collect( Collectors.toList( ) );
        codeList.sort( Comparator.naturalOrder( ) );
        for ( String code : codeList )
        {
            referenceList.addItem( code, code );
        }
        return referenceList;
    }

    /** {0} the answer label, {1} the number of forms producing it, {2} the number of forms carrying the code */
    private static final String PROPERTY_ANSWER_FORM_COUNT = "module.workflow.forms.task.editFormResponse.config.answerFormCount";

    /** {0} the answer label, {1} the number of different labels the forms give this value */
    private static final String PROPERTY_ANSWER_LABEL_CONFLICT = "module.workflow.forms.task.editFormResponse.config.answerLabelConflict";

    @Override
    public ReferenceList getResponseReferenceListByCode( String strCode, Locale locale )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( "", "" );

        Map<String, Set<Integer>> mapFormsByValue = new LinkedHashMap<>( );
        Map<String, Set<String>> mapLabelByValue = new HashMap<>( );
        Set<Integer> setForm = collectAnswersByCode( strCode, mapFormsByValue, mapLabelByValue );

        for ( Map.Entry<String, Set<Integer>> entry : mapFormsByValue.entrySet( ) )
        {
            Set<String> setLabel = mapLabelByValue.getOrDefault( entry.getKey( ), Collections.emptySet( ) );
            String strLabel = setLabel.isEmpty( ) ? entry.getKey( ) : setLabel.iterator( ).next( );

            if ( setLabel.size( ) > 1 )
            {
                // the same value does not mean the same thing everywhere : counting the forms that produce it would say they agree. Whoever
                // configures the task has to go and look, and a label picked among the others would hide that
                Object [ ] argsConflict = {
                        strLabel, setLabel.size( )
                };
                strLabel = I18nService.getLocalizedString( PROPERTY_ANSWER_LABEL_CONFLICT, argsConflict, locale );
            }

            if ( entry.getValue( ).size( ) < setForm.size( ) )
            {
                // only the answers some forms do not produce carry the count : written on every line it reads as decoration, and the one line that
                // matters stops being read
                Object [ ] args = {
                        strLabel, entry.getValue( ).size( ), setForm.size( )
                };
                strLabel = I18nService.getLocalizedString( PROPERTY_ANSWER_FORM_COUNT, args, locale );
            }

            refList.addItem( entry.getKey( ), strLabel );
        }

        return refList;
    }

    @Override
    public int countFormsCarryingCode( String strCode )
    {
        return collectAnswersByCode( strCode, new LinkedHashMap<>( ), new HashMap<>( ) ).size( );
    }

    /**
     * Walks the questions carrying a code and gathers their answer choices by value.
     * 
     * The value is the key, not the field identifier : each form has its own fields, and the runtime matches the saved response on the value. Two forms
     * offering the same value therefore count as two forms producing the same answer, which is the whole point of the count.
     * 
     * @param strCode
     *            the technical code
     * @param mapFormsByValue
     *            filled with, per answer value, the forms producing it, in encounter order
     * @param mapLabelByValue
     *            filled with, per answer value, a label to show
     * @return the forms carrying the code
     */
    private Set<Integer> collectAnswersByCode( String strCode, Map<String, Set<Integer>> mapFormsByValue, Map<String, Set<String>> mapLabelByValue )
    {
        Set<Integer> setForm = new HashSet<>( );

        for ( Question question : QuestionHome.findByCode( strCode ) )
        {
            Step step = StepHome.findByPrimaryKey( question.getIdStep( ) );

            if ( step == null || question.getEntry( ) == null || question.getEntry( ).getFields( ) == null )
            {
                // a question whose step or entry is gone says nothing about the forms producing a value
                AppLogService.error( "Workflow forms : the question {} carrying the code {} cannot be read, it is left out of the answer list",
                        question.getId( ), strCode );

                continue;
            }

            setForm.add( step.getIdForm( ) );

            for ( Field field : question.getEntry( ).getFields( ) )
            {
                // genatt_field.value and genatt_field.title are both nullable : a value saying nothing cannot be saved as a response and would
                // collide with the empty item opening the list, and a missing label falls back to the value, which at least names the answer
                if ( IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) && StringUtils.isNotEmpty( field.getValue( ) ) )
                {
                    mapFormsByValue.computeIfAbsent( field.getValue( ), v -> new HashSet<>( ) ).add( step.getIdForm( ) );
                    // every label this value is given, not the first one met : keeping only one would show agreement where the forms disagree
                    mapLabelByValue.computeIfAbsent( field.getValue( ), v -> new LinkedHashSet<>( ) )
                            .add( StringUtils.defaultIfEmpty( field.getTitle( ), field.getValue( ) ) );
                }
            }
        }

        return setForm;
    }

    @Override
    public ReferenceList getResponseReferenceList( int idQuestion )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( "", "" );
        if ( idQuestion != -1 )
        {
            Question question = QuestionHome.findByPrimaryKey( idQuestion );
            List<Field> listField = ( question == null || question.getEntry( ) == null ) ? null : question.getEntry( ).getFields( );

            if ( listField == null )
            {
                // an orphan question, left without its generic attribute entry, used to break the whole configuration page
                AppLogService.error( "Workflow forms : the question {} of the configuration has no entry any more, its answer list stays empty", idQuestion );
                return refList;
            }

            for ( Field field : listField )
            {
                if ( IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) && StringUtils.isNotEmpty( field.getValue( ) ) )
                {
                    refList.addItem( field.getValue( ), StringUtils.defaultIfEmpty( field.getTitle( ), field.getValue( ) ) );
                }
            }
        }
        return refList;
    }
}
