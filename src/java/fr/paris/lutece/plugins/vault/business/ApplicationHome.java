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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * The type Application home.
 */
public final class ApplicationHome
{
    // Static variable pointed at the DAO instance
    private static IApplicationDAO _dao = SpringContextService.getBean( "vault.applicationDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "vault" );

    private ApplicationHome( )
    {
    }

    /**
     * Create application.
     *
     * @param application
     *            the application
     * @return the application
     */
    public static Application create( Application application )
    {
        _dao.insert( application, _plugin );

        return application;
    }

    /**
     * Update application.
     *
     * @param application
     *            the application
     * @return the application
     */
    public static Application update( Application application )
    {
        _dao.store( application, _plugin );

        return application;
    }

    /**
     * Remove.
     *
     * @param nKey
     *            the n key
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Find by primary key optional.
     *
     * @param nKey
     *            the n key
     * @return the optional
     */
    public static Optional<Application> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Gets applications list.
     *
     * @return the applications list
     */
    public static List<Application> getApplicationsList( )
    {
        return _dao.selectApplicationsList( _plugin );
    }

    /**
     * Gets id applications list.
     *
     * @return the id applications list
     */
    public static List<Integer> getIdApplicationsList( )
    {
        return _dao.selectIdApplicationsList( _plugin );
    }

    /**
     * Gets applications reference list.
     *
     * @return the applications reference list
     */
    public static ReferenceList getApplicationsReferenceList( )
    {
        return _dao.selectApplicationsReferenceList( _plugin );
    }

    /**
     * Gets applications list by ids.
     *
     * @param listIds
     *            the list ids
     * @return the applications list by ids
     */
    public static List<Application> getApplicationsListByIds( List<Integer> listIds )
    {
        return _dao.selectApplicationsListByIds( _plugin, listIds );
    }

}
