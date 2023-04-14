![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=plugin-vault-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-vault&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-vault)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-vault&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-vault)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-vault&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-vault)

# Plugin vault

## Introduction

This plugin allows the creation, modification, and deletion of confidential data stored in the HashiCorp Vault. Thanks to this plugin, you will be able to create applications in which you will be able to initialize and manage working environments,in order to store your data.

To facilitate the dialog with the Vault server, the plugin uses the [Java Vault Driver](https://github.com/BetterCloud/vault-java-driver) library from Bettercloud which comes in and creates all the requests to query the Vault APIs.

## Configuration

Configure the plugin properties file (webapp/WEB-INF/conf/plugins/vault.properties).

In particular, it is necessary to set up :
 
* vault.rootToken – Root token generated at the creation of your Vault server
* vault.environnement.list – List of keys corresponding to the predefined environment names. The value of these keys must then be defined in your I18N properties.
* vault.vaultServerAdress – Address of the Vault servert
* vault.addPolicyPath – Policy creation path via API. Default : « /v1/sys/policies/acl/ »
* vault.addTokenPath – Token creation path via API. Default : « /v1/auth/token/revoke-accessor »
* vault.secretPath – Main folder where applications, environments, and secrets are created. Default : « /secret »


```

    vault.rootToken=hvs.lSjuVlF7RSEXpSyLOsel2bVc
    vault.environnement.list=pr,ppr,dev,infra
    vault.vaultServerAdress=http://127.0.0.1:8200
    vault.addPolicyPath=/v1/sys/policies/acl/
    vault.addTokenPath=/v1/auth/token/revoke-accessor
    vault.secretPath=/secret

```

## Usage




[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-vault/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*