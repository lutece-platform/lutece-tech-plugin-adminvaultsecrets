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
package fr.paris.lutece.plugins.vault.rs;

import com.google.gson.Gson;
import fr.paris.lutece.plugins.vault.business.Environnement;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.json.simple.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VaultAPI
{

    public static void createPolicy( String appCode, Environnement environnement )
    {
        try
        {
            JSONObject policy = new JSONObject( );
            policy.put( "policy", "# Manage auth methods broadly across Vault\npath \"secret/data/" + appCode + "/" + environnement.getCode( ) + "/*"
                    + "\"\n{\n  capabilities = [\"read\"]\n}" );

            String baseUrl = AppPropertiesService.getProperty( "vault.vaultServerAdress" );
            String policyPath = AppPropertiesService.getProperty( "vault.addPolicyPath" );
            String policyName = appCode + environnement.getCode( );
            String strUrl = baseUrl + policyPath + policyName;
            URL completeUrl = new URL( strUrl );

            HttpURLConnection httpConnection = (HttpURLConnection) completeUrl.openConnection( );
            httpConnection.setDoOutput( true );
            httpConnection.setRequestMethod( "POST" );
            httpConnection.setRequestProperty( "X-Vault-Token", AppPropertiesService.getProperty( "vault.rootToken" ) );
            httpConnection.setRequestProperty( "Content-Type", "application/json" );
            httpConnection.setRequestProperty( "Accept", "application/json" );

            DataOutputStream wr = new DataOutputStream( httpConnection.getOutputStream( ) );
            wr.write( policy.toString( ).getBytes( ) );
            Integer responseCode = httpConnection.getResponseCode( );
            AppLogService.info( "Policy creation. Statut : " + responseCode );

        }
        catch( Exception e )
        {
            AppLogService.error( "Error creating policy", e );
        }
    }

    public static void removeToken( String accessor )
    {
        try
        {
            JSONObject policy = new JSONObject( );
            policy.put( "accessor", accessor );

            String baseUrl = AppPropertiesService.getProperty( "vault.vaultServerAdress" );
            String tokenPath = AppPropertiesService.getProperty( "vault.addTokenPath" );
            URL completeUrl = new URL( baseUrl + tokenPath );

            HttpURLConnection httpConnection = (HttpURLConnection) completeUrl.openConnection( );
            httpConnection.setDoOutput( true );
            httpConnection.setRequestMethod( "POST" );
            httpConnection.setRequestProperty( "X-Vault-Token", AppPropertiesService.getProperty( "vault.rootToken" ) );
            httpConnection.setRequestProperty( "Content-Type", "application/json" );
            httpConnection.setRequestProperty( "Accept", "application/json" );

            DataOutputStream wr = new DataOutputStream( httpConnection.getOutputStream( ) );
            wr.write( policy.toString( ).getBytes( ) );
            Integer responseCode = httpConnection.getResponseCode( );
            AppLogService.info( "Removing token. Statut : " + responseCode );

        }
        catch( Exception e )
        {
            AppLogService.error( "Error removing token", e );
        }
    }

    public static void removePolicy( String policy )
    {
        try
        {

            String baseUrl = AppPropertiesService.getProperty( "vault.vaultServerAdress" );
            String policyPath = AppPropertiesService.getProperty( "vault.addPolicyPath" );
            String strUrl = baseUrl + policyPath + policy;
            URL completeUrl = new URL( strUrl );

            HttpURLConnection httpConnection = (HttpURLConnection) completeUrl.openConnection( );
            httpConnection.setDoOutput( true );
            httpConnection.setRequestMethod( "DELETE" );
            httpConnection.setRequestProperty( "X-Vault-Token", AppPropertiesService.getProperty( "vault.rootToken" ) );

            Integer responseCode = httpConnection.getResponseCode( );
            AppLogService.info( "Removing policy. Statut : " + responseCode );

        }
        catch( Exception e )
        {
            AppLogService.error( "Error removing policy", e );
        }
    }
}
