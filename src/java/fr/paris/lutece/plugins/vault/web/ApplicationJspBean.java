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
import fr.paris.lutece.plugins.vault.business.Application;
import fr.paris.lutece.plugins.vault.business.ApplicationHome;

/**
 * The type Application jsp bean.
 */
@Controller( controllerJsp = "ManageApplications.jsp", controllerPath = "jsp/admin/plugins/vault/", right = "VAULT_MANAGEMENT" )
public class ApplicationJspBean extends AbstractManageApplicationJspBean<Integer, Application>
{
    // Templates
    private static final String TEMPLATE_MANAGE_APPLICATIONS = "/admin/plugins/vault/manage_applications.html";
    private static final String TEMPLATE_CREATE_APPLICATION = "/admin/plugins/vault/create_application.html";
    private static final String TEMPLATE_MODIFY_APPLICATION = "/admin/plugins/vault/modify_application.html";

    // Parameters
    private static final String PARAMETER_ID_APPLICATION = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPLICATIONS = "vault.manage_applications.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPLICATION = "vault.modify_application.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPLICATION = "vault.create_application.pageTitle";

    // Markers
    private static final String MARK_APPLICATION_LIST = "application_list";
    /**
     * The constant MARK_APPLICATION.
     */
    public static final String MARK_APPLICATION = "application";

    private static final String JSP_MANAGE_APPLICATIONS = "jsp/admin/plugins/vault/ManageApplications.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_APPLICATION = "vault.message.confirmRemoveApplication";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "vault.model.entity.application.attribute.";

    // Views
    private static final String VIEW_MANAGE_APPLICATIONS = "manageApplications";
    private static final String VIEW_CREATE_APPLICATION = "createApplication";
    private static final String VIEW_MODIFY_APPLICATION = "modifyApplication";

    // Actions
    private static final String ACTION_CREATE_APPLICATION = "createApplication";
    private static final String ACTION_MODIFY_APPLICATION = "modifyApplication";
    private static final String ACTION_REMOVE_APPLICATION = "removeApplication";
    private static final String ACTION_CONFIRM_REMOVE_APPLICATION = "confirmRemoveApplication";

    // Infos
    private static final String INFO_APPLICATION_CREATED = "vault.info.application.created";
    private static final String INFO_APPLICATION_UPDATED = "vault.info.application.updated";
    private static final String INFO_APPLICATION_REMOVED = "vault.info.application.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Application _application;
    private List<Integer> _listIdApplications;

    /**
     * Gets manage applications.
     *
     * @param request
     *            the request
     * @return the manage applications
     */
    @View( value = VIEW_MANAGE_APPLICATIONS, defaultView = true )
    public String getManageApplications( HttpServletRequest request )
    {
        _application = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdApplications.isEmpty( ) )
        {
            _listIdApplications = ApplicationHome.getIdApplicationsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_APPLICATION_LIST, _listIdApplications, JSP_MANAGE_APPLICATIONS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPLICATIONS, TEMPLATE_MANAGE_APPLICATIONS, model );
    }

    @Override
    List<Application> getItemsFromIds( List<Integer> listIds )
    {
        List<Application> listApplication = ApplicationHome.getApplicationsListByIds( listIds );

        // keep original order
        return listApplication.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * Reset list id.
     */
    public void resetListId( )
    {
        _listIdApplications = new ArrayList<>( );
    }

    /**
     * Gets create application.
     *
     * @param request
     *            the request
     * @return the create application
     */
    @View( VIEW_CREATE_APPLICATION )
    public String getCreateApplication( HttpServletRequest request )
    {
        _application = ( _application != null ) ? _application : new Application( );

        Map<String, Object> model = getModel( );
        model.put( MARK_APPLICATION, _application );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_APPLICATION ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPLICATION, TEMPLATE_CREATE_APPLICATION, model );
    }

    /**
     * Do create application string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             the access denied exception
     */
    @Action( ACTION_CREATE_APPLICATION )
    public String doCreateApplication( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _application, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_APPLICATION ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _application, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_APPLICATION );
        }

        ApplicationHome.create( _application );
        addInfo( INFO_APPLICATION_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_APPLICATIONS );
    }

    /**
     * Gets confirm remove application.
     *
     * @param request
     *            the request
     * @return the confirm remove application
     */
    @Action( ACTION_CONFIRM_REMOVE_APPLICATION )
    public String getConfirmRemoveApplication( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPLICATION ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPLICATION ) );
        url.addParameter( PARAMETER_ID_APPLICATION, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPLICATION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Do remove application string.
     *
     * @param request
     *            the request
     * @return the string
     */
    @Action( ACTION_REMOVE_APPLICATION )
    public String doRemoveApplication( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPLICATION ) );

        ApplicationHome.remove( nId );
        addInfo( INFO_APPLICATION_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_APPLICATIONS );
    }

    /**
     * Gets modify application.
     *
     * @param request
     *            the request
     * @return the modify application
     */
    @View( VIEW_MODIFY_APPLICATION )
    public String getModifyApplication( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPLICATION ) );

        if ( _application == null || ( _application.getId( ) != nId ) )
        {
            Optional<Application> optApplication = ApplicationHome.findByPrimaryKey( nId );
            _application = optApplication.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_APPLICATION, _application );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_APPLICATION ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPLICATION, TEMPLATE_MODIFY_APPLICATION, model );
    }

    /**
     * Do modify application string.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             the access denied exception
     */
    @Action( ACTION_MODIFY_APPLICATION )
    public String doModifyApplication( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _application, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_APPLICATION ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _application, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_APPLICATION, PARAMETER_ID_APPLICATION, _application.getId( ) );
        }

        ApplicationHome.update( _application );
        addInfo( INFO_APPLICATION_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_APPLICATIONS );
    }
}
