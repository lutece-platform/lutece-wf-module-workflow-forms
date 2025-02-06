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
package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseTaskHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public interface ICompleteFormResponseTaskHistoryService
{

    /**
     * Creation of an instance of completeFormResponseTaskHistory value
     * 
     * @param completeFormResponseTaskHistory
     *            The instance of completeFormResponseTaskHistory value which contains the informations to store
     */
    void create( CompleteFormResponseTaskHistory completeFormResponseTaskHistory );

    /**
     * Return a list of CompleteFormResponseTaskHistory with an idHistory and an idTask
     * 
     * @param nIdHistory
     * @param nIdTask
     * @return a list of CompleteFormResponseTaskHistory
     */
    List<CompleteFormResponseTaskHistory> load( int nIdHistory, int nIdTask );

    /**
     * Deletes all CompleteFormResponseTaskHistory with an History and Task
     * 
     * @param history
     * @param task
     */
    void removeAllByHistoryAndTask( ResourceHistory history, ITask task );
}
