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

import fr.paris.lutece.plugins.vault.service.EnvironnementUtil;
import fr.paris.lutece.plugins.vault.service.VaultService;
import fr.paris.lutece.plugins.vault.service.VaultUtil;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * The type Environnement home.
 */
public final class EnvironnementHome
{
    // Static variable pointed at the DAO instance
    private static IEnvironnementDAO _dao = SpringContextService.getBean( "vault.environnementDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "vault" );

    private static Integer _count = 1;

    private EnvironnementHome( )
    {

    }

    /**
     * Create environnement.
     *
     * @param environnement
     *            the environnement
     * @return the environnement
     */
    public static Environnement create( Environnement environnement )
    {
        List<Environnement> listEnv = EnvironnementHome.getEnvironnementListByType( environnement.getType( ) );
        environnement.setCode( environnement.getType( ) + listEnv.size( ) );
        _dao.insert( environnement, _plugin );

        return environnement;
    }

    /**
     * Update environnement.
     *
     * @param environnement
     *            the environnement
     * @return the environnement
     */
    public static Environnement update( Environnement environnement )
    {
        _dao.store( environnement, _plugin );

        return environnement;
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
    public static Optional<Environnement> findByPrimaryKey( int nKey )
    {
        Optional<Environnement> env = _dao.load( nKey, _plugin );
        Application app = ApplicationHome.findByPrimaryKey( env.get( ).getIdapplication( ) ).get( );
        if ( env.isPresent( ) )
        {
            env.get( ).setType( env.get( ).getCode( ) );
            env.get( ).setToken( VaultService.getInstance( ).getEnvAccessor( env.get( ).getId( ) ) );
            env.get( ).setPath( EnvironnementUtil.getEnvironmentPath( app.getCode( ), env.get( ).getCode( ) ) );
            env.get( ).setListProperties( VaultService.getInstance( ).getSecretsByEnv( app, env.get( ) ) );
        }
        return env;
    }

    /**
     * Gets environnements list.
     *
     * @return the environnements list
     */
    public static List<Environnement> getEnvironnementsList( )
    {
        List<Environnement> envs = _dao.selectEnvironnementsList( _plugin );
        if ( !envs.isEmpty( ) )
        {
            envs.forEach( x -> x.setToken( VaultService.getInstance( ).getEnvAccessor( x.getId( ) ) ) );
            envs.forEach( x -> x.setPath(
                    EnvironnementUtil.getEnvironmentPath( ApplicationHome.findByPrimaryKey( x.getIdapplication( ) ).get( ).getCode( ), x.getCode( ) ) ) );
            envs.forEach( x -> x
                    .setListProperties( VaultService.getInstance( ).getSecretsByEnv( ApplicationHome.findByPrimaryKey( x.getIdapplication( ) ).get( ), x ) ) );

        }
        return envs;
    }

    /**
     * Gets id environnements list.
     *
     * @return the id environnements list
     */
    public static List<Integer> getIdEnvironnementsList( )
    {
        return _dao.selectIdEnvironnementsList( _plugin );
    }

    /**
     * Gets environnements reference list.
     *
     * @return the environnements reference list
     */
    public static ReferenceList getEnvironnementsReferenceList( )
    {
        return _dao.selectEnvironnementsReferenceList( _plugin );
    }

    /**
     * Gets environnements list by ids.
     *
     * @param listIds
     *            the list ids
     * @return the environnements list by ids
     */
    public static List<Environnement> getEnvironnementsListByIds( List<Integer> listIds )
    {
        List<Environnement> listEnvs = _dao.selectEnvironnementsListByIds( _plugin, listIds );
        if ( !listEnvs.isEmpty( ) )
        {
            listEnvs.forEach( x -> {
                x.setPath( EnvironnementUtil.getEnvironmentPath( ApplicationHome.findByPrimaryKey( x.getIdapplication( ) ).get( ).getCode( ), x.getCode( ) ) );
            } );
        }
        return listEnvs;
    }

    /**
     * Gets id environnements list by app.
     *
     * @param idApp
     *            the id app
     * @return the id environnements list by app
     */
    public static List<Integer> getIdEnvironnementsListByApp( Integer idApp )
    {
        return _dao.selectIdEnvironnementByIdApp( idApp, _plugin );
    }

    /**
     * Gets properties list.
     *
     * @param nIdEnv
     *            the n id env
     * @return the properties list
     */
    public static List<Properties> getPropertiesList( Integer nIdEnv )
    {
        Environnement env = EnvironnementHome.findByPrimaryKey( nIdEnv ).get( );
        Application app = ApplicationHome.findByPrimaryKey( env.getIdapplication( ) ).get( );
        return VaultService.getInstance( ).getSecretsByEnv( app, env );
    }

    /**
     * Gets environnement list by type.
     *
     * @param type
     *            the type
     * @return the environnement list by type
     */
    public static List<Environnement> getEnvironnementListByType( String type )
    {
        return _dao.selectEnvironnementByType( type, _plugin );
    }

}
