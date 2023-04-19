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
import com.bettercloud.vault.rest.RestException;
import fr.paris.lutece.plugins.vault.business.ApplicationHome;
import fr.paris.lutece.plugins.vault.service.EnvironnementUtil;
import fr.paris.lutece.plugins.vault.service.VaultService;
import fr.paris.lutece.plugins.vault.service.VaultUtil;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.plugins.vault.business.Environnement;
import fr.paris.lutece.plugins.vault.business.EnvironnementHome;

/**
 * The type Environnement jsp bean.
 */
@Controller( controllerJsp = "ManageEnvironnements.jsp", controllerPath = "jsp/admin/plugins/vault/", right = "VAULT_MANAGEMENT" )
public class EnvironnementJspBean extends AbstractManageApplicationJspBean<Integer, Environnement>
{
    // Templates
    private static final String TEMPLATE_MANAGE_ENVIRONNEMENTS = "/admin/plugins/vault/manage_environnements.html";
    private static final String TEMPLATE_CREATE_ENVIRONNEMENT = "/admin/plugins/vault/create_environnement.html";
    private static final String TEMPLATE_MODIFY_ENVIRONNEMENT = "/admin/plugins/vault/modify_environnement.html";

    // Parameters
    private static final String PARAMETER_ID_ENVIRONNEMENT = "id";

    private static final String PARAMETER_ID_APPLICATION = "idApp";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ENVIRONNEMENTS = "vault.manage_environnements.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_ENVIRONNEMENT = "vault.modify_environnement.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_ENVIRONNEMENT = "vault.create_environnement.pageTitle";

    // Markers
    private static final String MARK_ENVIRONNEMENT_LIST = "environnement_list";
    private static final String MARK_ENVIRONNEMENT_NAMES_EXAMPLE = "environnement_names_example";

    /**
     * The constant MARK_ENVIRONNEMENT.
     */
    public static final String MARK_ENVIRONNEMENT = "environnement";

    private static final String JSP_MANAGE_ENVIRONNEMENTS = "jsp/admin/plugins/vault/ManageEnvironnements.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_ENVIRONNEMENT = "vault.message.confirmRemoveEnvironnement";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "vault.model.entity.environnement.attribute.";

    // Views
    private static final String VIEW_MANAGE_ENVIRONNEMENTS = "manageEnvironnements";
    private static final String VIEW_CREATE_ENVIRONNEMENT = "createEnvironnement";
    private static final String VIEW_MODIFY_ENVIRONNEMENT = "modifyEnvironnement";

    // Actions
    private static final String ACTION_CREATE_ENVIRONNEMENT = "createEnvironnement";
    private static final String ACTION_MODIFY_ENVIRONNEMENT = "modifyEnvironnement";
    private static final String ACTION_REMOVE_ENVIRONNEMENT = "removeEnvironnement";
    private static final String ACTION_CONFIRM_REMOVE_ENVIRONNEMENT = "confirmRemoveEnvironnement";
    private static final String ACTION_REGENERATE_TOKEN = "regenerateToken";

    // Infos
    private static final String INFO_ENVIRONNEMENT_CREATED = "vault.info.environnement.created";
    private static final String INFO_TOKEN_REGENERATE = "vault.info.token.regenerate";

    private static final String INFO_ENVIRONNEMENT_UPDATED = "vault.info.environnement.updated";
    private static final String INFO_ENVIRONNEMENT_REMOVED = "vault.info.environnement.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Environnement _environnement;

    private List<Integer> _listIdEnvironnements;
    private ReferenceList _listNamesEnvironnements;

