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
package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class ResubmitFormResponseTaskHistoryDAOTest extends LuteceTestCase
{
    private static final int ID_HISTORY = 9702;
    private static final int ID_TASK = 36;
    private static final int ID_QUESTION = 16;

    @Inject
    @Named( "worklow-forms.resubmitFormResponseTaskHistoryDAO" )
    private ResubmitFormResponseTaskHistoryDAO _dao;

    public void testStoreThenInsert( )
    {
        _dao.deleteByIdHistoryAndTask( ID_HISTORY, ID_TASK );

        ResubmitFormResponseTaskHistory history = newHistory( "first" );

        // no entry yet: store does nothing
        assertFalse( _dao.store( history ) );
        assertEquals( 0, _dao.selectEditFormResponseHistoryByIdHistoryAndIdTask( ID_HISTORY, ID_TASK ).size( ) );

        _dao.insert( history );
        List<ResubmitFormResponseTaskHistory> loaded = _dao.selectEditFormResponseHistoryByIdHistoryAndIdTask( ID_HISTORY, ID_TASK );
        assertEquals( 1, loaded.size( ) );
        assertEquals( "first", loaded.get( 0 ).getNewValue( ) );
        assertEquals( "before", loaded.get( 0 ).getPreviousValue( ) );

        // same history, task, question and iteration: the entry is updated instead of duplicated
        ResubmitFormResponseTaskHistory again = newHistory( "second" );
        assertTrue( _dao.store( again ) );
        loaded = _dao.selectEditFormResponseHistoryByIdHistoryAndIdTask( ID_HISTORY, ID_TASK );
        assertEquals( 1, loaded.size( ) );
        assertEquals( "second", loaded.get( 0 ).getNewValue( ) );
        // the previous value of the first edition is kept
        assertEquals( "before", loaded.get( 0 ).getPreviousValue( ) );

        // another iteration of the same question is another entry
        ResubmitFormResponseTaskHistory otherIteration = newHistory( "third" );
        otherIteration.getQuestion( ).setIterationNumber( 1 );
        assertFalse( _dao.store( otherIteration ) );

        _dao.deleteByIdHistoryAndTask( ID_HISTORY, ID_TASK );
        assertEquals( 0, _dao.selectEditFormResponseHistoryByIdHistoryAndIdTask( ID_HISTORY, ID_TASK ).size( ) );
    }

    private ResubmitFormResponseTaskHistory newHistory( String strNewValue )
    {
        Question question = new Question( );
        question.setId( ID_QUESTION );
        question.setIterationNumber( 0 );

        ResubmitFormResponseTaskHistory history = new ResubmitFormResponseTaskHistory( );
        history.setIdHistory( ID_HISTORY );
        history.setIdTask( ID_TASK );
        history.setQuestion( question );
        history.setPreviousValue( "before" );
        history.setNewValue( strNewValue );
        return history;
    }
}
