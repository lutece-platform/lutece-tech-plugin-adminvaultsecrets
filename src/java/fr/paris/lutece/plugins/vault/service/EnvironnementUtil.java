package fr.paris.lutece.plugins.vault.service;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.ReferenceList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnvironnementUtil {

    public static ReferenceList getEnvironnementNames(Locale locale) {

        String listEnv=AppPropertiesService.getProperty("vault.environnement.list");

        String[] envs= listEnv.split(",");
        ReferenceList _listNamesEnvironnements = new ReferenceList();

       for(int i=0;i<envs.length;i++)
       {
           _listNamesEnvironnements.addItem(envs[i], I18nService.getLocalizedString("vault.manage_environnement.examples."+envs[i],locale));
       }

       return _listNamesEnvironnements;

    }

}
