/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.LinkedValuesFormResponseConfigValue;
import fr.paris.lutece.plugins.workflow.modules.forms.service.task.LinkedValuesFormResponseConfigService;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.bean.BeanUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * LinkedValuesFormResponseTaskComponent
 *
 */
public class LinkedValuesFormResponseTaskComponent extends NoFormTaskComponent
{
    
    // TEMPLATES
    private static final String           TEMPLATE_CONFIG               = "admin/plugins/workflow/modules/forms/task_linked_values_form_response_config.html";

    // MARKERS
    private static final String           MARK_CONFIG                   = "config";
    private static final String           MARK_LIST_FORM                = "forms";
    private static final String           MARK_LIST_QUESTIONS           = "questions";

    // PROPERTIES
    private static final String           PROPERTY_LIST_ENTRY_AVAILABLE = "task_linked_values_form_response_list_entry_id_type_available";

    // ACTIONS
    private static final String           ACTION_ADD_RULE               = "add_rule";
    private static final String           ACTION_REMOVE_RULE            = "remove_rule";

    // PARAMETERS
    private static final String           PARAMETER_ID_CONFIG_VALUE     = "id_config_value";
    private static final String           PARAMETER_ACTION              = "apply";
    
    @Inject
    @Named( LinkedValuesFormResponseConfigService.BEAN_NAME )
    LinkedValuesFormResponseConfigService _configService;

    @Override
    public String getDisplayConfigForm( HttpServletRequest reques, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        ReferenceList formList = FormHome.getFormsReferenceList( );

        model.put( MARK_LIST_FORM, formList );
        model.put( MARK_LIST_QUESTIONS, getListQuestions( formList ) );
        model.put( MARK_CONFIG, _configService.findByPrimaryKey( task.getId( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONFIG, locale, model );
        return template.getHtml( );
    }
    
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {        
        String action = request.getParameter( PARAMETER_ACTION );
        LinkedValuesFormResponseConfig config = _configService.findByPrimaryKey( task.getId( ) );
        LinkedValuesFormResponseConfigValue configValue = new LinkedValuesFormResponseConfigValue( );
        BeanUtil.populate( configValue, request, null );
        
        boolean isNewConfig = false;
        
        if ( config == null )
        {
            config = new LinkedValuesFormResponseConfig( );
            config.setIdTask( task.getId( ) );
            isNewConfig = true;
        }
        
        if( ACTION_ADD_RULE.equals( action ) )
        {
            config.addConfigValue( configValue );
            if( isNewConfig )
            {
                _configService.create( config );
            }
            else
            {
                _configService.update( config );  
            }
        }
        else if ( ACTION_REMOVE_RULE.equals( action ) )
        {
            String strIdConfigValue = request.getParameter( PARAMETER_ID_CONFIG_VALUE );
            int nIdConfigValue = Integer.parseInt( strIdConfigValue );
            
            List<LinkedValuesFormResponseConfigValue> newList = config.getListConfigValues( ).stream( )
                    .filter( configV -> configV.getIdConfigValue( ) != nIdConfigValue ).collect( Collectors.toList( ) );
            
            config.setListConfigValues( newList );
            
            _configService.update( config );
        }
        return null;
    }
    
    /**
     * List questions by form
     * @param formList
     * @return list question by form
     */
    private Map<String, List<Question>> getListQuestions( ReferenceList formList )
    {
        List<String> listIdTypeEntry = Arrays.asList( AppPropertiesService.getProperty( PROPERTY_LIST_ENTRY_AVAILABLE ).split( ";" ) );

        Map<String, List<Question>> listQuestionForm = new HashMap<>( );
        for ( ReferenceItem form : formList )
        {
            List<Question> listQuestions = new ArrayList<>( );

            for ( Question question : QuestionHome.getListQuestionByIdForm( Integer.parseInt( form.getCode( ) ) ) )
            {
                if ( listIdTypeEntry.contains( String.valueOf( question.getEntry( ).getEntryType( ).getIdType( ) ) ) )
                {
                    listQuestions.add( question );
                }
            }

            listQuestionForm.put( form.getCode( ), listQuestions );
        }
        return listQuestionForm;
    }
    
    @Override
    public String getDisplayTaskInformation( int arg0, HttpServletRequest arg1, Locale arg2, ITask arg3 )
    {
        return null;
    }
}
