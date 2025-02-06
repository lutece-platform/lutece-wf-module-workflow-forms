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

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;

public class FormResponseValueStateControllerConfig
{
    private int _id;
    private int _idTask;
    private boolean _multiform;
    private Form _form;
    private Step _step;
    private Question _question;
    private String _value;
    private String _code;

    /**
     * @return the id
     */
    public int getId( )
    {
        return _id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId( int id )
    {
        _id = id;
    }

    /**
     * @return the idTask
     */
    public int getIdTask( )
    {
        return _idTask;
    }

    /**
     * @param idTask
     *            the idTask to set
     */
    public void setIdTask( int idTask )
    {
        _idTask = idTask;
    }

    /**
     * @return the form
     */
    public Form getForm( )
    {
        return _form;
    }

    /**
     * @param form
     *            the form to set
     */
    public void setForm( Form form )
    {
        _form = form;
    }

    /**
     * @return the step
     */
    public Step getStep( )
    {
        return _step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep( Step step )
    {
        _step = step;
    }

    /**
     * @return the question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * @param question
     *            the question to set
     */
    public void setQuestion( Question question )
    {
        _question = question;
    }

    /**
     * @return the value
     */
    public String getValue( )
    {
        return _value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue( String value )
    {
        _value = value;
    }

    /**
     * @return the multiform
     */
    public boolean isMultiform( )
    {
        return _multiform;
    }

    /**
     * @param multiform
     *            the multiform to set
     */
    public void setMultiform( boolean multiform )
    {
        this._multiform = multiform;
    }

    /**
     * @return the code
     */
    public String getCode( )
    {
        return _code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode( String code )
    {
        _code = code;
    }
}
