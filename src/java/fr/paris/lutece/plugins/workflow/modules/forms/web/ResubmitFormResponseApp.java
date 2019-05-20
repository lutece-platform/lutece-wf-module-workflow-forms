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
import fr.paris.lutece.plugins.genericattributes.business.Entry;
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

    // MESSAGES
    private static final String MESSAGE_RECORD_ALREADY_COMPLETED = "module.workflow.forms.message.response_already_completed";
    private static final String PROPERTY_XPAGE_RESUBMIT_FORM_PAGETITLE = "module.workflow.forms.resubmit_form.page_title";
    private static final String PROPERTY_XPAGE_RESUBMIT_FORM_PATHLABEL = "module.workflow.forms.resubmit_form.page_label";
    
    // PARAMETERS
    private static final String PARAMETER_URL_RETURN = "url_return";
    private static final String PARAMETER_ID_HISTORY = "id_history";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_SIGNATURE = "signature";
    private static final String PARAMETER_TIMESTAMP = "timestamp";
    
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
    	if ( true )
    	//if ( ResubmitFormResponseRequestAuthenticatorService.getRequestAuthenticator( ).isRequestAuthenticated( request ))
    	{
    		String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
            String strIdTask = request.getParameter( PARAMETER_ID_TASK );
            
            if ( StringUtils.isNotBlank( strIdHistory ) && StringUtils.isNumeric( strIdHistory ) && StringUtils.isNotBlank( strIdTask )
                    && StringUtils.isNumeric( strIdTask ) )
            {
            	int nIdHistory = Integer.parseInt( strIdHistory );
                int nIdTask = Integer.parseInt( strIdTask );
                
                ResubmitFormResponse resubmitFormResponse = _resubmitFormResponseService.find(nIdHistory, nIdTask);
                if ( resubmitFormResponse != null && !resubmitFormResponse.isComplete( ) )
                {
                	 if ( _resubmitFormResponseService.isRecordStateValid( resubmitFormResponse, request.getLocale( ) ) )
                     {
                		 doAction( request, resubmitFormResponse );
                		 page = getResubmitFormResponsePage( request, resubmitFormResponse );
                     }
                	 else
                	 {
                		 _resubmitFormResponseService.setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP,
                                 request.getParameter( PARAMETER_URL_RETURN ) );
                	 }
                }
                else
                {
                	_resubmitFormResponseService.setSiteMessage( request, MESSAGE_RECORD_ALREADY_COMPLETED, SiteMessage.TYPE_INFO,
                            request.getParameter( PARAMETER_URL_RETURN ) );
                }
            }
            else
            {
            	_resubmitFormResponseService.setSiteMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP,
                         request.getParameter( PARAMETER_URL_RETURN ) );
            }
    	}
    	else 
    	{
    		_resubmitFormResponseService.setSiteMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP,
                     request.getParameter( PARAMETER_URL_RETURN ) );
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
    private XPage getResubmitFormResponsePage( HttpServletRequest request,  ResubmitFormResponse resubmitFormResponse ) throws SiteMessageException
    {
    	XPage page = new XPage( );
    	
    	List<Entry> listEntries = _resubmitFormResponseService.getListEntriesToEdit( resubmitFormResponse.getListResubmitReponseValues( ) );
    	List<Integer> idEntries = listEntries.stream( ).map( Entry::getIdEntry ).collect( Collectors.toList( ) );
    	
    	FormResponse formResponse = _resubmitFormResponseService.getFormResponseFromIdHistory( resubmitFormResponse.getIdHistory( ) );
		List<Question> listQuestions = _resubmitFormResponseService.findListQuestionShownCompleteness( formResponse );
		listQuestions = listQuestions.stream( )
				.filter( question -> idEntries.contains( question.getEntry( ).getIdEntry( ) ) )
				.collect( Collectors.toList( ) );
		
		List<Step> listStep = listQuestions.stream( )
				.map( Question::getStep )
				.map( Step::getId )
				.distinct( )
				.map( StepHome::findByPrimaryKey )
				.collect( Collectors.toList( ) );
		
		List<String> listStepDisplayTree = _formsTaskService.buildFormStepDisplayTreeList( request, listStep, listQuestions, formResponse, DisplayType.RESUBMIT_FRONTOFFICE );
    	
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
    	
    }
}
