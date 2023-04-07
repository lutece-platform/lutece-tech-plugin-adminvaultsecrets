package fr.paris.lutece.plugins.vault.service;

import java.io.Serializable;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import fr.paris.lutece.portal.service.util.AppLogService;

public class VaultUtil implements Serializable {
    private static Vault _vault = null;

    public static Vault initDriver(String strAdress, String strRootToken) {

        try {
            VaultConfig config = new VaultConfig()
                    .address(strAdress)
                    .token(strRootToken)
                    .build();

            _vault = new Vault(config);

        } catch (VaultException e) {

            AppLogService.error("Erreur pour se connecter au serveur Vault", e);

        }

        return _vault;
    }

}
