/*
 * Copyright (c) 2002-2023, City of Paris
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
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.vault.business;

import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;


/**
 * This is the business class test for the object Properties
 */
public class PropertiesBusinessTest extends LuteceTestCase
{
    private static final String KEY1 = "Key1";
    private static final String KEY2 = "Key2";
    private static final String VALUE1 = "Value1";
    private static final String VALUE2 = "Value2";
    private static final int IDENVIRONNEMENT1 = 1;
    private static final int IDENVIRONNEMENT2 = 2;

	/**
	* test Properties
	*/
    public void testBusiness(  )
    {
        // Initialize an object
        Properties properties = new Properties();
        properties.setKey( KEY1 );
        properties.setValue( VALUE1 );
        properties.setIdenvironnement( IDENVIRONNEMENT1 );

        // Create test
        PropertiesHome.create( properties );
        Optional<Properties> optPropertiesStored = PropertiesHome.findByPrimaryKey( properties.getId( ) );
        Properties propertiesStored = optPropertiesStored.orElse( new Properties ( ) );
        assertEquals( propertiesStored.getKey( ) , properties.getKey( ) );
        assertEquals( propertiesStored.getValue( ) , properties.getValue( ) );
        assertEquals( propertiesStored.getIdenvironnement( ) , properties.getIdenvironnement( ) );

        // Update test
        properties.setKey( KEY2 );
        properties.setValue( VALUE2 );
        properties.setIdenvironnement( IDENVIRONNEMENT2 );
        PropertiesHome.update( properties );
        optPropertiesStored = PropertiesHome.findByPrimaryKey( properties.getId( ) );
        propertiesStored = optPropertiesStored.orElse( new Properties ( ) );
        
        assertEquals( propertiesStored.getKey( ) , properties.getKey( ) );
        assertEquals( propertiesStored.getValue( ) , properties.getValue( ) );
        assertEquals( propertiesStored.getIdenvironnement( ) , properties.getIdenvironnement( ) );

        // List test
        PropertiesHome.getPropertiesList( );

        // Delete test
        PropertiesHome.remove( properties.getId( ) );
        optPropertiesStored = PropertiesHome.findByPrimaryKey( properties.getId( ) );
        propertiesStored = optPropertiesStored.orElse( null );
        assertNull( propertiesStored );
        
    }
    
    
     

}