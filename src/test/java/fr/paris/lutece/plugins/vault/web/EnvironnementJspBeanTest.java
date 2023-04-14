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
import fr.paris.lutece.plugins.vault.business.Environnement;
import fr.paris.lutece.plugins.vault.business.EnvironnementHome;

/**
 * This is the business class test for the object Environnement
 */
public class EnvironnementJspBeanTest extends LuteceTestCase
{
    private static final String CODE1 = "Code1";
    private static final String CODE2 = "Code2";
    private static final String TOKEN1 = "Token1";
    private static final String TOKEN2 = "Token2";
    private static final int IDAPPLICATION1 = 1;
    private static final int IDAPPLICATION2 = 2;

    public void testJspBeans( ) throws AccessDeniedException, IOException, VaultException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        MockHttpServletResponse response = new MockHttpServletResponse( );
        MockServletConfig config = new MockServletConfig( );

        // display admin Environnement management JSP
        EnvironnementJspBean jspbean = new EnvironnementJspBean( );
        String html = jspbean.getManageEnvironnements( request );
        assertNotNull( html );

        // display admin Environnement creation JSP
        html = jspbean.getCreateEnvironnement( request );
        assertNotNull( html );

        // action create Environnement
        request = new MockHttpServletRequest( );

        response = new MockHttpServletResponse( );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "code", CODE1 );
        request.addParameter( "token", TOKEN1 );
        request.addParameter( "idapplication", String.valueOf( IDAPPLICATION1 ) );
        request.addParameter( "action", "createEnvironnement" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createEnvironnement" ) );
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

        // display modify Environnement JSP
        request = new MockHttpServletRequest( );
        request.addParameter( "code", CODE1 );
        request.addParameter( "token", TOKEN1 );
        request.addParameter( "idapplication", String.valueOf( IDAPPLICATION1 ) );
        List<Integer> listIds = EnvironnementHome.getIdEnvironnementsList( );
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        jspbean = new EnvironnementJspBean( );

        assertNotNull( jspbean.getModifyEnvironnement( request ) );

        // action modify Environnement
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );

        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "code", CODE2 );
        request.addParameter( "token", TOKEN2 );
        request.addParameter( "idapplication", String.valueOf( IDAPPLICATION2 ) );
        request.setRequestURI( "jsp/admin/plugins/example/ManageEnvironnements.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createEnvironnement, qui est l'action par défaut
        request.addParameter( "action", "modifyEnvironnement" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyEnvironnement" ) );

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

        // get remove Environnement
        request = new MockHttpServletRequest( );
        // request.setRequestURI("jsp/admin/plugins/example/ManageEnvironnements.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        jspbean = new EnvironnementJspBean( );
        request.addParameter( "action", "confirmRemoveEnvironnement" );
        assertNotNull( jspbean.getModifyEnvironnement( request ) );

        // do remove Environnement
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );
        request.setRequestURI( "jsp/admin/plugins/example/ManageEnvironnementts.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createEnvironnement, qui est l'action par défaut
        request.addParameter( "action", "removeEnvironnement" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeEnvironnement" ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
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
