/*
 * Copyright (c) 2002-2020, City of Paris
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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.service.IResubmitFormResponseService;
import fr.paris.lutece.plugins.workflow.modules.forms.service.signrequest.ResubmitFormResponseRequestAuthenticatorService;
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

public class ResubmitFormResponseApp implements XPageApplication
{
    private static final long serialVersionUID = -6753642997148910492L;

    // TEMPLATES
    private static final String TEMPLATE_RESUBMIT_FORM = "skin/plugins/workflow/modules/forms/resubmit_form.html";

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
    private static final String MARK_RESUBMIT_FORM = "resubmit_form";
    private static final String MARK_ID_RESPONSE = "id_response";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_URL_RETURN = "url_return";
    private static final String MARK_SIGNATURE = "signature";
    private static final String MARK_TIMESTAMP = "timestamp";

    // SERVICES
    @Inject
    private IResubmitFormResponseService _resubmitFormResponseService = SpringContextService.getBean( "workflow-forms.taskResubmitResponseService" );
    private IFormsTaskService _formsTaskService = SpringContextService.getBean( "workflow-forms.formsTaskService" );

    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException, SiteMessageException
    {
        XPage page = null;
        if ( ResubmitFormResponseRequestAuthenticatorService.getRequestAuthenticator( ).isRequestAuthenticated( request ) )
        {
            String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
            String strIdTask = request.getParameter( PARAMETER_ID_TASK );

            if ( StringUtils.isNotBlank( strIdHistory ) && StringUtils.isNumeric( strIdHistory ) && StringUtils.isNotBlank( strIdTask )
                    && StringUtils.isNumeric( strIdTask ) )
            {
                int nIdHistory = Integer.parseInt( strIdHistory );
                int nIdTask = Integer.parseInt( strIdTask );

                ResubmitFormResponse resubmitFormResponse = _resubmitFormResponseService.find( nIdHistory, nIdTask );
                if ( resubmitFormResponse != null && !resubmitFormResponse.isComplete( ) )
                {
                    if ( _resubmitFormResponseService.isRecordStateValid( resubmitFormResponse, request.getLocale( ) ) )
                    {
                        doAction( request, resubmitFormResponse );
                        page = getResubmitFormResponsePage( request, resubmitFormResponse );
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
        }
        else
        {
            _formsTaskService.setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, request.getParameter( PARAMETER_URL_RETURN ) );
        }

        return page;
    }

    /**
     * Get the ResubmitFormResponse page
     * 
     * @param request
     *            the HTTP request
     * @param ResubmitFormResponse
     *            the ResubmitFormResponse
     * @return a XPage
     * @throws SiteMessageException
     *             a site message if there is a problem
     */
    private XPage getResubmitFormResponsePage( HttpServletRequest request, ResubmitFormResponse resubmitFormResponse ) throws SiteMessageException
    {
        XPage page = new XPage( );

        FormResponse formResponse = _formsTaskService.getFormResponseFromIdHistory( resubmitFormResponse.getIdHistory( ) );
        List<Question> listQuestions = _resubmitFormResponseService.getListQuestionToEdit( formResponse, resubmitFormResponse.getListResubmitReponseValues( ) );

        List<Step> listStep = listQuestions.stream( ).map( Question::getStep ).map( Step::getId ).distinct( ).map( StepHome::findByPrimaryKey )
                .collect( Collectors.toList( ) );

        List<String> listStepDisplayTree = _formsTaskService.buildFormStepDisplayTreeList( request, listStep, listQuestions, formResponse,
                DisplayType.RESUBMIT_FRONTOFFICE );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_RESUBMIT_FORM, resubmitFormResponse );
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

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESUBMIT_FORM, request.getLocale( ), model );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_RESUBMIT_FORM_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_RESUBMIT_FORM_PATHLABEL, request.getLocale( ) ) );
        page.setContent( template.getHtml( ) );

        return page;
    }

    private void doAction( HttpServletRequest request, ResubmitFormResponse resubmitFormResponse ) throws SiteMessageException
    {
        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( StringUtils.isNotBlank( strAction ) )
        {
            if ( ACTION_DO_MODIFY_RESPONSE.equals( strAction ) )
            {
                if ( doEditResponse( request, resubmitFormResponse ) )
                {
                    // Back to home page
                    String strUrlReturn = request.getParameter( PARAMETER_URL_RETURN );
                    strUrlReturn = StringUtils.isNotBlank( strUrlReturn ) ? strUrlReturn : AppPathService.getBaseUrl( request );
                    _formsTaskService.setSiteMessage( request, MESSAGE_EDITION_COMPLETE, SiteMessage.TYPE_INFO, strUrlReturn );
                }
            }
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
    private boolean doEditResponse( HttpServletRequest request, ResubmitFormResponse response ) throws SiteMessageException
    {
        if ( _resubmitFormResponseService.isRecordStateValid( response, request.getLocale( ) ) )
        {
            if ( _resubmitFormResponseService.doEditResponseData( request, response ) )
            {
                _resubmitFormResponseService.doChangeResponseState( response, request.getLocale( ) );

                _resubmitFormResponseService.doCompleteResponse( response );

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
