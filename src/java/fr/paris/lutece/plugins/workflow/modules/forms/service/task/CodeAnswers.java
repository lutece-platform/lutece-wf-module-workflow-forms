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

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.util.ReferenceList;

/**
 * The answers a technical code offers over the forms that count, and how many of them carry it : both come from one walk over the questions.
 * 
 * The forms that count are those using the workflow of the task, which are the only ones whose responses the task will ever see; when no form uses
 * the workflow yet, every form carrying the code counts.
 */
public final class CodeAnswers
{
    private final ReferenceList _referenceList;
    private final int _nFormCount;
    private final boolean _bWorkflowUnused;

    /**
     * @param referenceList
     *            the answers, each labelled with the number of forms producing it when not all of them do
     * @param nFormCount
     *            the number of distinct forms carrying the code
     * @param bWorkflowUnused
     *            true when no form uses the workflow, so that the answers gather every form carrying the code
     */
    public CodeAnswers( ReferenceList referenceList, int nFormCount, boolean bWorkflowUnused )
    {
        _referenceList = referenceList;
        _nFormCount = nFormCount;
        _bWorkflowUnused = bWorkflowUnused;
    }

    /**
     * @return the answers, each labelled with the number of forms producing it when not all of them do
     */
    public ReferenceList getReferenceList( )
    {
        return _referenceList;
    }

    /**
     * @return the number of distinct forms carrying the code
     */
    public int getFormCount( )
    {
        return _nFormCount;
    }

    /**
     * @return true when no form uses the workflow, so that the answers gather every form carrying the code
     */
    public boolean isWorkflowUnused( )
    {
        return _bWorkflowUnused;
    }

    /**
     * @return true when the questions carrying the code offer answer choices : a free text answer can take any value
     */
    public boolean hasChoices( )
    {
        // the list opens with an empty item
        return _referenceList.size( ) > 1;
    }

    /**
     * @param strValue
     *            an answer value
     * @return true when at least one of the forms that count produces this value
     */
    public boolean isOffered( String strValue )
    {
        return StringUtils.isNotEmpty( strValue ) && _referenceList.stream( ).anyMatch( item -> strValue.equals( item.getCode( ) ) );
    }
}
