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
 	
 
package fr.paris.lutece.plugins.vault.web;

import com.bettercloud.vault.VaultException;
import fr.paris.lutece.plugins.vault.business.*;
import fr.paris.lutece.plugins.vault.service.VaultService;
import fr.paris.lutece.plugins.vault.service.VaultUtil;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Properties features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageProperties.jsp", controllerPath = "jsp/admin/plugins/vault/", right = "VAULT_MANAGEMENT" )
public class PropertiesJspBean extends AbstractManageApplicationJspBean <Integer, Properties>
{
    // Templates
    private static final String TEMPLATE_MANAGE_PROPERTIES = "/admin/plugins/vault/manage_properties.html";
    private static final String TEMPLATE_CREATE_PROPERTIES = "/admin/plugins/vault/create_properties.html";
    private static final String TEMPLATE_MODIFY_PROPERTIES = "/admin/plugins/vault/modify_properties.html";

    // Parameters
    private static final String PARAMETER_ID_PROPERTIES = "id";
    private static final String PARAMETER_ID_ENVIRONNEMENT = "idEnv";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROPERTIES = "vault.manage_properties.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PROPERTIES = "vault.modify_properties.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PROPERTIES = "vault.create_properties.pageTitle";

    // Markers
    private static final String MARK_PROPERTIES_LIST = "properties_list";
    private static final String MARK_PROPERTIES = "properties";

    private static final String JSP_MANAGE_PROPERTIES = "jsp/admin/plugins/vault/ManageProperties.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_PROPERTIES = "vault.message.confirmRemoveProperties";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "vault.model.entity.properties.attribute.";

    // Views
    private static final String VIEW_MANAGE_PROPERTIES = "manageProperties";
    private static final String VIEW_CREATE_PROPERTIES = "createProperties";
    private static final String VIEW_MODIFY_PROPERTIES = "modifyProperties";

    // Actions
    private static final String ACTION_CREATE_PROPERTIES = "createProperties";
    private static final String ACTION_MODIFY_PROPERTIES = "modifyProperties";
    private static final String ACTION_REMOVE_PROPERTIES = "removeProperties";
    private static final String ACTION_CONFIRM_REMOVE_PROPERTIES = "confirmRemoveProperties";

