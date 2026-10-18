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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * ResubmitFormResponseDAO tests: the claim of a response is one-shot
 */
public class ResubmitFormResponseDAOTest extends LuteceTestCase
{
    private static final int ID_HISTORY = 9101;
    private static final int ID_TASK = 9102;
    private static final int NB_THREADS = 8;

    @Inject
    @Named( "worklow-forms.resubmitFormResponseDAO" )
    private IResubmitFormResponseDAO _dao;

    /**
     * A response can be claimed once, and again after it is released
     */
    public void testClaimRelease( )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );
        _dao.insert( newResubmit( ), plugin );
        try
        {
            assertTrue( _dao.claim( ID_HISTORY, ID_TASK, plugin ) );
            assertFalse( _dao.claim( ID_HISTORY, ID_TASK, plugin ) );
            assertTrue( _dao.load( ID_HISTORY, ID_TASK, plugin ).isComplete( ) );

            _dao.release( ID_HISTORY, ID_TASK, plugin );
            assertFalse( _dao.load( ID_HISTORY, ID_TASK, plugin ).isComplete( ) );
            assertTrue( _dao.claim( ID_HISTORY, ID_TASK, plugin ) );

            // an unknown response cannot be claimed
            assertFalse( _dao.claim( ID_HISTORY + 1, ID_TASK, plugin ) );
        }
        finally
        {
            _dao.deleteByIdHistory( ID_HISTORY, ID_TASK, plugin );
        }
    }

    /**
     * Of several concurrent claims of the same response, only one succeeds
     * 
     * @throws Exception
     *             if a thread fails
     */
    public void testConcurrentClaim( ) throws Exception
    {
        Plugin plugin = WorkflowUtils.getPlugin( );
        _dao.insert( newResubmit( ), plugin );
        ExecutorService executor = Executors.newFixedThreadPool( NB_THREADS );
        try
        {
            List<Callable<Boolean>> listClaims = new ArrayList<>( );
            for ( int i = 0; i < NB_THREADS; i++ )
            {
                listClaims.add( ( ) -> _dao.claim( ID_HISTORY, ID_TASK, plugin ) );
            }
            int nClaimed = 0;
            for ( Future<Boolean> future : executor.invokeAll( listClaims ) )
            {
                if ( Boolean.TRUE.equals( future.get( ) ) )
                {
                    nClaimed++;
                }
            }
            assertEquals( 1, nClaimed );
        }
        finally
        {
            executor.shutdownNow( );
            _dao.deleteByIdHistory( ID_HISTORY, ID_TASK, plugin );
        }
    }

    private ResubmitFormResponse newResubmit( )
    {
        ResubmitFormResponse resubmitFormResponse = new ResubmitFormResponse( );
        resubmitFormResponse.setIdHistory( ID_HISTORY );
        resubmitFormResponse.setIdTask( ID_TASK );
        resubmitFormResponse.setMessage( "message" );
        resubmitFormResponse.setIsComplete( false );
        return resubmitFormResponse;
    }
}
