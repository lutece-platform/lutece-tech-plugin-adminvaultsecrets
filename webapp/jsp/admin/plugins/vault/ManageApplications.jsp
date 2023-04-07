<jsp:useBean id="manageapplicationApplication" scope="session" class="fr.paris.lutece.plugins.vault.web.ApplicationJspBean" />
<% String strContent = manageapplicationApplication.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
