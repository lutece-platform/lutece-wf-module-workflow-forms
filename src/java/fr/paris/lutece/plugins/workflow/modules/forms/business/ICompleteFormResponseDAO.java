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
package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.sql.Timestamp;

import java.util.List;

/**
 *
 * ICompleteFormResponseDAO
 *
 */
public interface ICompleteFormResponseDAO
{
    /**
     * Insert new record
     * 
     * @param completeFormResponse
     *            the CompleteFormResponse Object
     * @param plugin
     *            the plugin
     */
    void insert( CompleteFormResponse completeFormResponse, Plugin plugin );

    /**
     * Insert new record
     * 
     * @param completeFormResponse
     *            the CompleteFormResponse Object
     * @param plugin
     *            the plugin
     */
    void store( CompleteFormResponse completeFormResponse, Plugin plugin );

    /**
     * Load a CompleteFormResponse by id history
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     * @return CompleteFormResponse Object
     */
    CompleteFormResponse load( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Load a list of CompleteFormResponse by id task
     * 
     * @param nIdTask
     *            the id task
     * @param plugin
     *            the plugin
     * @return a list of CompleteFormResponse
     */
    List<CompleteFormResponse> loadByIdTask( int nIdTask, Plugin plugin );

    /**
     * Remove CompleteFormResponse by id history
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     */
    void deleteByIdHistory( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Remove CompleteFormResponse by id task
     * 
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     */
    void deleteByIdTask( int nIdTask, Plugin plugin );

    /**
     * Marks the CompleteFormResponse as complete, only if it is not complete yet. The update is atomic: when two requests submit the same form at the
     * same time, exactly one of them gets true.
     * 
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @param dateCompleted
     *            the completion date
     * @param plugin
     *            the plugin
     * @return true if the response has been marked as complete by this call, false if it was already complete or does not exist
     */
    boolean markAsComplete( int nIdHistory, int nIdTask, Timestamp dateCompleted, Plugin plugin );

    /**
     * Marks the CompleteFormResponse as not complete again, so the form can be submitted another time.
     * 
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     */
    void reopen( int nIdHistory, int nIdTask, Plugin plugin );
}
