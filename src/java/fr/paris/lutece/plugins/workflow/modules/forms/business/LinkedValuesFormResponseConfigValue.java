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

/**
 * This is the business class for the object LinkedValuesFormResponseConfigValue
 */
public class LinkedValuesFormResponseConfigValue
{
    // Variables declarations
    private int _nIdConfigValue;
    private int _nIdConfig;
    private int _nIdForm;
    private int _nIdQuestionSource;
    private String _strQuestionSourceValue;
    private int _nIdQuestionTarget;
    private String _strQuestionTargetValue;
    
    /**
     * Returns the IdConfigValue
     * 
     * @return The IdConfigValue
     */
    public int getIdConfigValue( )
    {
        return _nIdConfigValue;
    }

    /**
     * Sets the IdConfigValue
     * 
     * @param nIdConfigValue
     *            The IdConfigValue
     */
    public void setIdConfigValue( int nIdConfigValue )
    {
        _nIdConfigValue = nIdConfigValue;
    }

    /**
     * Returns the IdConfig
     * 
     * @return The IdConfig
     */
    public int getIdConfig( )
    {
        return _nIdConfig;
    }

    /**
     * Sets the IdConfig
     * 
     * @param nIdConfig
     *            The IdConfig
     */
    public void setIdConfig( int nIdConfig )
    {
        _nIdConfig = nIdConfig;
    }

    /**
     * Returns the IdForm
     * 
     * @return The IdForm
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Sets the IdForm
     * 
     * @param nIdForm
     *            The IdForm
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * Returns the IdQuestionSource
     * 
     * @return The IdQuestionSource
     */
    public int getIdQuestionSource( )
    {
        return _nIdQuestionSource;
    }

    /**
     * Sets the IdQuestionSource
     * 
     * @param nIdQuestionSource
     *            The IdQuestionSource
     */
    public void setIdQuestionSource( int nIdQuestionSource )
    {
        _nIdQuestionSource = nIdQuestionSource;
    }

    /**
     * Returns the IdQuestionTarget
     * 
     * @return The IdQuestionTarget
     */
    public int getIdQuestionTarget( )
    {
        return _nIdQuestionTarget;
    }

    /**
     * Sets the IdQuestionTarget
     * 
     * @param nIdQuestionTarget
     *            The IdQuestionTarget
     */
    public void setIdQuestionTarget( int nIdQuestionTarget )
    {
        _nIdQuestionTarget = nIdQuestionTarget;
    }

    /**
     * @return the _strQuestionSourceValue
     */
    public String getQuestionSourceValue( )
    {
        return _strQuestionSourceValue;
    }

    /**
     * @param strQuestionSourceValue the _strQuestionSourceValue to set
     */
    public void setQuestionSourceValue( String strQuestionSourceValue )
    {
        this._strQuestionSourceValue = strQuestionSourceValue;
    }

    /**
     * @return the _strQuestionTargetValue
     */
    public String getQuestionTargetValue( )
    {
        return _strQuestionTargetValue;
    }

    /**
     * @param strQuestionTargetValue the _strQuestionTargetValue to set
     */
    public void setQuestionTargetValue( String strQuestionTargetValue )
    {
        this._strQuestionTargetValue = strQuestionTargetValue;
    }
}
