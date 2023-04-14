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
 * The type Properties jsp bean.
 */
@Controller( controllerJsp = "ManageProperties.jsp", controllerPath = "jsp/admin/plugins/vault/", right = "VAULT_MANAGEMENT" )
public class PropertiesJspBean extends AbstractManageApplicationJspBean<Integer, Properties>
{
    // Templates
    private static final String TEMPLATE_MANAGE_PROPERTIES = "/admin/plugins/vault/manage_properties.html";
    private static final String TEMPLATE_CREATE_PROPERTIES = "/admin/plugins/vault/create_properties.html";
    private static final String TEMPLATE_MODIFY_PROPERTIES = "/admin/plugins/vault/modify_properties.html";

    // Parameters
    private static final String PARAMETER_KEY_PROPERTIES = "key";

    private static final String PARAMETER_ID_ENVIRONNEMENT = "idEnv";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROPERTIES = "vault.manage_properties.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PROPERTIES = "vault.modify_properties.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PROPERTIES = "vault.create_properties.pageTitle";

    // Markers
    private static final String MARK_PROPERTIES_LIST = "properties_list";
    private static final String MARK_PROPERTIES = "properties";

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
    private List<Properties> _listProperties;

    /**
     * Gets manage properties.
     *
     * @param request
     *            the request
     * @return the manage properties
     */
    @View( value = VIEW_MANAGE_PROPERTIES, defaultView = true )
    public String getManageProperties( HttpServletRequest request )
    {
        _properties = null;
        _listProperties = new ArrayList<>( );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        Environnement _environnement = EnvironnementHome.findByPrimaryKey( nId ).get( );

        if ( EnvironnementHome.getPropertiesList( _environnement.getId( ) ) != null )
        {
            _listProperties = ( EnvironnementHome.getPropertiesList( _environnement.getId( ) ) );
        }
        // Map<String, Object> model = getPaginatedListModel( request, MARK_PROPERTIES_LIST, _listIdProperties, JSP_MANAGE_PROPERTIES );
        Map<String, Object> model = getModel( );
        model.put( MARK_PROPERTIES_LIST, _listProperties );
        model.put( EnvironnementJspBean.MARK_ENVIRONNEMENT, EnvironnementHome.findByPrimaryKey( nId ).get( ) );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_PROPERTIES, TEMPLATE_MANAGE_PROPERTIES, model );
    }

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    public void resetListId( )
    {
        _listIdProperties = new ArrayList<>( );
    }

    /**
     * Gets create properties.
     *
     * @param request
     *            the request
     * @return the create properties
     */
    @View( VIEW_CREATE_PROPERTIES )
    public String getCreateProperties( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );

        _properties = ( _properties != null ) ? _properties : new Properties( );

        Map<String, Object> model = getModel( );
        model.put( MARK_PROPERTIES, _properties );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_PROPERTIES ) );
        model.put( EnvironnementJspBean.MARK_ENVIRONNEMENT, EnvironnementHome.findByPrimaryKey( nId ).get( ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_PROPERTIES, TEMPLATE_CREATE_PROPERTIES, model );
    }

    /**
     * Do create properties string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             the access denied exception
     * @throws VaultException
     *             the vault exception
     */
    @Action( ACTION_CREATE_PROPERTIES )
    public String doCreateProperties( HttpServletRequest request ) throws AccessDeniedException, VaultException
    {
        populate( _properties, request, getLocale( ) );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        int nIdApp = EnvironnementHome.findByPrimaryKey( nId ).get( ).getIdapplication( );
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_PROPERTIES ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _properties, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_PROPERTIES );
        }

        PropertiesHome.create( _properties );
        addInfo( INFO_PROPERTIES_CREATED, getLocale( ) );
        resetListId( );

        return redirect( request, VIEW_MANAGE_PROPERTIES, PARAMETER_ID_ENVIRONNEMENT, _properties.getIdenvironnement( ) );
    }

    /**
     * Gets confirm remove properties.
     *
     * @param request
     *            the request
     * @return the confirm remove properties
     */
    @Action( ACTION_CONFIRM_REMOVE_PROPERTIES )
    public String getConfirmRemoveProperties( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        String nKey = request.getParameter( PARAMETER_KEY_PROPERTIES );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PROPERTIES ) );
        url.addParameter( PARAMETER_ID_ENVIRONNEMENT, nId );
        url.addParameter( PARAMETER_KEY_PROPERTIES, nKey );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PROPERTIES, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Do remove properties string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws VaultException
     *             the vault exception
     */
    @Action( ACTION_REMOVE_PROPERTIES )
    public String doRemoveProperties( HttpServletRequest request ) throws VaultException
    {
        int nIdEnv = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        String nCode = request.getParameter( PARAMETER_KEY_PROPERTIES );

        if ( _properties == null )
        {
            Optional<Properties> optProperties = Optional.ofNullable( PropertiesHome.getPropertiesByEnvIdAndCode( nIdEnv, nCode ) );
            _properties = optProperties.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        PropertiesHome.remove( _properties );
        addInfo( INFO_PROPERTIES_REMOVED, getLocale( ) );
        resetListId( );

        return redirect( request, VIEW_MANAGE_PROPERTIES, PARAMETER_ID_ENVIRONNEMENT, nIdEnv );
    }

    /**
     * Gets modify properties.
     *
     * @param request
     *            the request
     * @return the modify properties
     */
    @View( VIEW_MODIFY_PROPERTIES )
    public String getModifyProperties( HttpServletRequest request )
    {
        int nIdEnv = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        String nCode = request.getParameter( PARAMETER_KEY_PROPERTIES );
        if ( _properties == null )
        {
            Optional<Properties> optProperties = Optional.ofNullable( PropertiesHome.getPropertiesByEnvIdAndCode( nIdEnv, nCode ) );
            _properties = optProperties.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        System.out.println( _properties.getKey( ) );
        Map<String, Object> model = getModel( );
        model.put( MARK_PROPERTIES, _properties );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_PROPERTIES ) );
        model.put( EnvironnementJspBean.MARK_ENVIRONNEMENT, EnvironnementHome.findByPrimaryKey( _properties.getIdenvironnement( ) ).get( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_PROPERTIES, TEMPLATE_MODIFY_PROPERTIES, model );
    }

    /**
     * Do modify properties string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             the access denied exception
     * @throws VaultException
     *             the vault exception
     */
    @Action( ACTION_MODIFY_PROPERTIES )
    public String doModifyProperties( HttpServletRequest request ) throws AccessDeniedException, VaultException
    {
        populate( _properties, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_PROPERTIES ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _properties, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
        }
        PropertiesHome.update( _properties );
        addInfo( INFO_PROPERTIES_UPDATED, getLocale( ) );
        resetListId( );

        return redirect( request, VIEW_MANAGE_PROPERTIES, PARAMETER_ID_ENVIRONNEMENT, _properties.getIdenvironnement( ) );
    }

    @Override
    List<Properties> getItemsFromIds( List<Integer> listIds )
    {
        return null;
    }
}
