<jsp:useBean id="manageapplicationProperties" scope="session" class="fr.paris.lutece.plugins.vault.web.PropertiesJspBean" />
<% String strContent = manageapplicationProperties.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
