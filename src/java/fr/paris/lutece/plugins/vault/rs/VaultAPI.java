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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.paris.lutece.plugins.vault.business.Environnement;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class VaultAPI
{

    public static void createPolicy( String appCode, Environnement environnement )
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode json = objectMapper.createObjectNode();
            json.put("policy","# Manage auth methods broadly across Vault\npath \"secret/data/" + appCode + "/" + environnement.getCode( ) + "/*"
                    + "\"\n{\n  capabilities = [\"read\"]\n}\n\n# Create, update, and delete auth methods\npath \"secret/metadata/" + appCode + "/" + environnement.getCode( ) + "/*\"\n{\n  capabilities = [\"list\"]\n}");

            String baseUrl = AppPropertiesService.getProperty( "vault.vaultServerAdress" );
            String policyPath = AppPropertiesService.getProperty( "vault.addPolicyPath" );
            String policyName = appCode + environnement.getCode( );
            String strUrl = baseUrl + policyPath + policyName;
            HttpPost httpPost = new HttpPost(strUrl);

            httpPost.setHeader("X-Vault-Token", AppPropertiesService.getProperty( "vault.rootToken" ));
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setEntity(new StringEntity(json.toString()));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity!=null) {
                String responseString = EntityUtils.toString(httpEntity);
                AppLogService.info("Vault removing token status : " + responseString);

            }

            response.close();
            httpClient.close();

        }
        catch( Exception e )
        {
            AppLogService.error( "Error creating policy", e );
        }
    }

    public static void removePolicy( String policy )
    {
        try
        {

            String baseUrl = AppPropertiesService.getProperty( "vault.vaultServerAdress" );
            String policyPath = AppPropertiesService.getProperty( "vault.addPolicyPath" );
            String strUrl = baseUrl + policyPath + policy;
            HttpDelete httpDelete = new HttpDelete(strUrl);

            httpDelete.setHeader("X-Vault-Token", AppPropertiesService.getProperty( "vault.rootToken" ));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpDelete);

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity!=null) {
                String responseString = EntityUtils.toString(httpEntity);
                AppLogService.info("Vault removing policy status : " + responseString);

            }

            response.close();
            httpClient.close();

        }
        catch( Exception e )
        {
            AppLogService.error( "Error removing policy", e );
        }
    }

    public static void removeTokenJackson( String accessor )
    {
        if (accessor != null) {
            try
            {
                // Créez l'objet que vous souhaitez envoyer en tant que JSON

                // Créez un objet ObjectMapper pour mapper l'objet Java en JSON
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode json = objectMapper.createObjectNode();
                json.put("accessor",accessor);

                // First open URL connection (using JDK; similar with other libs)
                String baseUrl = AppPropertiesService.getProperty( "vault.vaultServerAdress" );
                String tokenPath = AppPropertiesService.getProperty( "vault.addTokenPath" );
                String completeUrl = baseUrl + tokenPath;
                HttpPost httpPost = new HttpPost(completeUrl);

                httpPost.setHeader("X-Vault-Token", AppPropertiesService.getProperty( "vault.rootToken" ));
                httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                httpPost.setEntity(new StringEntity(json.toString()));

                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(httpPost);

                HttpEntity httpEntity = response.getEntity();
                if (httpEntity!=null) {
                    String responseString = EntityUtils.toString(httpEntity);
                    AppLogService.info("Vault removing token status : " + responseString);

                }

                response.close();
                httpClient.close();

            }
            catch( Exception e )
            {
                AppLogService.error( "Error removing token", e );
            }
        }
    }
}