    /**
     * Gets manage environnements.
     *
     * @param request
     *            the request
     * @return the manage environnements
     * @throws VaultException
     *             the vault exception
     */
    @View( value = VIEW_MANAGE_ENVIRONNEMENTS, defaultView = true )
    public String getManageEnvironnements( HttpServletRequest request ) throws VaultException
    {
        _environnement = ( _environnement != null ) ? _environnement : new Environnement( );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPLICATION ) );

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdEnvironnements.isEmpty( ) )
        {
            _listIdEnvironnements = EnvironnementHome.getIdEnvironnementsListByApp( nId );
        }

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listNamesEnvironnements.isEmpty( ) )
        {
            _listNamesEnvironnements = EnvironnementUtil.getEnvironnementNames( getLocale( ) );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_ENVIRONNEMENT_LIST, _listIdEnvironnements, JSP_MANAGE_ENVIRONNEMENTS );
        model.put( ApplicationJspBean.MARK_APPLICATION, ApplicationHome.findByPrimaryKey( nId ).get( ) );
        model.put( MARK_ENVIRONNEMENT_NAMES_EXAMPLE, _listNamesEnvironnements );
        model.put( MARK_ENVIRONNEMENT, _environnement );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_ENVIRONNEMENT ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ENVIRONNEMENTS, TEMPLATE_MANAGE_ENVIRONNEMENTS, model );
    }

    @Override
    List<Environnement> getItemsFromIds( List<Integer> listIds )
    {
        List<Environnement> listEnvironnement = EnvironnementHome.getEnvironnementsListByIds( listIds );

        // keep original order
        return listEnvironnement.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * Reset list id.
     */
    public void resetListId( )
    {
        _listIdEnvironnements = new ArrayList<>( );
    }

    /**
     * Gets create environnement.
     *
     * @param request
     *            the request
     * @return the create environnement
     */
    @View( VIEW_CREATE_ENVIRONNEMENT )
    public String getCreateEnvironnement( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPLICATION ) );

        _environnement = ( _environnement != null ) ? _environnement : new Environnement( );

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listNamesEnvironnements.isEmpty( ) )
        {
            _listNamesEnvironnements = EnvironnementUtil.getEnvironnementNames( getLocale( ) );
        }
        Map<String, Object> model = getModel( );
        model.put( ApplicationJspBean.MARK_APPLICATION, ApplicationHome.findByPrimaryKey( nId ).get( ) );
        model.put( MARK_ENVIRONNEMENT_NAMES_EXAMPLE, _listNamesEnvironnements );
        model.put( MARK_ENVIRONNEMENT, _environnement );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_ENVIRONNEMENT ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_ENVIRONNEMENT, TEMPLATE_CREATE_ENVIRONNEMENT, model );
    }

    /**
     * Do create environnement string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             the access denied exception
     * @throws VaultException
     *             the vault exception
     * @throws RestException
     *             the rest exception
     */
    @Action( ACTION_CREATE_ENVIRONNEMENT )
    public String doCreateEnvironnement( HttpServletRequest request ) throws AccessDeniedException, VaultException, RestException
    {

        populate( _environnement, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_ENVIRONNEMENT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _environnement, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_ENVIRONNEMENT );
        }

        List<Environnement> listEnv = EnvironnementHome.getEnvironnementListByType( _environnement.getType( ) );
        _environnement.setCode( _environnement.getType( ) + listEnv.size( ) );
        EnvironnementHome.create( _environnement );
        addInfo( INFO_ENVIRONNEMENT_CREATED, getLocale( ) );
        String strToken = VaultService.getInstance( )
                .createEnvironnementToken( ApplicationHome.findByPrimaryKey( _environnement.getIdapplication( ) ).get( ).getCode( ), _environnement );
        Object [ ] tabObj = {
                strToken
        };
        addWarning( I18nService.getLocalizedString( "vault.manage_environnement.create_token", tabObj, getLocale( ) ) );

        resetListId( );
        return redirect( request, VIEW_MANAGE_ENVIRONNEMENTS, PARAMETER_ID_APPLICATION, _environnement.getIdapplication( ) );
    }

    /**
     * Do regenerate token string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws VaultException
     *             the vault exception
     */
    @Action( ACTION_REGENERATE_TOKEN )
    public String doRegenerateToken( HttpServletRequest request ) throws VaultException
    {

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );

        if ( _environnement == null || ( _environnement.getId( ) != nId ) )
        {
            Optional<Environnement> optEnvironnement = EnvironnementHome.findByPrimaryKey( nId );
            _environnement = optEnvironnement.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        addInfo( INFO_TOKEN_REGENERATE, getLocale( ) );
        String strToken = VaultService.getInstance( ).regenerateToken( ApplicationHome.findByPrimaryKey( _environnement.getIdapplication( ) ).get( ).getCode( ),
                _environnement );
        Object [ ] tabObj = {
                strToken
        };
        addWarning( I18nService.getLocalizedString( "vault.manage_environnement.create_token", tabObj, getLocale( ) ) );
        return redirect( request, VIEW_MANAGE_ENVIRONNEMENTS, PARAMETER_ID_APPLICATION, _environnement.getIdapplication( ) );
    }

    /**
     * Gets confirm remove environnement.
     *
     * @param request
     *            the request
     * @return the confirm remove environnement
     */
    @Action( ACTION_CONFIRM_REMOVE_ENVIRONNEMENT )
    public String getConfirmRemoveEnvironnement( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_ENVIRONNEMENT ) );
        url.addParameter( PARAMETER_ID_ENVIRONNEMENT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ENVIRONNEMENT, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Do remove environnement string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws VaultException
     *             the vault exception
     */
    @Action( ACTION_REMOVE_ENVIRONNEMENT )
    public String doRemoveEnvironnement( HttpServletRequest request ) throws VaultException
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        int nIdApp = EnvironnementHome.findByPrimaryKey( nId ).get( ).getIdapplication( );

        VaultService.getInstance( ).removeEnv( EnvironnementHome.findByPrimaryKey( nId ).get( ).getToken( ),
                ApplicationHome.findByPrimaryKey( nIdApp ).get( ).getCode( ), EnvironnementHome.findByPrimaryKey( nId ).get( ) );
        EnvironnementHome.remove( nId );
        addInfo( INFO_ENVIRONNEMENT_REMOVED, getLocale( ) );
        resetListId( );

        return redirect( request, VIEW_MANAGE_ENVIRONNEMENTS, PARAMETER_ID_APPLICATION, nIdApp );
    }

    /**
     * Gets modify environnement.
     *
     * @param request
     *            the request
     * @return the modify environnement
     * @throws VaultException
     *             the vault exception
     */
    @View( VIEW_MODIFY_ENVIRONNEMENT )
    public String getModifyEnvironnement( HttpServletRequest request ) throws VaultException
    {
         int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );

        if ( _environnement == null || ( _environnement.getId( ) != nId ) )
        {
            Optional<Environnement> optEnvironnement = EnvironnementHome.findByPrimaryKey( nId );
            _environnement = optEnvironnement.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listNamesEnvironnements.isEmpty( ) )
        {
            _listNamesEnvironnements = EnvironnementUtil.getEnvironnementNames( getLocale( ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_ENVIRONNEMENT, _environnement );
        model.put( ApplicationJspBean.MARK_APPLICATION, ApplicationHome.findByPrimaryKey( _environnement.getIdapplication( ) ).get( ) );
        model.put( MARK_ENVIRONNEMENT_NAMES_EXAMPLE, _listNamesEnvironnements );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_ENVIRONNEMENT ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_ENVIRONNEMENT, TEMPLATE_MODIFY_ENVIRONNEMENT, model );
    }

    /**
     * Do modify environnement string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             the access denied exception
     */
    @Action( ACTION_MODIFY_ENVIRONNEMENT )
    public String doModifyEnvironnement( HttpServletRequest request ) throws AccessDeniedException, VaultException {

        String strOldCode = request.getParameter( "oldCode" );
        String strOldToken = request.getParameter( "oldToken" );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENVIRONNEMENT ) );
        int nIdApp = EnvironnementHome.findByPrimaryKey( nId ).get( ).getIdapplication( );

        populate( _environnement, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_ENVIRONNEMENT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _environnement, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_ENVIRONNEMENT, PARAMETER_ID_ENVIRONNEMENT, _environnement.getId( ) );
        }

        addInfo( INFO_ENVIRONNEMENT_UPDATED, getLocale( ) );


        String strToken = VaultService.getInstance( )
                .createEnvironnementToken( ApplicationHome.findByPrimaryKey( _environnement.getIdapplication( ) ).get( ).getCode( ), _environnement );
        Object [ ] tabObj = {
                strToken
        };
        addWarning( I18nService.getLocalizedString( "vault.manage_environnement.create_token", tabObj, getLocale( ) ) );

        EnvironnementHome.update( _environnement, strOldCode, strOldToken );

        resetListId( );

        return redirect( request, VIEW_MANAGE_ENVIRONNEMENTS, PARAMETER_ID_APPLICATION, _environnement.getIdapplication( ) );

    }
}
