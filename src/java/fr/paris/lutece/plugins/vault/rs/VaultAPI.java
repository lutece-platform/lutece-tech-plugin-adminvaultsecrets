package fr.paris.lutece.plugins.vault.rs;


import com.google.gson.Gson;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.json.simple.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VaultAPI {

    public static void createPolicy(String appCode, String envCode)
    {
        try {
            JSONObject policy = new JSONObject();
            policy.put(
                    "policy","# Manage auth methods broadly across Vault\npath \"secret/data/" +
                    appCode + "/" + envCode + "/*" + "\"\n{\n  capabilities = [\"read\"]\n}");

            URL baseUrl = new URL("http://127.0.0.1:8200/v1/sys/policies/acl/");
            URL completeUrl = new URL(baseUrl,appCode+envCode);

            HttpURLConnection httpConnection  = (HttpURLConnection) completeUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("X-Vault-Token", AppPropertiesService.getProperty("vault.rootToken"));
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");

            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(policy.toString().getBytes());
            Integer responseCode = httpConnection.getResponseCode();
            System.out.println("###########################################################################################");
            System.out.println(new Gson().toJson(policy));
            System.out.println("Données JSON envoyées. Statut : " + responseCode);
            System.out.println("###########################################################################################");


        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    public static void removeToken(String accessor) {
        try {
            JSONObject policy = new JSONObject();
            policy.put("accessor",accessor);

            URL completeUrl = new URL("http://127.0.0.1:8200/v1/auth/token/revoke-accessor");

            HttpURLConnection httpConnection  = (HttpURLConnection) completeUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("X-Vault-Token", AppPropertiesService.getProperty("vault.rootToken"));
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");

            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(policy.toString().getBytes());
            Integer responseCode = httpConnection.getResponseCode();
            System.out.println("###########################################################################################");
            System.out.println(new Gson().toJson(policy));
            System.out.println("Données JSON envoyées. Statut : " + responseCode);
            System.out.println("###########################################################################################");


        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    public static void removePolicy(String policy) {
        try {

            URL baseUrl = new URL("http://127.0.0.1:8200/v1/sys/policy/");
            URL completeUrl = new URL(baseUrl,policy);

            HttpURLConnection httpConnection  = (HttpURLConnection) completeUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("DELETE");
            httpConnection.setRequestProperty("X-Vault-Token", AppPropertiesService.getProperty("vault.rootToken"));

            Integer responseCode = httpConnection.getResponseCode();
            System.out.println("###########################################################################################");
            System.out.println(new Gson().toJson(policy));
            System.out.println("Données JSON envoyées. Statut : " + responseCode);
            System.out.println("###########################################################################################");

        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}
