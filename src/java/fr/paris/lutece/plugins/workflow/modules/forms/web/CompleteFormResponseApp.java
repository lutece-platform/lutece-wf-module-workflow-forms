/*
 * Copyright (c) 2002-2021, City of Paris
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.service.ICompleteFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.signrequest.CompleteFormResponseRequestAuthenticatorService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

public class CompleteFormResponseApp implements XPageApplication
{
    private static final long serialVersionUID = -6753642997148910492L;

    // TEMPLATES
    private static final String TEMPLATE_COMPLETE_FORM = "skin/plugins/workflow/modules/forms/complete_form.html";

    // ACTIONS
    private static final String ACTION_DO_MODIFY_RESPONSE = "do_modify_response";

    // MESSAGES
    private static final String MESSAGE_RECORD_ALREADY_COMPLETED = "module.workflow.forms.message.response_already_completed";
    private static final String PROPERTY_XPAGE_RESUBMIT_FORM_PAGETITLE = "module.workflow.forms.resubmit_form.page_title";
    private static final String PROPERTY_XPAGE_RESUBMIT_FORM_PATHLABEL = "module.workflow.forms.resubmit_form.page_label";
    private static final String MESSAGE_EDITION_COMPLETE = "module.workflow.forms.message.edition_complete";

    // PARAMETERS
    private static final String PARAMETER_URL_RETURN = "url_return";
    private static final String PARAMETER_ID_HISTORY = "id_history";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_SIGNATURE = "signature";
    private static final String PARAMETER_TIMESTAMP = "timestamp";
    private static final String PARAMETER_ACTION = "action";

    // MARKS
    private static final String MARK_STEP_LIST = "list_step";
    private static final String MARK_COMPLETE_FORM = "complete_form";
    private static final String MARK_ID_RESPONSE = "id_response";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_URL_RETURN = "url_return";
    private static final String MARK_SIGNATURE = "signature";
    private static final String MARK_TIMESTAMP = "timestamp";

    // SERVICES
    private ICompleteFormResponseService _completeFormResponseService = SpringContextService.getBean( "workflow-forms.taskCompleteResponseService" );

    @Inject
    private IFormsTaskService _formsTaskService = SpringContextService.getBean( "workflow-forms.formsTaskService" );

    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException, SiteMessageException
    {
        XPage page = null;
        if ( !CompleteFormResponseRequestAuthenticatorService.getRequestAuthenticator( ).isRequestAuthenticated( request ) )
        {
            // Throws Exception
            _formsTaskService.setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );
            return null;
        }
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );

        if ( StringUtils.isNumeric( strIdHistory ) && StringUtils.isNumeric( strIdTask ) )
        {
            int nIdHistory = Integer.parseInt( strIdHistory );
            int nIdTask = Integer.parseInt( strIdTask );

            CompleteFormResponse completeFormResponse = _completeFormResponseService.find( nIdHistory, nIdTask );
            if ( completeFormResponse != null && !completeFormResponse.isComplete( ) )
            {
                if ( _completeFormResponseService.isRecordStateValid( completeFormResponse, request.getLocale( ) ) )
                {
                    doAction( request, completeFormResponse, nIdTask, nIdHistory );
                    page = getCompleteFormResponsePage( request, completeFormResponse );
                }
                else
                {
                    _formsTaskService.setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP,
                            request.getParameter( PARAMETER_URL_RETURN ) );
                }
            }
            else
            {
                _formsTaskService.setSiteMessage( request, MESSAGE_RECORD_ALREADY_COMPLETED, SiteMessage.TYPE_INFO,
                        request.getParameter( PARAMETER_URL_RETURN ) );
            }
        }
        else
        {
            _formsTaskService.setSiteMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );
        }

        return page;
    }

    /**
     * Get the CompleteFormResponse page
     * 
     * @param request
     *            the HTTP request
     * @param CompleteFormResponse
     *            the CompleteFormResponse
     * @return a XPage
     */
    private XPage getCompleteFormResponsePage( HttpServletRequest request, CompleteFormResponse completeFormResponse )
    {
        XPage page = new XPage( );

        FormResponse formResponse = _formsTaskService.getFormResponseFromIdHistory( completeFormResponse.getIdHistory( ) );
        List<Question> listQuestions = _completeFormResponseService.getListQuestionToEdit( formResponse, completeFormResponse.getListCompleteReponseValues( ) );

        List<Step> listStep = listQuestions.stream( ).map( Question::getStep ).map( Step::getId ).distinct( ).map( StepHome::findByPrimaryKey )
                .collect( Collectors.toList( ) );

        List<String> listStepDisplayTree = _formsTaskService.buildFormStepDisplayTreeList( request, listStep, listQuestions, formResponse,
                DisplayType.COMPLETE_FRONTOFFICE );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_COMPLETE_FORM, completeFormResponse );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, request.getLocale( ) );
        model.put( MARK_URL_RETURN, request.getParameter( PARAMETER_URL_RETURN ) );
        model.put( MARK_SIGNATURE, request.getParameter( PARAMETER_SIGNATURE ) );
        model.put( MARK_TIMESTAMP, request.getParameter( PARAMETER_TIMESTAMP ) );
        model.put( MARK_STEP_LIST, listStepDisplayTree );

        if ( formResponse != null )
        {
            model.put( MARK_ID_RESPONSE, formResponse.getId( ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_COMPLETE_FORM, request.getLocale( ), model );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_RESUBMIT_FORM_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_RESUBMIT_FORM_PATHLABEL, request.getLocale( ) ) );
        page.setContent( template.getHtml( ) );

        return page;
    }

    private void doAction( HttpServletRequest request, CompleteFormResponse completeFormResponse, int idTask, int idHistory ) throws SiteMessageException
    {
        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( StringUtils.isBlank( strAction ) )
        {
            return;
        }

        if ( ACTION_DO_MODIFY_RESPONSE.equals( strAction ) && doEditResponse( request, completeFormResponse, idTask, idHistory ) )
        {
            // Back to home page
            String strUrlReturn = request.getParameter( PARAMETER_URL_RETURN );
            strUrlReturn = StringUtils.isNotBlank( strUrlReturn ) ? strUrlReturn : AppPathService.getBaseUrl( request );
            _formsTaskService.setSiteMessage( request, MESSAGE_EDITION_COMPLETE, SiteMessage.TYPE_INFO, strUrlReturn );
        }
    }

    /**
     * Do edit a response
     * 
     * @param request
     *            the HTTP request
     * @param response
     *            the response
     * @return true if the record must be updated, false otherwise
     * @throws SiteMessageException
     *             a site message if there is a problem
     */
    private boolean doEditResponse( HttpServletRequest request, CompleteFormResponse response, int idTask, int idHistory ) throws SiteMessageException
    {
        if ( _completeFormResponseService.isRecordStateValid( response, request.getLocale( ) ) )
        {
            if ( _completeFormResponseService.doEditResponseData( request, response, idTask, idHistory ) )
            {
                _completeFormResponseService.doChangeResponseState( response, request.getLocale( ) );

                _completeFormResponseService.doCompleteResponse( response );

                return true;
            }
            return false;
        }
        else
        {
            _formsTaskService.setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );
        }
        return false;
    }
}
