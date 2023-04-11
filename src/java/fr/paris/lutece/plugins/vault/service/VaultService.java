package fr.paris.lutece.plugins.vault.service;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Auth;
import com.bettercloud.vault.response.AuthResponse;
import fr.paris.lutece.plugins.vault.business.Application;
import fr.paris.lutece.plugins.vault.business.Environnement;
import fr.paris.lutece.plugins.vault.business.EnvironnementHome;
import fr.paris.lutece.plugins.vault.business.Properties;
import fr.paris.lutece.plugins.vault.rs.VaultAPI;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.*;

public class VaultService {

    private ReferenceList _listEnvAccessor = new ReferenceList();
    private List<Properties> _listEnvSecrets;

    private static VaultService _instance = null;
    private Vault _vault;

    public VaultService(String strAdress, String strVaultToken) {
        init(strAdress, strVaultToken);

    }

    public static VaultService getInstance() {
        if (_instance == null) {

            _instance = new VaultService(AppPropertiesService.getProperty("vault.vaultServerAdress"), AppPropertiesService.getProperty("vault.rootToken"));
        }
        return _instance;
    }

    private void init(String strAdress, String strVaultToken) {

        this._vault = VaultUtil.initDriver(strAdress, strVaultToken);

    }

    public String createEnvironnementToken(String appCode, String envCode, Environnement env) throws VaultException {

        //Creation of the policy; combination of appCode and envCode to form the name
        VaultAPI.createPolicy(appCode, envCode);

        //Auth.TokenRequest is asking for a list of policies so we create it
        List<String> policies = new ArrayList<>();
        policies.add(appCode.toLowerCase() + envCode.toLowerCase());

        //Create the tokenRequest and adding the list of policies to it
        Auth.TokenRequest tokenRequest = new Auth.TokenRequest();
        tokenRequest.polices(policies);
        tokenRequest.displayName(appCode + envCode);

        //Token creation with the tokenRequest composed of the policy
        AuthResponse vaultToken = _vault.auth().createToken(tokenRequest);

        //Adding token accessor to the Environnement Object : can revoke token using accessor
        _listEnvAccessor.addItem(env.getId(), vaultToken.getTokenAccessor());
        return vaultToken.getAuthClientToken();

    }

    public String regenerateToken(String appCode, String envCode, String accessor, Integer idEnv) throws VaultException {

        VaultAPI.removeToken(accessor);
        _listEnvAccessor.remove(VaultService.getInstance().getEnvAccessorObject(idEnv));

        List<String> policies = new ArrayList<>();
        policies.add(appCode.toLowerCase() + envCode.toLowerCase());

        //Create the tokenRequest and adding the list of policies to it
        Auth.TokenRequest tokenRequest = new Auth.TokenRequest();
        tokenRequest.polices(policies);
        tokenRequest.displayName(appCode + envCode);

        //Token creation with the tokenRequest composed of the policy
        AuthResponse vaultToken = _vault.auth().createToken(tokenRequest);

        //Adding token accessor to the Environnement Object : can revoke token using accessor
        _listEnvAccessor.addItem(idEnv, vaultToken.getTokenAccessor());
        return vaultToken.getAuthClientToken();

    }

    public String getEnvAccessor(Integer idEnv) {
        if (!_listEnvAccessor.isEmpty()) {
            for (ReferenceItem env : _listEnvAccessor) {
                if (Objects.equals(env.getCode(), idEnv.toString())) {
                    return env.getName();
                }
            }
        }
        return null;
    }

    public ReferenceItem getEnvAccessorObject(Integer idEnv) {
        if (!_listEnvAccessor.isEmpty()) {
            for (ReferenceItem env : _listEnvAccessor) {
                if (Objects.equals(env.getCode(), idEnv.toString())) {
                    return env;
                }
            }
        }
        return null;
    }

    public void removeEnv(String token, String appCode, String envCode, Environnement environnement) throws VaultException {

        List<String> secretList = _vault.logical().list("secret/" + appCode + "/" + envCode).getListData();
        if (!secretList.isEmpty()) {
            secretList.forEach(x -> {
                try {
                    _vault.logical()
                            .delete("secret/" + appCode + "/" + envCode + "/" + x);
                } catch (VaultException e) {
                    AppLogService.error("Erreur pour supprimer l'environnement", e);
                }
            });
        }
        VaultAPI.removePolicy(appCode.toLowerCase() + envCode.toLowerCase());
        VaultAPI.removeToken(token);
        _listEnvAccessor.remove(VaultService.getInstance().getEnvAccessorObject(environnement.getId()));

    }

    public void removeToken(String token, String appCode, String envCode) throws VaultException {
        VaultAPI.removeToken(token);
        VaultAPI.removePolicy(appCode.toLowerCase() + envCode.toLowerCase());
    }

    public boolean checkEnvPresence(String appCode, String envCode) throws VaultException {

        return _vault.logical().read("secret" + "/" + appCode + "/" + envCode + "/" + envCode + "Token").getRestResponse().getStatus() == 200;

    }

    public void writeSecret(String secret, String value, String appCode, String envCode) throws VaultException {

        if (secret == null || value == null || appCode == null || envCode == null) {
            throw new VaultException("Paramètres incorrects");
        }

        final Map<String, Object> secrets = new HashMap<String, Object>();
        secrets.put(secret, value);

        // Write operation
        _vault.logical().write("secret/" + appCode + "/" + envCode + "/" + secret, secrets);

    }

    public void deleteSecret(String secret, String appCode, String envCode) throws VaultException {

        if (secret == null || appCode == null || envCode == null) {
            throw new VaultException("Paramètres incorrects");
        }

        _vault.logical().delete("secret/" + appCode + "/" + envCode + "/" + secret);

    }

    public List<String> getAllSecrets() {

        try {

            List<String> listAllSecrets = _vault.logical()
                    .list("/secret/").getListData();
            return listAllSecrets;

        } catch (VaultException e) {

            AppLogService.error("Erreur pour récupérer la valeur du secret", e);

        }

        return null;
    }

    public List<Properties> getSecretsByEnv(Application app, Environnement env) {

        _listEnvSecrets = new ArrayList<>();

        try {
            final List<String> secretList =_vault.logical().list("/secret/" + app.getCode() + "/" + env.getCode()).getListData();
            for(int i=0;i<secretList.size();i++) {
                int appId = app.getId();
                int envId = env.getId();
                String id = app.getId()+""+env.getId()+""+i;
                Properties _properties = new Properties();
                _properties.setId(Integer.parseInt(id));
                _properties.setKey(secretList.get(i));
                _properties.setIdenvironnement(envId);
                _properties.setValue(VaultService.getInstance().getDetailsSecret(secretList.get(i),app.getCode(),env.getCode()));
                _listEnvSecrets.add(_properties);
            }
            return _listEnvSecrets;

        } catch (VaultException e) {

            AppLogService.error("Erreur pour récupérer la liste des secretst", e);

        }

        return null;
    }

    public String getDetailsSecret(String secretKey, String appCode, String envCode) {

        try {

            final String secretKV = _vault.logical()
                    .read("/secret/" + appCode + "/" + envCode + "/" + secretKey)
                    .getData()
                    .get(secretKey);
            return secretKV;

        } catch (VaultException e) {

            System.out.println("Erreur pour récupérer la valeur du secret");
            e.printStackTrace();

        }

        return secretKey;
    }

}



