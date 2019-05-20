/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.util.AppException;

/**
 * This class is a service for the tasks of the plugin-forms
 *
 */
public class FormsTaskService implements IFormsTaskService
{
    private final IResourceHistoryService _resourceHistoryService;

    /**
     * Constructor
     * 
     * @param resourceHistoryService
     *            the resource history service
     */
    @Inject
    public FormsTaskService( IResourceHistoryService resourceHistoryService )
    {
        _resourceHistoryService = resourceHistoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory findResourceHistory( int nIdResourceHistory )
    {
        return _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormResponse findFormResponseFrom( int nIdResource, String strResourceType )
    {
        FormResponse formResponse = null;

        if ( FormResponse.RESOURCE_TYPE.equals( strResourceType ) )
        {
            formResponse = FormResponseHome.findByPrimaryKey( nIdResource );
        }
        else
        {
            throw new AppException( "This task must be used with a form" );
        }

        return formResponse;
    }
    
    @Override
	public List<String> buildFormStepDisplayTreeList( HttpServletRequest request, List<Step> listStep, List<Question> listQuestionToDisplay,
	            FormResponse formResponse, DisplayType displayType )
	{
		List<String> listFormDisplayTrees = new ArrayList<>( );

		List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
		List<Integer> listQuestionToDisplayId = listQuestionToDisplay.stream( ).map( question -> question.getId( ) ).collect( Collectors.toList( ) );

		listFormQuestionResponse.stream( ).filter( formQuestionResponse -> listQuestionToDisplayId.contains( formQuestionResponse.getQuestion( ).getId( ) ) );

		if ( !CollectionUtils.isEmpty( listStep ) )
		{
				for ( Step step : listStep )
				{
					int nIdStep = step.getId( );

					StepDisplayTree stepDisplayTree = new StepDisplayTree( nIdStep, formResponse );
					listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, listFormQuestionResponse, request.getLocale( ),
	                		displayType ) );
				}
		}

		return listFormDisplayTrees;
	}
}
