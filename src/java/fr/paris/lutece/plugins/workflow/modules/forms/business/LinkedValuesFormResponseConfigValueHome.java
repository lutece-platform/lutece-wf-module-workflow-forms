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

import fr.paris.lutece.portal.service.spring.SpringContextService;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for LinkedValuesFormResponseConfigValue objects
 */

public final class LinkedValuesFormResponseConfigValueHome
{

    // Static variable pointed at the DAO instance
    private static ILinkedValuesFormResponseConfigValueDAO _dao = ( ILinkedValuesFormResponseConfigValueDAO ) SpringContextService.getBean( LinkedValuesFormResponseConfigValueDAO.BEAN_NAME );

    /**
     * Private constructor - this class need not be instantiated
     */

    private LinkedValuesFormResponseConfigValueHome( )
    {
    }

    /**
     * Create an instance of the linkedValuesFormResponseConfigValue class
     * 
     * @param linkedValuesFormResponseConfigValue
     *            The instance of the LinkedValuesFormResponseConfigValue which contains the informations to store
     * @return The instance of linkedValuesFormResponseConfigValue which has been created with its primary key.
     */

    public static LinkedValuesFormResponseConfigValue create( LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue )
    {
        _dao.insert( linkedValuesFormResponseConfigValue );

        return linkedValuesFormResponseConfigValue;
    }

    /**
     * Update of the linkedValuesFormResponseConfigValue data specified in parameter
     * 
     * @param linkedValuesFormResponseConfigValue
     *            The instance of the LinkedValuesFormResponseConfigValue which contains the data to store
     * @return The instance of the linkedValuesFormResponseConfigValue which has been updated
     */

    public static LinkedValuesFormResponseConfigValue update( LinkedValuesFormResponseConfigValue linkedValuesFormResponseConfigValue )
    {
        _dao.store( linkedValuesFormResponseConfigValue );

        return linkedValuesFormResponseConfigValue;
    }

    /**
     * Remove the linkedValuesFormResponseConfigValue whose identifier is specified in parameter
     * 
     * @param nLinkedValuesFormResponseConfigValueId
     *            The linkedValuesFormResponseConfigValue Id
     */

    public static void remove( int nLinkedValuesFormResponseConfigValueId )
    {
        _dao.delete( nLinkedValuesFormResponseConfigValueId );
    }
    
    /**
     * Remove the linkedValuesFormResponseConfigValue whose identifier is specified in parameter
     * 
     * @param nIdConfig
     *            The nIdConfig Id
     */

    public static void removeByIdConfig( int nIdConfig )
    {
        _dao.deleteByIdConfig( nIdConfig );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a linkedValuesFormResponseConfigValue whose identifier is specified in parameter
     * 
     * @param nKey
     *            The linkedValuesFormResponseConfigValue primary key
     * @return an instance of LinkedValuesFormResponseConfigValue
     */

    public static LinkedValuesFormResponseConfigValue findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Load the data of all the linkedValuesFormResponseConfigValue objects and returns them in form of a collection
     * 
     * @return the list which contains the data of all the linkedValuesFormResponseConfigValue objects
     */

    public static List<LinkedValuesFormResponseConfigValue> findAll( )
    {
        return _dao.selectLinkedValuesFormResponseConfigValuesList( );
    }
    

    /**
     * Load the data of all the linkedValuesFormResponseConfigValue objects bu id config and returns them in form of a collection
     * 
     * @return the list which contains the data of all the linkedValuesFormResponseConfigValue objects
     */

    public static List<LinkedValuesFormResponseConfigValue> findByIdConfig( int nIdConfig )
    {
        return _dao.selectLinkedValuesFormResponseConfigValuesByIdConfig( nIdConfig );
    }

}
