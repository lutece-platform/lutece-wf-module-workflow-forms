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

import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractCompleteFormResponseValue
{

    /**
     * Default iteration number of a Response
     */
    public static final int DEFAULT_ITERATION_NUMBER = NumberUtils.INTEGER_MINUS_ONE;

    private int _nIdHistory;
    private int _nIdEntry;
    private int _nIterationNumber = DEFAULT_ITERATION_NUMBER;

    /**
     * Get the id history
     * 
     * @return the id history
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * Set the id history
     * 
     * @param nIdHistory
     *            the id history
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     * Get the id entry
     * 
     * @return the id entry
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * Set the id entry
     * 
     * @param nIdEntry
     *            the id entry
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /**
     * Return the iteration number of the response
     *
     * @return the nIterationNumber
     */
    public int getIterationNumber( )
    {
        return _nIterationNumber;
    }

    /**
     * Set the iteration number of the response
     *
     * @param nIterationNumber
     *            the nIterationNumber to set
     */
    public void setIterationNumber( int nIterationNumber )
    {
        _nIterationNumber = nIterationNumber;
    }
}
