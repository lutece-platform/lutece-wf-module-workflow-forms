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
package fr.paris.lutece.plugins.workflow.modules.forms.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.template.HtmlMarkup;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Renders the task forms of workflow-forms (task_edit_form_response_form.html, back office and front office) with HTML
 * auto-escaping, as the task components and the XPages build their model (LUT-33153): the steps the forms step display
 * tree renders are marked with HtmlMarkup and keep their tags, and a step left as a String would print as text. Only the
 * FreeMarker engine is needed: the core macros and the asynchronousupload includes are stubbed.
 */
public class WorkflowFormsHtmlFragmentsRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_TASK_FORM_BO = "admin/plugins/workflow/modules/forms/task_edit_form_response_form.html";
    private static final String TEMPLATE_TASK_FORM_FO = "skin/plugins/workflow/modules/forms/task_edit_form_response_form.html";
    private static final String MARK_STEP_LIST = "list_step";
    private static final String STUBS = "stubs.ftl";
    private static final String STEP = "<div class=\"step\"><label for=\"q_1\">Nom</label><input id=\"q_1\" name=\"q_1\" value=\"a &lt;b&gt; &quot;c&quot;\"></div>";

    /**
     * Renders a template with the core macros and the upload includes stubbed.
     *
     * @param strTemplate
     *            the template path
     * @param listSteps
     *            the steps
     * @return the HTML
     * @throws IOException
     *             if the template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strTemplate, List<?> listSteps ) throws IOException, TemplateException
    {
        StringTemplateLoader stubs = new StringTemplateLoader( );
        stubs.putTemplate( STUBS, "<#macro cTpl><#nested></#macro>" );
        stubs.putTemplate( "admin/plugins/asynchronousupload/upload_commons.html", "<#macro addRequiredBOJsFiles></#macro>" );
        stubs.putTemplate( "skin/plugins/asynchronousupload/upload_commons.html", "<#macro addRequiredJsFiles></#macro>" );

        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                stubs, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( STUBS );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( Map.of( MARK_STEP_LIST, listSteps ), writer );

        return writer.toString( );
    }

    /**
     * The steps marked as markup are written as they are, in the back office and front office task forms.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMarkedStepsKeepTheirTags( ) throws Exception
    {
        for ( String strTemplate : new String [ ] {
                TEMPLATE_TASK_FORM_BO, TEMPLATE_TASK_FORM_FO
        } )
        {
            String strHtml = render( strTemplate, HtmlMarkup.ofAll( List.of( STEP, STEP ) ) );

            assertTrue( strHtml.contains( STEP ), strHtml );
            assertFalse( strHtml.contains( "&lt;div" ), strHtml );
        }
    }

    /**
     * A step left as a String is escaped: the reason the components and the XPages mark it.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAStringStepIsEscaped( ) throws Exception
    {
        String strHtml = render( TEMPLATE_TASK_FORM_BO, List.of( STEP ) );

        assertTrue( strHtml.contains( "&lt;div class=&quot;step&quot;&gt;" ), strHtml );
    }
}
