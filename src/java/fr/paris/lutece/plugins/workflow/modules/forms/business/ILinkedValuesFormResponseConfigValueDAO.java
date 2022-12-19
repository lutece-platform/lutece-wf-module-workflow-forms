/*
 * Copyright (c) 2002-2022, Mairie de Paris
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

import java.util.List;

/**
 * ILinkedValuesFormResponseConfigValueDAO Interface
 */

public interface ILinkedValuesFormResponseConfigValueDAO
{

    /**
     * Insert a new record in the table.
     * 
     * @param linkedValuesFormResponseConfigValue
     *            instance of the LinkedValuesFormResponseConfigValue object to inssert
     */

    void insert( LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue );

    /**
     * Update the record in the table
     * 
     * @param linkedValuesFormResponseConfigValue
     *            the reference of the LinkedValuesFormResponseConfigValue
     */

    void store( LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue );

    /**
     * Delete a record from the table
     * 
     * @param nIdLinkedValuesFormResponseConfigValue
     *            int identifier of the LinkedValuesFormResponseConfigValue to delete
     */

    void delete( int nIdLinkedValuesFormResponseConfigValue );
    
    /**
     * Delete a record from the table by id config
     * 
     * @param nIdConfig
     *            int identifier of the nIdConfig to delete
     */

    void deleteByIdConfig( int nIdConfig );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the linkedValuesFormResponseConfigValue
     * @return The instance of the linkedValuesFormResponseConfigValue
     */

    LinkedValuesFormResponseConfigValue load( int nKey );
    
    /**
     * Load the data of all the linkedValuesFormResponseConfigValue objects by id config and returns them as a collection
     * 
     * @param nIdConfig
     * @return the List which contains the data of all the linkedValuesFormResponseConfigValue objects
     */

    List<LinkedValuesFormResponseConfigValue> selectLinkedValuesFormResponseConfigValuesByIdConfig( int nIdConfig );

    /**
     * Load the data of all the linkedValuesFormResponseConfigValue objects and returns them as a collection
     * 
     * @return the List which contains the data of all the linkedValuesFormResponseConfigValue objects
     */

    List<LinkedValuesFormResponseConfigValue> selectLinkedValuesFormResponseConfigValuesList( );

}
