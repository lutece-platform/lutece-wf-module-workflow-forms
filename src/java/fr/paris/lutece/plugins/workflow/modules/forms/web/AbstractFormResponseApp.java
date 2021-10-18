package fr.paris.lutece.plugins.workflow.modules.forms.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.AbstractCompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.IFormsTaskService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.signrequest.AbstractPrivateKeyAuthenticator;

/**
 *  abstract class for Complete & Resubmit Form Response FO action
 */
public abstract class AbstractFormResponseApp<R extends AbstractCompleteFormResponse> implements XPageApplication
{

    private static final long serialVersionUID = 2628844288485204790L;
    
    // ACTIONS
    private static final String ACTION_DO_MODIFY_RESPONSE = "do_modify_response";
    
    // MARKS
    private static final String MARK_STEP_LIST = "list_step";
    private static final String MARK_ID_RESPONSE = "id_response";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_URL_RETURN = "url_return";
    private static final String MARK_SIGNATURE = "signature";
    private static final String MARK_TIMESTAMP = "timestamp";
    
    // PARAMETERS
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_SIGNATURE = "signature";
    private static final String PARAMETER_TIMESTAMP = "timestamp";
    protected static final String PARAMETER_URL_RETURN = "url_return";
    private static final String PARAMETER_ID_HISTORY = "id_history";
    private static final String PARAMETER_ID_TASK = "id_task";
    
    // MESSAGES
    private static final String MESSAGE_EDITION_COMPLETE = "module.workflow.forms.message.edition_complete";
    private static final String MESSAGE_RECORD_ALREADY_COMPLETED = "module.workflow.forms.message.response_already_completed";
    
    protected IFormsTaskService _formsTaskService = SpringContextService.getBean( "workflow-forms.formsTaskService" );
    
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException, SiteMessageException
    {
        XPage page = null;
        if ( !getRequestAuthenticator( ).isRequestAuthenticated( request ) )
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

            R completeFormResponse = findAbstractCompleteFormResponse( nIdHistory, nIdTask );
            if ( completeFormResponse != null && !completeFormResponse.isComplete( ) )
            {
                if ( isRecordStateValid( completeFormResponse, request.getLocale( ) ) )
                {
                    doAction( request, completeFormResponse, nIdTask, nIdHistory );
                    page = getFormResponseXPage( request, completeFormResponse );
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

    protected void doAction( HttpServletRequest request, R completeFormResponse, int idTask, int idHistory ) throws SiteMessageException
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
    
    protected Map<String, Object> initModelFormPage( HttpServletRequest request, FormResponse formResponse, List<String> listStepDisplayTree )
    {
        Map<String, Object> model = new HashMap<>( );
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

        return model;
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
    protected abstract boolean doEditResponse( HttpServletRequest request, R response, int idTask, int idHistory ) throws SiteMessageException;
    
    protected abstract AbstractPrivateKeyAuthenticator getRequestAuthenticator( );
    
    protected abstract boolean isRecordStateValid( R resubmitFormResponse, Locale locale );
    
    /**
    * Get the Xpage
    * 
    * @param request
    *            the HTTP request
    * @param CompleteFormResponse
    *            the CompleteFormResponse
    * @return a XPage
    */
    protected abstract XPage getFormResponseXPage( HttpServletRequest request, R completeFormResponse );
    
    protected abstract R findAbstractCompleteFormResponse( int nIdHistory, int nIdTask );
}
