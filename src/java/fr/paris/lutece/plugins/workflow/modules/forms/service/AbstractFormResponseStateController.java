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
package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfigHome;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateController;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.util.ReferenceList;

public abstract class AbstractFormResponseStateController implements IChooseStateController
{

    @Override
    public boolean hasConfig( )
    {
        return true;
    }

    @Override
    public void doRemoveConfig( ITask task )
    {
        FormResponseValueStateControllerConfigHome.removeByTask( task.getId( ) );
    }

    protected FormResponseValueStateControllerConfig loadConfig( int idTask )
    {
        FormResponseValueStateControllerConfig controllerConfig = FormResponseValueStateControllerConfigHome.findByTask( idTask );
        if ( controllerConfig == null )
        {
            controllerConfig = new FormResponseValueStateControllerConfig( );
            controllerConfig.setIdTask( idTask );
            FormResponseValueStateControllerConfigHome.create( controllerConfig );
        }
        return controllerConfig;
    }

    protected ReferenceList getQuestionReferenceList( int idStep )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( -1, "" );
        if ( idStep != -1 )
        {
            List<Question> questionList = QuestionHome.getQuestionsListByStep( idStep );
            for ( Question question : questionList )
            {
                if ( canQuestionBeCondition( question ) )
                {
                    refList.addItem( question.getId( ), question.getTitle( ) );
                }
            }
        }

        return refList;
    }

    protected abstract boolean canQuestionBeCondition( Question question );

    protected Response getResponseFromQuestionAndFormResponse( int IdQuestion, int idResponse )
    {
        List<FormQuestionResponse> responseList = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( idResponse, IdQuestion );

        if ( CollectionUtils.isEmpty( responseList ) )
        {
            return null;
        }
        List<Response> entryResponseList = responseList.get( 0 ).getEntryResponse( );
        if ( CollectionUtils.isEmpty( entryResponseList ) )
        {
            return null;
        }
        return entryResponseList.get( 0 );
    }
}
