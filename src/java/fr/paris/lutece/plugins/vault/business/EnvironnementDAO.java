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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Environnement dao.
 */
public final class EnvironnementDAO implements IEnvironnementDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_environnement, type, code, idapplication FROM vault_environnement WHERE id_environnement = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO vault_environnement ( type, code, idapplication ) VALUES ( ?, ?, ?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM vault_environnement WHERE id_environnement = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE vault_environnement SET type = ?, code = ?, idapplication = ? WHERE id_environnement = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_environnement, type, code, idapplication FROM vault_environnement";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_environnement FROM vault_environnement";
    private static final String SQL_QUERY_SELECTALL_IDAPP = "SELECT id_environnement FROM vault_environnement where idapplication = ?";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_environnement, type, code, idapplication FROM vault_environnement WHERE id_environnement IN (  ";
    private static final String SQL_QUERY_SELECTALL_BY_TYPE = "SELECT id_environnement, type, code, idapplication FROM vault_environnement WHERE type = ?";

    @Override
    public void insert( Environnement environnement, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, environnement.getType( ) );
            daoUtil.setString( nIndex++, environnement.getCode( ) );
            daoUtil.setInt( nIndex, environnement.getIdapplication( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                environnement.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    @Override
    public Optional<Environnement> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            Environnement environnement = null;

            if ( daoUtil.next( ) )
            {
                environnement = new Environnement( );
                int nIndex = 1;

                environnement.setId( daoUtil.getInt( nIndex++ ) );
                environnement.setType( daoUtil.getString( nIndex++ ) );
                environnement.setCode( daoUtil.getString( nIndex++ ) );
                environnement.setIdapplication( daoUtil.getInt( nIndex ) );
            }

            return Optional.ofNullable( environnement );
        }
    }

    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void store( Environnement environnement, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, environnement.getType( ) );
            daoUtil.setString( nIndex++, environnement.getCode( ) );
            daoUtil.setInt( nIndex++, environnement.getIdapplication( ) );
            daoUtil.setInt( nIndex, environnement.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<Environnement> selectEnvironnementsList( Plugin plugin )
    {
        List<Environnement> environnementList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Environnement environnement = new Environnement( );
                int nIndex = 1;

                environnement.setId( daoUtil.getInt( nIndex++ ) );
                environnement.setType( daoUtil.getString( nIndex++ ) );
                environnement.setCode( daoUtil.getString( nIndex++ ) );
                environnement.setIdapplication( daoUtil.getInt( nIndex ) );

                environnementList.add( environnement );
            }

            return environnementList;
        }
    }

    @Override
    public List<Integer> selectIdEnvironnementsList( Plugin plugin )
    {
        List<Integer> environnementList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                environnementList.add( daoUtil.getInt( 1 ) );
            }

            return environnementList;
        }
    }

    @Override
    public ReferenceList selectEnvironnementsReferenceList( Plugin plugin )
    {
        ReferenceList environnementList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                environnementList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return environnementList;
        }
    }

    @Override
    public List<Environnement> selectEnvironnementsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<Environnement> environnementList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    Environnement environnement = new Environnement( );
                    int nIndex = 1;

                    environnement.setId( daoUtil.getInt( nIndex++ ) );
                    environnement.setType( daoUtil.getString( nIndex++ ) );
                    environnement.setCode( daoUtil.getString( nIndex++ ) );
                    environnement.setIdapplication( daoUtil.getInt( nIndex ) );

                    environnementList.add( environnement );
                }

                daoUtil.free( );

            }
        }
        return environnementList;

    }

    @Override
    public List<Integer> selectIdEnvironnementByIdApp( int nKey, Plugin plugin )
    {
        List<Integer> environnementList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_IDAPP, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                environnementList.add( daoUtil.getInt( 1 ) );
            }

            return environnementList;
        }
    }

    @Override
    public List<Environnement> selectEnvironnementByType( String nKey, Plugin plugin )
    {
        List<Environnement> environnementList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_TYPE, plugin ) )
        {
            daoUtil.setString( 1, nKey );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Environnement environnement = new Environnement( );
                int nIndex = 1;

                environnement.setId( daoUtil.getInt( nIndex++ ) );
                environnement.setType( daoUtil.getString( nIndex++ ) );
                environnement.setCode( daoUtil.getString( nIndex++ ) );
                environnement.setIdapplication( daoUtil.getInt( nIndex ) );

                environnementList.add( environnement );
            }

            return environnementList;
        }

    }

}
