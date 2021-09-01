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
package fr.paris.lutece.plugins.workflow.modules.forms.service.archiver;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflow.modules.archive.IResourceArchiver;
import fr.paris.lutece.plugins.workflow.modules.archive.service.IArchiveProcessingService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;

/**
 * {@link IResourceArchiver} for all data of module workflow-forms
 */
public class WorkflowFormsResourceArchiver implements IResourceArchiver
{
    public static final String BEAN_NAME = "workflow-forms.workflowFormsResourceArchiver";

    @Inject
    @Named( WorkflowFormsDeleteArchiveProcessingService.BEAN_NAME )
    private IArchiveProcessingService _deleteArchiveProcessingService;

    @Inject
    @Named( WorkflowFormsAnonymizeArchiveProcessingService.BEAN_NAME )
    private IArchiveProcessingService _anonymizeArchiveProcessingService;

    @Override
    public void archiveResource( ArchivalType archivalType, ResourceWorkflow resourceWorkflow )
    {
        if ( !FormResponse.RESOURCE_TYPE.equals( resourceWorkflow.getResourceType( ) ) )
        {
            return;
        }
        switch( archivalType )
        {
            case DELETE:
                _deleteArchiveProcessingService.archiveResource( resourceWorkflow );
                break;
            case ANONYMIZE:
                _anonymizeArchiveProcessingService.archiveResource( resourceWorkflow );
                break;
            default:
                break;
        }
    }

    @Override
    public String getBeanName( )
    {
        return BEAN_NAME;
    }
}
