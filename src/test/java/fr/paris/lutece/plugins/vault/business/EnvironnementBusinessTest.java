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
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.vault.business;

import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * This is the business class test for the object Environnement
 */
public class EnvironnementBusinessTest extends LuteceTestCase
{
    private static final String CODE1 = "Code1";
    private static final String CODE2 = "Code2";
    private static final String TOKEN1 = "Token1";
    private static final String TOKEN2 = "Token2";
    private static final int IDAPPLICATION1 = 1;
    private static final int IDAPPLICATION2 = 2;

    /**
     * test Environnement
     */
    public void testBusiness( )
    {
        // Initialize an object
        Environnement environnement = new Environnement( );
        environnement.setCode( CODE1 );
        environnement.setToken( TOKEN1 );
        environnement.setIdapplication( IDAPPLICATION1 );

        // Create test
        EnvironnementHome.create( environnement );
        Optional<Environnement> optEnvironnementStored = EnvironnementHome.findByPrimaryKey( environnement.getId( ) );
        Environnement environnementStored = optEnvironnementStored.orElse( new Environnement( ) );
        assertEquals( environnementStored.getCode( ), environnement.getCode( ) );
        assertEquals( environnementStored.getToken( ), environnement.getToken( ) );
        assertEquals( environnementStored.getIdapplication( ), environnement.getIdapplication( ) );

        // Update test
        environnement.setCode( CODE2 );
        environnement.setToken( TOKEN2 );
        environnement.setIdapplication( IDAPPLICATION2 );
        EnvironnementHome.update( environnement );
        optEnvironnementStored = EnvironnementHome.findByPrimaryKey( environnement.getId( ) );
        environnementStored = optEnvironnementStored.orElse( new Environnement( ) );

        assertEquals( environnementStored.getCode( ), environnement.getCode( ) );
        assertEquals( environnementStored.getToken( ), environnement.getToken( ) );
        assertEquals( environnementStored.getIdapplication( ), environnement.getIdapplication( ) );

        // List test
        EnvironnementHome.getEnvironnementsList( );

        // Delete test
        EnvironnementHome.remove( environnement.getId( ) );
        optEnvironnementStored = EnvironnementHome.findByPrimaryKey( environnement.getId( ) );
        environnementStored = optEnvironnementStored.orElse( null );
        assertNull( environnementStored );

    }

}
