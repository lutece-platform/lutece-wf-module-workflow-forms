/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;

/**
 * This class is a service for the task {@link EditFormResponseTask}
 *
 */
public class EditFormResponseTaskService implements IEditFormResponseTaskService
{
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Question> findQuestionsToEdit( FormResponse formResponse )
    {
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );

        List<Question> listFormQuestion = QuestionHome.getListQuestionByIdForm( form.getId( ) );
        listFormQuestion = listFormQuestion.stream( ).filter( question -> question.getEntry( ).isEditableBack( ) == true ).collect( Collectors.toList( ) );
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
            List<FormQuestionResponse> listFormQuestionResponse = findResponses( formResponse, question );

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

    /**
     * {@inheritDoc}
     */
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
}
