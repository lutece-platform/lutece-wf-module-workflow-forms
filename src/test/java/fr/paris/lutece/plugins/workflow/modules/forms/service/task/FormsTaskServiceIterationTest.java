/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

public class FormsTaskServiceIterationTest
{
    private static final int ID_ENTRY = 15;

    private static String iterationParam( int nIteration )
    {
        return IEntryTypeService.PREFIX_ITERATION_ATTRIBUTE + nIteration + "_" + IEntryTypeService.PREFIX_ATTRIBUTE + ID_ENTRY;
    }

    @Test
    public void testExpandDetectsAllSubmittedIterations( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( iterationParam( 0 ), "value-0" );
        request.addParameter( iterationParam( 2 ), "value-2" );
        request.addParameter( iterationParam( 1 ) + "_7", "value-1-field-7" ); // field-suffixed variant
        request.addParameter( "nIt0_" + IEntryTypeService.PREFIX_ATTRIBUTE + 999, "other-entry" ); // noise
        request.addParameter( "url_return", "somewhere" ); // noise

        List<Question> expanded = new FormsTaskService( ).expandWithSubmittedIterations( request, Collections.singletonList( question( ) ) );

        List<Integer> iterations = expanded.stream( ).map( Question::getIterationNumber ).sorted( ).collect( Collectors.toList( ) );
        assertEquals( List.of( 0, 1, 2 ), iterations, "the three submitted iterations (0, 1, 2) must all be resolved" );
    }

    @Test
    public void testExpandFallsBackToIterationZeroWhenNothingSubmitted( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "url_return", "somewhere" );

        List<Question> expanded = new FormsTaskService( ).expandWithSubmittedIterations( request, Collections.singletonList( question( ) ) );

        assertEquals( 1, expanded.size( ), "a non-iterated question with no submitted value yields one iteration" );
        assertEquals( 0, expanded.get( 0 ).getIterationNumber( ), "that single iteration is iteration 0" );
    }

    private static Question question( )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( ID_ENTRY );

        Question question = new Question( );
        question.setEntry( entry );
        question.setIterationNumber( -1 ); // DEFAULT_ITERATION_NUMBER : "not iterated yet"
        return question;
    }
}
