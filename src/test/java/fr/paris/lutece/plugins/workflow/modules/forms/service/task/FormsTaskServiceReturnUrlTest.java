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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The return URL after a form saved from a workflow task leads only to the site.
 */
public class FormsTaskServiceReturnUrlTest
{
    private static final String BASE_URL = "https://site.example.org:8443/lutece/";

    @Test
    public void testUrlsOfTheSiteAreAccepted( )
    {
        assertTrue( FormsTaskService.isSiteReturnUrl( "jsp/site/Portal.jsp?page=forms", BASE_URL ) );
        assertTrue( FormsTaskService.isSiteReturnUrl( "/lutece/jsp/site/Portal.jsp", BASE_URL ) );
        assertTrue( FormsTaskService.isSiteReturnUrl( "Portal.jsp?page=x&url=http://other.example.org", BASE_URL ) );
        assertTrue( FormsTaskService.isSiteReturnUrl( "https://site.example.org:8443/lutece/jsp/site/Portal.jsp", BASE_URL ) );
        assertTrue( FormsTaskService.isSiteReturnUrl( "HTTPS://SITE.example.org:8443", BASE_URL ) );
    }

    @Test
    public void testOtherUrlsAreRefused( )
    {
        assertFalse( FormsTaskService.isSiteReturnUrl( null, BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( " ", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "https://other.example.org/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "https://site.example.org/lutece/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "https://site.example.org:8443@other.example.org/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "https://site.example.org:8443.other.example.org/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "//other.example.org/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "\\\\other.example.org/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "/\\other.example.org/", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "javascript:alert(1)", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "java\tscript:alert(1)", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "data:text/html,x", BASE_URL ) );
        assertFalse( FormsTaskService.isSiteReturnUrl( "https://site.example.org:8443/", null ) );
    }
}
