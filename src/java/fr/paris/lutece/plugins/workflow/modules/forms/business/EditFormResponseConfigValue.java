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
package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;

public class EditFormResponseConfigValue
{
    private int _nIdConfigValue;
    private int _nIdConfig;
    private Form _form;
    private Step _step;
    private Question _question;
    private String _strResponse;
    private String _strCode;

    /**
     * @return the nIdConfigValue
     */
    public int getIdConfigValue( )
    {
        return _nIdConfigValue;
    }

    /**
     * @param nIdConfigValue
     *            the nIdConfigValue to set
     */
    public void setIdConfigValue( int nIdConfigValue )
    {
        _nIdConfigValue = nIdConfigValue;
    }

    /**
     * @return the nIdConfig
     */
    public int getIdConfig( )
    {
        return _nIdConfig;
    }

    /**
     * @param nIdConfig
     *            the nIdConfig to set
     */
    public void setIdConfig( int nIdConfig )
    {
        _nIdConfig = nIdConfig;
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

    public Form getForm( )
    {
        return _form;
    }

    public void setForm( Form form )
    {
        _form = form;
    }
    
    /**
     * @return the response
     */
    public String getResponse( )
    {
        return _strResponse;
    }

    /**
     * @param strResponse the strResponse to set
     */
    public void setResponse( String strResponse )
    {
        _strResponse = strResponse;
    }

    /**
     * @return the strCode
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * @param strCode the strCode to set
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }
}
