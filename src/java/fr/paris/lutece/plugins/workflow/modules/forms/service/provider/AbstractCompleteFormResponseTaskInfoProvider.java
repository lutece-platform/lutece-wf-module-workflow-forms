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
package fr.paris.lutece.plugins.workflow.modules.forms.service.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.forms.service.signrequest.ResubmitFormResponseRequestAuthenticatorService;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.service.taskinfo.AbstractTaskInfoProvider;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;
import net.sf.json.JSONObject;

public abstract class AbstractCompleteFormResponseTaskInfoProvider extends AbstractTaskInfoProvider
{
    private static final String PROPERTY_URL_RETURN = "workflow-forms.url_return";

    private static final String PROPERTY_BASE_URL_USE_PROPERTY = "workflow-forms.base_url.use_property";
    private static final String PROPERTY_LUTECE_BASE_URL = "lutece.base.url";
    private static final String PROPERTY_LUTECE_PROD_URL = "lutece.prod.url";

    private static final String PARAMETER_ID_HISTORY = "id_history";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_URL_RETURN = "url_return";
    private static final String PARAMETER_SIGNATURE = "signature";
    private static final String PARAMETER_TIMESTAMP = "timestamp";
    private static final String SLASH = "/";

    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Override
    public String getPluginName( )
    {
        return WorkflowPlugin.PLUGIN_NAME;
    }
    
    public abstract String getPageName( );

    @Override
    public String getTaskResourceInfo( int nIdHistory, int nIdTask, HttpServletRequest request )
    {
        JSONObject jsonInfos = new JSONObject( );
        String strInfoUrl = StringUtils.EMPTY;

        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        if ( resourceHistory != null )
        {
            List<String> listElements = new ArrayList<>( );
            listElements.add( Integer.toString( nIdHistory ) );
            listElements.add( Integer.toString( nIdTask ) );

            String strTimestamp = Long.toString( new Date( ).getTime( ) );
            String strSignature = ResubmitFormResponseRequestAuthenticatorService.getRequestAuthenticator( ).buildSignature( listElements, strTimestamp );
            StringBuilder sbUrl = new StringBuilder( getBaseUrl( request ) );

            if ( !sbUrl.toString( ).endsWith( SLASH ) )
            {
                sbUrl.append( SLASH );
            }

            UrlItem url = new UrlItem( sbUrl.toString( ) + AppPathService.getPortalUrl( ) );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, getPageName( ) );
            url.addParameter( PARAMETER_ID_HISTORY, nIdHistory );
            url.addParameter( PARAMETER_ID_TASK, nIdTask );
            url.addParameter( PARAMETER_SIGNATURE, strSignature );
            url.addParameter( PARAMETER_TIMESTAMP, strTimestamp );
            url.addParameter( PARAMETER_URL_RETURN, AppPropertiesService.getProperty( PROPERTY_URL_RETURN ) );

            strInfoUrl = url.getUrlWithEntity( );
        }

        String strInfoEntries = getInfoEntries( nIdHistory );

        String strInfoMsg = getInfoMessage( nIdHistory, nIdTask );
        jsonInfos.accumulate( getMarkUrl( ), strInfoUrl );
        jsonInfos.accumulate( getMarkMsg( ), strInfoMsg );
        jsonInfos.accumulate( getMarkEntries( ), strInfoEntries );

        return jsonInfos.toString( );
    }

    protected abstract String getInfoEntries( int nIdHistory );

    protected abstract String getInfoMessage( int nIdHistory, int nIdTask );

    protected abstract String getMarkUrl( );

    protected abstract String getMarkMsg( );

    protected abstract String getMarkEntries( );

    /**
     * Get the base url
     * 
     * @param request
     *            the HTTP request
     * @return the base url
     */
    private String getBaseUrl( HttpServletRequest request )
    {
        String strBaseUrl;

        if ( request != null && !isBaseUrlFetchedInProperty( ) )
        {
            strBaseUrl = AppPathService.getBaseUrl( request );
        }
        else
        {
            strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_BASE_URL );

            if ( StringUtils.isBlank( strBaseUrl ) )
            {
                strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_PROD_URL );
            }
        }

        return strBaseUrl;
    }

    /**
     * Check if the base url must be fetched in the config.properties file or in the request
     * 
     * @return true if it must be fetched in the config.properties file
     */
    private boolean isBaseUrlFetchedInProperty( )
    {
        return Boolean.valueOf( AppPropertiesService.getProperty( PROPERTY_BASE_URL_USE_PROPERTY ) );
    }
}
