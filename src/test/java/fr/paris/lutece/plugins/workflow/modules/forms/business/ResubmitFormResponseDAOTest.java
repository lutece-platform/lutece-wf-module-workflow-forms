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

import java.sql.Timestamp;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class ResubmitFormResponseDAOTest extends LuteceTestCase
{
    private static final int ID_HISTORY = 9702;
    private static final int ID_TASK = 36;

    @Inject
    @Named( "worklow-forms.resubmitFormResponseDAO" )
    private ResubmitFormResponseDAO _dao;

    public void testMarkAsCompleteOnlyOnce( )
    {
        Plugin plugin = WorkflowUtils.getPlugin( );
        _dao.deleteByIdHistory( ID_HISTORY, ID_TASK, plugin );

        ResubmitFormResponse response = new ResubmitFormResponse( );
        response.setIdHistory( ID_HISTORY );
        response.setIdTask( ID_TASK );
        response.setMessage( "message" );
        response.setIsComplete( false );
        _dao.insert( response, plugin );

        Timestamp now = new Timestamp( System.currentTimeMillis( ) );

        // the first claim wins
        assertTrue( _dao.markAsComplete( ID_HISTORY, ID_TASK, now, plugin ) );
        ResubmitFormResponse loaded = _dao.load( ID_HISTORY, ID_TASK, plugin );
        assertTrue( loaded.isComplete( ) );
        assertNotNull( loaded.getDateCompleted( ) );

        // a second claim of the same response fails
        assertFalse( _dao.markAsComplete( ID_HISTORY, ID_TASK, now, plugin ) );

        // an unknown response cannot be claimed
        assertFalse( _dao.markAsComplete( ID_HISTORY, ID_TASK + 1, now, plugin ) );

        // once reopened, the response can be claimed again
        _dao.reopen( ID_HISTORY, ID_TASK, plugin );
        loaded = _dao.load( ID_HISTORY, ID_TASK, plugin );
        assertFalse( loaded.isComplete( ) );
        assertNull( loaded.getDateCompleted( ) );
        assertTrue( _dao.markAsComplete( ID_HISTORY, ID_TASK, now, plugin ) );

        _dao.deleteByIdHistory( ID_HISTORY, ID_TASK, plugin );
        assertNull( _dao.load( ID_HISTORY, ID_TASK, plugin ) );
    }
}
