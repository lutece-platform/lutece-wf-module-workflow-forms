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
package fr.paris.lutece.plugins.workflow.modules.forms.service.provider;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Without base url, the url of the task is left empty instead of failing the action that uses its markers
 */
public class CompleteFormResponseTaskInfoProviderTest extends LuteceTestCase
{
    private static class TestProvider extends AbstractCompleteFormResponseTaskInfoProvider
    {
        @Override
        public String getPageName( )
        {
            return "test";
        }

        @Override
        protected String getInfoEntries( int nIdHistory )
        {
            return "entries";
        }

        @Override
        protected String getInfoMessage( int nIdHistory, int nIdTask )
        {
            return "message";
        }

        @Override
        protected String getMarkUrl( )
        {
            return "url";
        }

        @Override
        protected String getMarkMsg( )
        {
            return "msg";
        }

        @Override
        protected String getMarkEntries( )
        {
            return "entries";
        }
    }

    @Test
    public void testNoBaseUrlLeavesTheUrlEmpty( ) throws Exception
    {
        // the base url is read from the properties (no request), both empty in the test configuration
        assumeTrue( StringUtils.isBlank( AppPropertiesService.getProperty( "lutece.base.url" ) )
                && StringUtils.isBlank( AppPropertiesService.getProperty( "lutece.prod.url" ) ) );

        TestProvider provider = new TestProvider( );
        IResourceHistoryService resourceHistoryService = (IResourceHistoryService) Proxy.newProxyInstance( getClass( ).getClassLoader( ), new Class<?> [ ] {
                IResourceHistoryService.class
        }, ( proxy, method, args ) -> "findByPrimaryKey".equals( method.getName( ) ) ? new ResourceHistory( ) : null );
        Field field = AbstractCompleteFormResponseTaskInfoProvider.class.getDeclaredField( "_resourceHistoryService" );
        field.setAccessible( true );
        field.set( provider, resourceHistoryService );

        String strJson = provider.getTaskResourceInfo( 1, 2, null );

        JsonNode json = new ObjectMapper( ).readTree( strJson );
        assertEquals( "", json.get( "url" ).asText( ) );
        assertEquals( "message", json.get( "msg" ).asText( ) );
        assertEquals( "entries", json.get( "entries" ).asText( ) );
    }
}
