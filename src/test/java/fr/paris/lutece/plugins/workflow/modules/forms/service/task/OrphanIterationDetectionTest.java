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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;

public class OrphanIterationDetectionTest
{
    private static final int ID_QUESTION = 100;
    private static final int ID_OTHER_QUESTION = 200;

    private final FormsTaskService formsTaskService = new FormsTaskService();

    /**
     * The group had 3 children (iterations 0, 1, 2), the user removed the last one : only the responses of
     * iteration 2 must be flagged for removal.
     */
    @Test
    public void testRemovedLastIterationIsOrphan( )
    {
        FormResponse formResponse = formResponseWith( response( ID_QUESTION, 0 ), response( ID_QUESTION, 1 ), response( ID_QUESTION, 2 ) );

        List<FormQuestionResponse> orphans = formsTaskService.findOrphanIterationResponses( formResponse,
                Arrays.asList( submitted( ID_QUESTION, 0 ), submitted( ID_QUESTION, 1 ) ) );

        assertEquals( 1, orphans.size( ), "only the surplus iteration is orphan" );
        assertEquals( ID_QUESTION, orphans.get( 0 ).getQuestion( ).getId( ) );
        assertEquals( 2, orphans.get( 0 ).getQuestion( ).getIterationNumber( ), "the removed iteration is 2" );
    }

    /**
     * When every persisted iteration is still submitted, nothing is orphan.
     */
    @Test
    public void testNoIterationRemovedYieldsNoOrphan( )
    {
        FormResponse formResponse = formResponseWith( response( ID_QUESTION, 0 ), response( ID_QUESTION, 1 ), response( ID_QUESTION, 2 ) );

        List<FormQuestionResponse> orphans = formsTaskService.findOrphanIterationResponses( formResponse,
                Arrays.asList( submitted( ID_QUESTION, 0 ), submitted( ID_QUESTION, 1 ), submitted( ID_QUESTION, 2 ) ) );

        assertTrue( orphans.isEmpty( ), "no iteration removed ⇒ no orphan" );
    }

    /**
     * A question absent from the submitted responses must not have any of its iterations reclaimed : only the
     * questions actually edited on this request are affected.
     */
    @Test
    public void testUnsubmittedQuestionIsLeftUntouched( )
    {
        FormResponse formResponse = formResponseWith( response( ID_QUESTION, 0 ), response( ID_QUESTION, 1 ), response( ID_QUESTION, 2 ),
                response( ID_OTHER_QUESTION, 0 ), response( ID_OTHER_QUESTION, 1 ) );

        // only ID_QUESTION is edited, kept up to iteration 1 ; ID_OTHER_QUESTION is not submitted at all
        List<FormQuestionResponse> orphans = formsTaskService.findOrphanIterationResponses( formResponse,
                Arrays.asList( submitted( ID_QUESTION, 0 ), submitted( ID_QUESTION, 1 ) ) );

        List<Integer> orphanQuestionIds = orphans.stream( ).map( r -> r.getQuestion( ).getId( ) ).distinct( ).collect( Collectors.toList( ) );
        assertEquals( Collections.singletonList( ID_QUESTION ), orphanQuestionIds, "the unsubmitted question is never touched" );
        assertEquals( 1, orphans.size( ), "only iteration 2 of the edited question is orphan" );
        assertEquals( 2, orphans.get( 0 ).getQuestion( ).getIterationNumber( ) );
    }

    private static FormResponse formResponseWith( FormQuestionResponse... responses )
    {
        FormResponseStep formResponseStep = new FormResponseStep( );
        formResponseStep.setQuestions( new ArrayList<>( Arrays.asList( responses ) ) );

        FormResponse formResponse = new FormResponse( );
        formResponse.setSteps( new ArrayList<>( Collections.singletonList( formResponseStep ) ) );
        return formResponse;
    }

    private static FormQuestionResponse response( int nIdQuestion, int nIteration )
    {
        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        formQuestionResponse.setQuestion( submitted( nIdQuestion, nIteration ) );
        return formQuestionResponse;
    }

    private static Question submitted( int nIdQuestion, int nIteration )
    {
        Question question = new Question( );
        question.setId( nIdQuestion );
        question.setIterationNumber( nIteration );
        return question;
    }
}
