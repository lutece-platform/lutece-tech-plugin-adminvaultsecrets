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
package fr.paris.lutece.plugins.vault.service;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Auth;
import com.bettercloud.vault.response.AuthResponse;
import fr.paris.lutece.plugins.vault.business.*;
import fr.paris.lutece.plugins.vault.business.Properties;
import fr.paris.lutece.plugins.vault.rs.VaultAPI;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.*;

/**
 * The type Vault service.
 */
public class VaultService
{

    private ReferenceList _listEnvAccessor = new ReferenceList( );
    private List<Properties> _listEnvSecrets;

    private static VaultService _instance = null;
    private Vault _vault;

    /**
     * Instantiates a new Vault service.
     *
     * @param strAdress
     *            the str adress
     * @param strVaultToken
     *            the str vault token
     */
    public VaultService( String strAdress, String strVaultToken )
    {
        init( strAdress, strVaultToken );

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VaultService getInstance( )
    {
        if ( _instance == null )
        {

            _instance = new VaultService( AppPropertiesService.getProperty( "vault.vaultServerAdress" ),
                    AppPropertiesService.getProperty( "vault.rootToken" ) );
        }
        return _instance;
    }

    private void init( String strAdress, String strVaultToken )
    {

        this._vault = VaultUtil.initDriver( strAdress, strVaultToken );

    }

    /**
     * Create environnement token string.
     *
     * @param appCode
     *            the app code
     * @param env
     *            the env
     * @return the string
     * @throws VaultException
     *             the vault exception
     */
    public String createEnvironnementToken( String appCode, Environnement env ) throws VaultException
    {

        // Creation of the policy; combination of appCode and envCode to form the name
        VaultAPI.createPolicy( appCode, env );

        // Auth.TokenRequest is asking for a list of policies so we create it
        List<String> policies = new ArrayList<>( );
        policies.add( appCode.toLowerCase( ) + env.getCode( ).toLowerCase( ) );

        // Create the tokenRequest and adding the list of policies to it
        Auth.TokenRequest tokenRequest = new Auth.TokenRequest( );
        tokenRequest.polices( policies );
        tokenRequest.displayName( appCode + env.getCode( ) );

        // Token creation with the tokenRequest composed of the policy
        AuthResponse vaultToken = _vault.auth( ).createToken( tokenRequest );
        // Adding token accessor to the Environnement Object : can revoke token using accessor
        _listEnvAccessor.addItem( env.getId( ), vaultToken.getTokenAccessor( ) );
        return vaultToken.getAuthClientToken( );

    }

    /**
     * Regenerate token string.
     *
     * @param appCode
     *            the app code
     * @param environnement
     *            the environnement
     * @return the string
     * @throws VaultException
     *             the vault exception
     */
    public String regenerateToken( String appCode, Environnement environnement ) throws VaultException
    {

        VaultAPI.removeTokenJackson( environnement.getToken( ) );
        _listEnvAccessor.remove( VaultService.getInstance( ).getEnvAccessorObject( environnement.getId( ) ) );

        List<String> policies = new ArrayList<>( );
        policies.add( appCode.toLowerCase( ) + environnement.getCode( ).toLowerCase( ) );

        // Create the tokenRequest and adding the list of policies to it
        Auth.TokenRequest tokenRequest = new Auth.TokenRequest( );
        tokenRequest.polices( policies );
        tokenRequest.displayName( appCode + environnement.getCode( ) );

        // Token creation with the tokenRequest composed of the policy
        AuthResponse vaultToken = _vault.auth( ).createToken( tokenRequest );

        // Adding token accessor to the Environnement Object : can revoke token using accessor
        _listEnvAccessor.addItem( environnement.getId( ), vaultToken.getTokenAccessor( ) );
        return vaultToken.getAuthClientToken( );

    }

    public void renameEnvironnement(Application application, Environnement environnement, String strOldCode, String strOldToken) throws VaultException {

        String currentCode = environnement.getCode();
        String currentToken = environnement.getToken();

        List<String> secretList = _vault.logical( ).list( AppPropertiesService.getProperty("vault.secretPath")+"/"+application.getCode()+"/"+strOldCode ).getListData( );
        if ( !secretList.isEmpty( ) )
        {
            secretList.forEach( x -> {
                try
                {
                    final String secretV = _vault.logical( ).read( AppPropertiesService.getProperty("vault.secretPath")+"/"+application.getCode()+"/"+strOldCode + "/" + x ).getData( ).get( x );
                    VaultService.getInstance().writeSecret(x,secretV,application,environnement);
                }
                catch( VaultException e )
                {
                    AppLogService.error( "Erreur pour supprimer l'environnement", e );
                }
            } );
        }
        environnement.setCode(strOldCode);
        environnement.setPath(EnvironnementUtil.getEnvironmentPath(application.getCode(),strOldCode));
        environnement.setToken(strOldToken);
        VaultService.getInstance().removeEnv(environnement.getToken(),application.getCode(),environnement);
        environnement.setCode(currentCode);
        environnement.setToken(currentToken);
    }

    /**
     * Gets env accessor.
     *
     * @param idEnv
     *            the id env
     * @return the env accessor
     */
    public String getEnvAccessor( Integer idEnv )
    {
        if ( !_listEnvAccessor.isEmpty( ) )
        {
            for ( ReferenceItem env : _listEnvAccessor )
            {
                if ( Objects.equals( env.getCode( ), idEnv.toString( ) ) )
                {
                    return env.getName( );
                }
            }
        }
        return null;
    }

    /**
     * Gets env accessor object.
     *
     * @param idEnv
     *            the id env
     * @return the env accessor object
     */
    public ReferenceItem getEnvAccessorObject( Integer idEnv )
    {
        if ( !_listEnvAccessor.isEmpty( ) )
        {
            for ( ReferenceItem env : _listEnvAccessor )
            {
                if ( Objects.equals( env.getCode( ), idEnv.toString( ) ) )
                {
                    return env;
                }
            }
        }
        return null;
    }

    /**
     * Remove env.
     *
     * @param token
     *            the token
     * @param appCode
     *            the app code
     * @param environnement
     *            the environnement
     * @throws VaultException
     *             the vault exception
     */
    public void removeEnv( String token, String appCode, Environnement environnement ) throws VaultException
    {

        List<String> secretList = _vault.logical( ).list( environnement.getPath( ) ).getListData( );
        if ( !secretList.isEmpty( ) )
        {
            secretList.forEach( x -> {
                try
                {
                    _vault.logical( ).delete( environnement.getPath( ) + "/" + x );
                }
                catch( VaultException e )
                {
                    AppLogService.error( "Erreur pour supprimer l'environnement", e );
                }
            } );
        }
        VaultAPI.removePolicy( appCode.toLowerCase( ) + environnement.getCode( ).toLowerCase( ) );
//        VaultAPI.removeToken( token );
        VaultAPI.removeTokenJackson(token);
        _listEnvAccessor.remove( VaultService.getInstance( ).getEnvAccessorObject( environnement.getId( ) ) );

    }


    /**
     * Write secret.
     *
     * @param secret
     *            the secret
     * @param value
     *            the value
     * @param application
     *            the application
     * @param environnement
     *            the environnement
     * @throws VaultException
     *             the vault exception
     */
    public void writeSecret( String secret, String value, Application application, Environnement environnement ) throws VaultException
    {

        if ( secret == null || value == null || application.getCode( ) == null || environnement.getCode( ) == null )
        {
            throw new VaultException( "Paramètres incorrects" );
        }

        final Map<String, Object> secrets = new HashMap<String, Object>( );
        secrets.put( secret, value );

        // Write operation
        _vault.logical( ).write( environnement.getPath( ) + "/" + secret, secrets );

    }

    /**
     * Delete secret.
     *
     * @param secret
     *            the secret
     * @param application
     *            the application
     * @param environnement
     *            the environnement
     * @throws VaultException
     *             the vault exception
     */
    public void deleteSecret( String secret, Application application, Environnement environnement ) throws VaultException
    {

        if ( secret == null || application.getCode( ) == null || environnement.getCode( ) == null )
        {
            throw new VaultException( "Paramètres incorrects" );
        }

        _vault.logical( ).delete( environnement.getPath( ) + "/" + secret );

    }

    /**
     * Update secret.
     *
     * @param secret
     *            the secret
     * @param value
     *            the value
     * @param application
     *            the application
     * @param environnement
     *            the environnement
     * @throws VaultException
     *             the vault exception
     */
    public void updateSecret( String secret, String value, Application application, Environnement environnement ) throws VaultException
    {
        if ( secret == null || value == null || application.getCode( ) == null || environnement.getCode( ) == null )
        {
            throw new VaultException( "Paramètres incorrects" );
        }

        VaultService.getInstance( ).deleteSecret( secret, application, environnement );
        VaultService.getInstance( ).writeSecret( secret, value, application, environnement );
    }

    /**
     * Gets all secrets.
     *
     * @return the all secrets
     */
    public List<String> getAllSecrets( )
    {

        try
        {

            List<String> listAllSecrets = _vault.logical( ).list( AppPropertiesService.getProperty( "vault.secretPath" ) + "/" ).getListData( );
            return listAllSecrets;

        }
        catch( VaultException e )
        {

            AppLogService.error( "Erreur pour récupérer la valeur du secret", e );

        }

        return null;
    }

    /**
     * Gets secrets by env.
     *
     * @param application
     *            the application
     * @param environnement
     *            the environnement
     * @return the secrets by env
     */
    public List<Properties> getSecretsByEnv( Application application, Environnement environnement )
    {

        _listEnvSecrets = new ArrayList<>( );

        try
        {
            final List<String> secretList = _vault.logical( ).list( environnement.getPath( ) ).getListData( );
            for ( int i = 0; i < secretList.size( ); i++ )
            {
                int appId = application.getId( );
                int envId = environnement.getId( );
                Properties _properties = new Properties( );
                _properties.setKey( secretList.get( i ) );
                _properties.setIdenvironnement( envId );
                _properties.setValue( VaultService.getInstance( ).getDetailsSecret( secretList.get( i ), environnement ) );
                _listEnvSecrets.add( _properties );
            }
            return _listEnvSecrets;

        }
        catch( VaultException e )
        {

            AppLogService.error( "Erreur pour récupérer la liste des secretst", e );

        }

        return null;
    }

    /**
     * Gets details secret.
     *
     * @param secretKey
     *            the secret key
     * @param environnement
     *            the environnement
     * @return the details secret
     */
    public String getDetailsSecret( String secretKey, Environnement environnement )
    {

        try
        {

            final String secretKV = _vault.logical( ).read( environnement.getPath( ) + "/" + secretKey ).getData( ).get( secretKey );
            return secretKV;

        }
        catch( VaultException e )
        {

           AppLogService.error( "Erreur pour récupérer la valeur du secret",e);
            e.printStackTrace( );

        }

        return secretKey;
    }

    /**
     * Gets secret.
     *
     * @param secretKey
     *            the secret key
     * @param application
     *            the application
     * @param environnement
     *            the environnement
     * @return the secret
     */
    public Properties getSecret( String secretKey, Application application, Environnement environnement )
    {

        try
        {

            final String secretValue = _vault.logical( ).read( environnement.getPath( ) + "/" + secretKey ).getData( ).get( secretKey );

            Properties secret = new Properties( );
            secret.setIdenvironnement( environnement.getId( ) );
            secret.setKey( secretKey );
            secret.setValue( secretValue );
            return secret;

        }
        catch( VaultException e )
        {

            AppLogService.error( "Erreur pour récupérer la valeur du secret" );
            e.printStackTrace( );

        }

        return null;
    }

}
