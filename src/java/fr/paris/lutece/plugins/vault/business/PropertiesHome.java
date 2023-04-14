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

import com.bettercloud.vault.VaultException;
import fr.paris.lutece.plugins.vault.service.VaultService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * The type Properties home.
 */
public final class PropertiesHome
{
    // Static variable pointed at the DAO instance
    private static Plugin _plugin = PluginService.getPlugin( "vault" );

    private PropertiesHome( )
    {
    }

    /**
     * Create properties.
     *
     * @param properties
     *            the properties
     * @return the properties
     * @throws VaultException
     *             the vault exception
     */
    public static Properties create( Properties properties ) throws VaultException
    {
        Environnement env = EnvironnementHome.findByPrimaryKey( properties.getIdenvironnement( ) ).get( );
        Application app = ApplicationHome.findByPrimaryKey( env.getIdapplication( ) ).get( );
        VaultService.getInstance( ).writeSecret( properties.getKey( ), properties.getValue( ), app, env );

        return properties;
    }

    /**
     * Update properties.
     *
     * @param properties
     *            the properties
     * @return the properties
     * @throws VaultException
     *             the vault exception
     */
    public static Properties update( Properties properties ) throws VaultException
    {
        Environnement env = EnvironnementHome.findByPrimaryKey( properties.getIdenvironnement( ) ).get( );
        Application app = ApplicationHome.findByPrimaryKey( env.getIdapplication( ) ).get( );
        VaultService.getInstance( ).updateSecret( properties.getKey( ), properties.getValue( ), app, env );

        return properties;
    }

    /**
     * Remove.
     *
     * @param properties
     *            the properties
     * @throws VaultException
     *             the vault exception
     */
    public static void remove( Properties properties ) throws VaultException
    {
        Environnement env = EnvironnementHome.findByPrimaryKey( properties.getIdenvironnement( ) ).get( );
        Application app = ApplicationHome.findByPrimaryKey( env.getIdapplication( ) ).get( );
        VaultService.getInstance( ).deleteSecret( properties.getKey( ), app, env );
    }

    /**
     * Gets properties by env id and code.
     *
     * @param idEnv
     *            the id env
     * @param key
     *            the key
     * @return the properties by env id and code
     */
    public static Properties getPropertiesByEnvIdAndCode( Integer idEnv, String key )
    {
        Environnement env = EnvironnementHome.findByPrimaryKey( idEnv ).get( );
        Application app = ApplicationHome.findByPrimaryKey( env.getIdapplication( ) ).get( );
        return VaultService.getInstance( ).getSecret( key, app, env );
    }

}
