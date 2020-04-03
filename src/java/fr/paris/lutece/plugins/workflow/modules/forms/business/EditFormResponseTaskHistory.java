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
package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.forms.business.Question;

public class EditFormResponseTaskHistory
{

    private int _nIdHistory;
    private Question _question;
    private int _nIdTask;
    private String _strPreviousValue;
    private String _strNewValue;

    /**
     * @return the idHistory
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * @param idHistory
     *            the idHistory to set
     */
    public void setIdHistory( int idHistory )
    {
        _nIdHistory = idHistory;
    }

    /**
     * @return the _question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * @param question
     *            the _question to set
     */
    public void setQuestion( Question question )
    {
        _question = question;
    }

    /**
     * @return the idTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * @param idTask
     *            the idTask to set
     */
    public void setIdTask( int idTask )
    {
        _nIdTask = idTask;
    }

    /**
     * @return the previousValue
     */
    public String getPreviousValue( )
    {
        return _strPreviousValue;
    }

    /**
     * @param previousValue
     *            the previousValue to set
     */
    public void setPreviousValue( String previousValue )
    {
        _strPreviousValue = previousValue;
    }

    /**
     * @return the newValue
     */
    public String getNewValue( )
    {
        return _strNewValue;
    }

    /**
     * @param newValue
     *            the newValue to set
     */
    public void setNewValue( String newValue )
    {
        _strNewValue = newValue;
    }

}
