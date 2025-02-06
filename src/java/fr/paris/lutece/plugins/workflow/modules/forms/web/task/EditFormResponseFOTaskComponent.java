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
package fr.paris.lutece.plugins.workflow.modules.forms.web.task;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskHistoryService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IEditFormResponseTaskService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * This class represents a component for the task {@link fr.paris.lutece.plugins.workflow.modules.forms.service.task.EditFormResponseTask EditFormResponseTask}
 *
 */
public class EditFormResponseFOTaskComponent extends EditFormResponseTaskComponent
{
    /**
     * Constructor
     *
     * @param formsTaskService
     *            the form task service
     * @param editFormResponseTaskService
     *            the edit form response task service
     */
    @Inject
    public EditFormResponseFOTaskComponent( IFormsTaskService formsTaskService, IEditFormResponseTaskService editFormResponseTaskService,
            IEditFormResponseTaskHistoryService editFormResponseTaskHistoryService )
    {
        super( formsTaskService, editFormResponseTaskService, editFormResponseTaskHistoryService );
    }

    @Override
    protected String createTemplateTaskForm( HttpServletRequest request, Locale locale, FormResponse formResponse, List<Step> listStep,
            List<Question> listQuestion )
    {
        List<String> listStepDisplayTree = _formsTaskService.buildFormStepDisplayTreeList( request, listStep, listQuestion, formResponse,
                DisplayType.EDITION_BACKOFFICE );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_STEP_LIST, listStepDisplayTree );

        return AppTemplateService.getTemplate( TEMPLATE_TASK_FORM, locale, model ).getHtml( );
    }

    @Override
    protected boolean isTaskBo( )
    {
        return false;
    }
}
