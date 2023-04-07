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

import fr.paris.lutece.plugins.vault.service.VaultService;
import fr.paris.lutece.plugins.vault.service.VaultUtil;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Environnement objects
 */
public final class EnvironnementHome
{
    // Static variable pointed at the DAO instance
    private static IEnvironnementDAO _dao = SpringContextService.getBean( "vault.environnementDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "vault" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EnvironnementHome(  )
    {

    }



    /**
     * Create an instance of the environnement class
     * @param environnement The instance of the Environnement which contains the informations to store
     * @return The  instance of environnement which has been created with its primary key.
     */
    public static Environnement create( Environnement environnement )
    {
        _dao.insert( environnement, _plugin );

        return environnement;
    }

    /**
     * Update of the environnement which is specified in parameter
     * @param environnement The instance of the Environnement which contains the data to store
     * @return The instance of the  environnement which has been updated
     */
    public static Environnement update( Environnement environnement )
    {
        _dao.store( environnement, _plugin );

        return environnement;
    }

    /**
     * Remove the environnement whose identifier is specified in parameter
     * @param nKey The environnement Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a environnement whose identifier is specified in parameter
     * @param nKey The environnement primary key
     * @return an instance of Environnement
     */
    public static Optional<Environnement> findByPrimaryKey( int nKey )
    {
        Optional<Environnement> env=_dao.load( nKey, _plugin );
    if(env.isPresent())
        {
            env.get().setToken(VaultService.getInstance().getEnvAccessor(env.get().getId()));
        }
        return env;
    }

    /**
     * Load the data of all the environnement objects and returns them as a list
     * @return the list which contains the data of all the environnement objects
     */
    public static List<Environnement> getEnvironnementsList( )
    {
        List<Environnement> envs= _dao.selectEnvironnementsList( _plugin );
        if (!envs.isEmpty()) {
            envs.forEach(x -> x.setToken(VaultService.getInstance().getEnvAccessor(x.getId())));
        }
        return envs;
    }
    
    /**
     * Load the id of all the environnement objects and returns them as a list
     * @return the list which contains the id of all the environnement objects
     */
    public static List<Integer> getIdEnvironnementsList( )
    {
        return _dao.selectIdEnvironnementsList( _plugin );
    }
    
    /**
     * Load the data of all the environnement objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the environnement objects
     */
    public static ReferenceList getEnvironnementsReferenceList( )
    {
        return _dao.selectEnvironnementsReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Environnement> getEnvironnementsListByIds( List<Integer> listIds )
    {
        List<Environnement> listEnvs=_dao.selectEnvironnementsListByIds( _plugin, listIds );
        if (!listEnvs.isEmpty()) {
            listEnvs.forEach(x -> x
                    .setToken(
                            VaultService.getInstance().getEnvAccessor(x.getId())
                    ));
        }
        return listEnvs;
    }


    public static List<Integer> getIdEnvironnementsListByApp(Integer idApp)
    {
        return _dao.selectIdEnvironnementByIdApp(idApp, _plugin);
    }

}

