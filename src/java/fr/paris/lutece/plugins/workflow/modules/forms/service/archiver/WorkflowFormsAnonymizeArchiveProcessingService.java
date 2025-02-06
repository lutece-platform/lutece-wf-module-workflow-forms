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
package fr.paris.lutece.plugins.workflow.modules.forms.service.archiver;

import javax.inject.Inject;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.IEntryAnonymizationType;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.archive.service.AbstractArchiveProcessingService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;

/**
 * Service for archival of type delete of plugin-workflow.
 */
public class WorkflowFormsAnonymizeArchiveProcessingService extends AbstractArchiveProcessingService
{
    public static final String BEAN_NAME = "workflow-forms.workflowFormsAnonymizeArchiveProcessingService";

    @Inject
    private FormService _formService;

    @Override
    public void archiveResource( ResourceWorkflow resourceWorkflow )
    {
        archiveFormResponse( resourceWorkflow );
    }

    private void archiveFormResponse( ResourceWorkflow resourceWorkflow )
    {
        int formResponseId = resourceWorkflow.getIdResource( );
        FormResponse formResponse = FormResponseHome.loadById( formResponseId );

        for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponseId ) )
        {
            Entry entry = formQuestionResponse.getQuestion( ).getEntry( );
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
            for ( Response response : formQuestionResponse.getEntryResponse( ) )
            {
                boolean first = true;
                for ( IEntryAnonymizationType wildcard : entryTypeService.getValidWildcards( ) )
                {
                    wildcard.getAnonymisationTypeService( ).anonymizeResponse( entry, response, first );
                    first = false;
                }
                ResponseHome.update( response );
            }
        }

        _formService.fireFormResponseEventUpdate( formResponse );
    }
}