    // Infos
    private static final String INFO_PROPERTIES_CREATED = "vault.info.properties.created";
    private static final String INFO_PROPERTIES_UPDATED = "vault.info.properties.updated";
    private static final String INFO_PROPERTIES_REMOVED = "vault.info.properties.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Properties _properties;
    private List<Integer> _listIdProperties;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_PROPERTIES, defaultView = true )
    public String getManageProperties( HttpServletRequest request )
    {
        _properties = null;

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        Application _application = ApplicationHome.findByPrimaryKey(EnvironnementHome.findByPrimaryKey(nId).get().getIdapplication()).get();
        Environnement _environnement = EnvironnementHome.findByPrimaryKey(nId).get();

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdProperties.isEmpty( ) )
        {
        	_listIdProperties = PropertiesHome.getIdPropertiesListByEnv( nId );
        }

        System.out.println(VaultService.getInstance().getSecretsByEnv(_application, _environnement));
        VaultService.getInstance().getSecretsByEnv(_application, _environnement).forEach(x->System.out.println(x.getId() + " - " + x.getValue()));
        Map<String, Object> model = getPaginatedListModel( request, MARK_PROPERTIES_LIST, _listIdProperties, JSP_MANAGE_PROPERTIES );
        model.put(EnvironnementJspBean.MARK_ENVIRONNEMENT, EnvironnementHome.findByPrimaryKey(nId).get());
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_PROPERTIES, TEMPLATE_MANAGE_PROPERTIES, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Properties> getItemsFromIds( List<Integer> listIds ) 
	{
		List<Properties> listProperties = PropertiesHome.getPropertiesListByIds( listIds );
		
		// keep original order
        return listProperties.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
    
    /**
    * reset the _listIdProperties list
    */
    public void resetListId( )
    {
    	_listIdProperties = new ArrayList<>( );
    }

    /**
     * Returns the form to create a properties
     *
     * @param request The Http request
     * @return the html code of the properties form
     */
    @View( VIEW_CREATE_PROPERTIES )
    public String getCreateProperties( HttpServletRequest request )
    {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_ENVIRONNEMENT));

        _properties = ( _properties != null ) ? _properties : new Properties(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROPERTIES, _properties );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_PROPERTIES ) );
        model.put(EnvironnementJspBean.MARK_ENVIRONNEMENT, EnvironnementHome.findByPrimaryKey(nId).get());

        return getPage( PROPERTY_PAGE_TITLE_CREATE_PROPERTIES, TEMPLATE_CREATE_PROPERTIES, model );
    }

    /**
     * Process the data capture form of a new properties
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_PROPERTIES )
    public String doCreateProperties( HttpServletRequest request ) throws AccessDeniedException, VaultException {
        populate( _properties, request, getLocale( ) );

        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_ENVIRONNEMENT));
        int nIdApp = EnvironnementHome.findByPrimaryKey(nId).get().getIdapplication();
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_PROPERTIES ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _properties, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_PROPERTIES );
        }

        PropertiesHome.create( _properties );
        addInfo( INFO_PROPERTIES_CREATED, getLocale(  ) );
        VaultService.getInstance().writeSecret(_properties.getKey(),_properties.getValue(), ApplicationHome.findByPrimaryKey(nIdApp).get().getCode(), EnvironnementHome.findByPrimaryKey(nId).get().getCode());
        resetListId( );

        return redirect( request, VIEW_MANAGE_PROPERTIES, PARAMETER_ID_ENVIRONNEMENT, _properties.getIdenvironnement());
    }

    /**
     * Manages the removal form of a properties whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_PROPERTIES )
    public String getConfirmRemoveProperties( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROPERTIES ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PROPERTIES ) );
        url.addParameter( PARAMETER_ID_PROPERTIES, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PROPERTIES, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a properties
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage properties
     */
    @Action( ACTION_REMOVE_PROPERTIES )
    public String doRemoveProperties( HttpServletRequest request ) throws VaultException {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROPERTIES ) );
        int nIdEnv = PropertiesHome.findByPrimaryKey(nId).get().getIdenvironnement();
        int nIdApp = EnvironnementHome.findByPrimaryKey(nIdEnv).get().getIdapplication();

        if ( _properties == null || ( _properties.getId(  ) != nId ) )
        {
            Optional<Properties> optProperties = PropertiesHome.findByPrimaryKey( nId );
            _properties = optProperties.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }

        VaultService.getInstance().deleteSecret(_properties.getKey(), ApplicationHome.findByPrimaryKey(nIdApp).get().getCode(), EnvironnementHome.findByPrimaryKey(nIdEnv).get().getCode());
        PropertiesHome.remove( nId );
        addInfo( INFO_PROPERTIES_REMOVED, getLocale(  ) );
        resetListId( );

        return redirect( request, VIEW_MANAGE_PROPERTIES, PARAMETER_ID_ENVIRONNEMENT, nIdEnv);
    }

    /**
     * Returns the form to update info about a properties
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_PROPERTIES )
    public String getModifyProperties( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROPERTIES ) );

        if ( _properties == null || ( _properties.getId(  ) != nId ) )
        {
            Optional<Properties> optProperties = PropertiesHome.findByPrimaryKey( nId );
            _properties = optProperties.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_PROPERTIES, _properties );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_PROPERTIES ) );
        model.put(EnvironnementJspBean.MARK_ENVIRONNEMENT, EnvironnementHome.findByPrimaryKey(_properties.getIdenvironnement()).get());


        return getPage( PROPERTY_PAGE_TITLE_MODIFY_PROPERTIES, TEMPLATE_MODIFY_PROPERTIES, model );
    }

    /**
     * Process the change form of a properties
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_PROPERTIES )
    public String doModifyProperties( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _properties, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_PROPERTIES ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _properties, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_PROPERTIES, PARAMETER_ID_PROPERTIES, _properties.getId( ) );
        }

        PropertiesHome.update( _properties );
        addInfo( INFO_PROPERTIES_UPDATED, getLocale(  ) );
        resetListId( );

        return redirect( request, VIEW_MANAGE_PROPERTIES, PARAMETER_ID_ENVIRONNEMENT, _properties.getIdenvironnement());
    }
}
