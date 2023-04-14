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

import fr.paris.lutece.plugins.vault.business.EnvironnementHome;
import fr.paris.lutece.plugins.vault.business.Properties;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import java.util.List;
import java.io.IOException;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.plugins.vault.business.PropertiesHome;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * This is the business class test for the object Properties
 */
public class PropertiesJspBeanTest extends LuteceTestCase
{
    private static final String KEY1 = "Key1";
    private static final String KEY2 = "Key2";
    private static final String VALUE1 = "Value1";
    private static final String VALUE2 = "Value2";
    private static final int IDENVIRONNEMENT1 = 1;
    private static final int IDENVIRONNEMENT2 = 2;

    public void testJspBeans( ) throws AccessDeniedException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "idEnv", String.valueOf( IDENVIRONNEMENT1 ) );
        MockHttpServletResponse response = new MockHttpServletResponse( );
        MockServletConfig config = new MockServletConfig( );

        // display admin Properties management JSP
        PropertiesJspBean jspbean = new PropertiesJspBean( );
        String html = jspbean.getManageProperties( request );
        assertNotNull( html );

        // display admin Properties creation JSP
        html = jspbean.getCreateProperties( request );
        assertNotNull( html );

        // action create Properties
        request = new MockHttpServletRequest( );

        response = new MockHttpServletResponse( );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );
        request.addParameter( "idEnv", String.valueOf( IDENVIRONNEMENT1 ) );

        request.addParameter( "key", KEY1 );
        request.addParameter( "value", VALUE1 );
        request.addParameter( "idenvironnement", String.valueOf( IDENVIRONNEMENT1 ) );
        request.addParameter( "action", "createProperties" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createProperties" ) );
        request.setMethod( "POST" );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

        // display modify Properties JSP
        request = new MockHttpServletRequest( );
        request.addParameter( "key", KEY1 );
        request.addParameter( "value", VALUE1 );
        request.addParameter( "idenvironnement", String.valueOf( IDENVIRONNEMENT1 ) );
        List<Properties> listProperties = EnvironnementHome.getPropertiesList( IDENVIRONNEMENT1 );
        assertTrue( !listProperties.isEmpty( ) );
        request.addParameter( "idEnv", String.valueOf( listProperties.get( 0 ).getIdenvironnement( ) ) );
        jspbean = new PropertiesJspBean( );

        assertNotNull( jspbean.getModifyProperties( request ) );

        // action modify Properties
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );

        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "value", VALUE2 );
        request.addParameter( "idenvironnement", String.valueOf( IDENVIRONNEMENT2 ) );
        request.setRequestURI( "jsp/admin/plugins/example/ManageProperties.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createProperties, qui est l'action par défaut
        request.addParameter( "action", "modifyProperties" );
        request.addParameter( "idEnv", String.valueOf( IDENVIRONNEMENT1 ) );
        request.addParameter( "key", KEY1 );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyProperties" ) );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

        // get remove Properties
        request = new MockHttpServletRequest( );
        // request.setRequestURI("jsp/admin/plugins/example/ManageProperties.jsp");
        request.addParameter( "idEnv", String.valueOf( listProperties.get( 0 ).getIdenvironnement( ) ) );
        jspbean = new PropertiesJspBean( );
        request.addParameter( "action", "confirmRemoveProperties" );
        assertNotNull( jspbean.getModifyProperties( request ) );

        // do remove Properties
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );
        request.setRequestURI( "jsp/admin/plugins/example/ManagePropertiests.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createProperties, qui est l'action par défaut
        request.addParameter( "action", "removeProperties" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeProperties" ) );
        request.addParameter( "idEnv", String.valueOf( listProperties.get( 0 ).getIdenvironnement( ) ) );
        request.setMethod( "POST" );
        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

    }
}
