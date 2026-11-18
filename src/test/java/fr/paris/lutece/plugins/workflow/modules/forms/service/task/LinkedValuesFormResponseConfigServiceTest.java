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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfigValue;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Copy of the configuration of a linked values task (copy of a task, of an action or of a state)
 */
public class LinkedValuesFormResponseConfigServiceTest extends LuteceTestCase
{
    private static final int ID_TASK = 9101;
    private static final int ID_TASK_COPY = 9102;

    @Inject
    @Named( LinkedValuesFormResponseConfigService.BEAN_NAME )
    private LinkedValuesFormResponseConfigService _service;

    @Test
    public void testCopyConfig( )
    {
        LinkedValuesFormResponseConfig config = new LinkedValuesFormResponseConfig( );
        config.setIdTask( ID_TASK );
        LinkedValuesFormResponseConfigValue value = new LinkedValuesFormResponseConfigValue( );
        value.setIdForm( 1 );
        value.setIdQuestionSource( 2 );
        value.setQuestionSourceValue( "source" );
        value.setIdQuestionTarget( 3 );
        value.setQuestionTargetValue( "target" );
        List<LinkedValuesFormResponseConfigValue> listValues = new ArrayList<>( );
        listValues.add( value );
        config.setListConfigValues( listValues );
        _service.create( config );

        try
        {
            // what the copy of a task does: load the configuration, change the task, create it
            LinkedValuesFormResponseConfig copy = _service.findByPrimaryKey( ID_TASK );
            copy.setIdTask( ID_TASK_COPY );
            _service.create( copy );

            LinkedValuesFormResponseConfig original = _service.findByPrimaryKey( ID_TASK );
            LinkedValuesFormResponseConfig loadedCopy = _service.findByPrimaryKey( ID_TASK_COPY );
            assertNotNull( loadedCopy );
            assertTrue( original.getIdConfig( ) != loadedCopy.getIdConfig( ) );
            assertEquals( 1, original.getListConfigValues( ).size( ) );
            assertEquals( 1, loadedCopy.getListConfigValues( ).size( ) );
            assertEquals( "target", loadedCopy.getListConfigValues( ).get( 0 ).getQuestionTargetValue( ) );
            assertTrue( original.getListConfigValues( ).get( 0 ).getIdConfigValue( ) != loadedCopy.getListConfigValues( ).get( 0 ).getIdConfigValue( ) );
        }
        finally
        {
            _service.remove( ID_TASK );
            _service.remove( ID_TASK_COPY );
        }
    }
}
