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
 * This class provides Data Access methods for Properties objects
 */
public final class PropertiesDAO implements IPropertiesDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_properties, labelkey, value, idenvironnement FROM vault_properties WHERE id_properties = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO vault_properties ( labelkey, value, idenvironnement ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM vault_properties WHERE id_properties = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE vault_properties SET labelkey = ?, value = ?, idenvironnement = ? WHERE id_properties = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_properties, labelkey, value, idenvironnement FROM vault_properties";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_properties FROM vault_properties";
	private static final String SQL_QUERY_SELECTALL_IDENV = "SELECT id_properties FROM vault_properties WHERE idenvironnement = ?";

	private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_properties, labelkey, value, idenvironnement FROM vault_properties WHERE id_properties IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Properties properties, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , properties.getKey( ) );
            daoUtil.setString( nIndex++ , properties.getValue( ) );
            daoUtil.setInt( nIndex++ , properties.getIdenvironnement( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                properties.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Properties> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        Properties properties = null;
	
	        if ( daoUtil.next( ) )
	        {
	            properties = new Properties();
	            int nIndex = 1;
	            
	            properties.setId( daoUtil.getInt( nIndex++ ) );
			    properties.setKey( daoUtil.getString( nIndex++ ) );
			    properties.setValue( daoUtil.getString( nIndex++ ) );
			    properties.setIdenvironnement( daoUtil.getInt( nIndex ) );
	        }
	
	        return Optional.ofNullable( properties );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Properties properties, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            	daoUtil.setString( nIndex++ , properties.getKey( ) );
            	daoUtil.setString( nIndex++ , properties.getValue( ) );
            	daoUtil.setInt( nIndex++ , properties.getIdenvironnement( ) );
	        daoUtil.setInt( nIndex , properties.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Properties> selectPropertiesList( Plugin plugin )
    {
        List<Properties> propertiesList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            Properties properties = new Properties(  );
	            int nIndex = 1;
	            
	            properties.setId( daoUtil.getInt( nIndex++ ) );
			    properties.setKey( daoUtil.getString( nIndex++ ) );
			    properties.setValue( daoUtil.getString( nIndex++ ) );
			    properties.setIdenvironnement( daoUtil.getInt( nIndex ) );
	
	            propertiesList.add( properties );
	        }
	
	        return propertiesList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdPropertiesList( Plugin plugin )
    {
        List<Integer> propertiesList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            propertiesList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return propertiesList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectPropertiesReferenceList( Plugin plugin )
    {
        ReferenceList propertiesList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            propertiesList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return propertiesList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<Properties> selectPropertiesListByIds( Plugin plugin, List<Integer> listIds ) {
		List<Properties> propertiesList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( Integer n : listIds ) {
					daoUtil.setInt(  index++, n ); 
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		        	Properties properties = new Properties(  );
		            int nIndex = 1;
		            
		            properties.setId( daoUtil.getInt( nIndex++ ) );
				    properties.setKey( daoUtil.getString( nIndex++ ) );
				    properties.setValue( daoUtil.getString( nIndex++ ) );
				    properties.setIdenvironnement( daoUtil.getInt( nIndex ) );
		            
		            propertiesList.add( properties );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return propertiesList;
		
	}

	@Override
	public List<Integer> selectIdPropertiesByIdEnv(int nKey, Plugin plugin) {
		List<Integer> propertiesList = new ArrayList<>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_IDENV, plugin ) )
		{
			daoUtil.setInt( 1 , nKey );
			daoUtil.executeQuery( );
			while ( daoUtil.next(  ) )
			{
				propertiesList.add( daoUtil.getInt( 1 ) );
			}

			return propertiesList;
		}
	}

}
