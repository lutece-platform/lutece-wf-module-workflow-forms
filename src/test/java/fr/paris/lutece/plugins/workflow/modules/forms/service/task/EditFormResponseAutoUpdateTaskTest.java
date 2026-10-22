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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseConfigValue;

/**
 * Field matching of the task that edits a response automatically
 */
public class EditFormResponseAutoUpdateTaskTest
{
    /**
     * The fields without value of an entry (a text entry has configuration fields without value) are skipped instead of failing the task
     */
    @Test
    public void testFieldsWithoutValueAreSkipped( )
    {
        Field fieldWithoutValue = new Field( );
        Field fieldYes = new Field( );
        fieldYes.setValue( "Oui" );

        assertSame( fieldYes, EditFormResponseAutoUpdateTask.findFieldByValue( List.of( fieldWithoutValue, fieldYes ), "Oui" ) );
        assertNull( EditFormResponseAutoUpdateTask.findFieldByValue( List.of( fieldWithoutValue ), "texte libre" ) );
        assertNull( EditFormResponseAutoUpdateTask.findFieldByValue( null, "Oui" ) );
    }

    /**
     * With several configured values, each one finds the response of its own question in all the responses of the form response, whatever the values
     * processed before: the second question is found instead of a new empty response being created
     */
    @Test
    public void testEachConfiguredValueFindsItsQuestion( )
    {
        FormQuestionResponse responseFirst = newQuestionResponse( 1, "first" );
        FormQuestionResponse responseSecond = newQuestionResponse( 2, "second" );
        List<FormQuestionResponse> listResponses = List.of( responseFirst, responseSecond );

        assertSame( responseFirst, EditFormResponseAutoUpdateTask.findQuestionResponse( listResponses, newConfigValue( responseFirst ), false ) );
        assertSame( responseSecond, EditFormResponseAutoUpdateTask.findQuestionResponse( listResponses, newConfigValue( responseSecond ), false ) );
        assertSame( responseFirst, EditFormResponseAutoUpdateTask.findQuestionResponse( listResponses, newConfigValue( responseFirst ), true ) );
        assertSame( responseSecond, EditFormResponseAutoUpdateTask.findQuestionResponse( listResponses, newConfigValue( responseSecond ), true ) );
        assertNull( EditFormResponseAutoUpdateTask.findQuestionResponse( List.of( responseFirst ), newConfigValue( responseSecond ), false ) );
    }

    /**
     * Builds the response of a question
     *
     * @param nIdQuestion
     *            the question id
     * @param strCode
     *            the question code
     * @return the question response
     */
    private static FormQuestionResponse newQuestionResponse( int nIdQuestion, String strCode )
    {
        Question question = new Question( );
        question.setId( nIdQuestion );
        question.setCode( strCode );
        FormQuestionResponse questionResponse = new FormQuestionResponse( );
        questionResponse.setQuestion( question );
        return questionResponse;
    }

    /**
     * Builds a configured value targeting the question of a response
     *
     * @param questionResponse
     *            the question response
     * @return the configured value
     */
    private static EditFormResponseConfigValue newConfigValue( FormQuestionResponse questionResponse )
    {
        EditFormResponseConfigValue configValue = new EditFormResponseConfigValue( );
        configValue.setQuestion( questionResponse.getQuestion( ) );
        configValue.setCode( questionResponse.getQuestion( ).getCode( ) );
        return configValue;
    }
}
