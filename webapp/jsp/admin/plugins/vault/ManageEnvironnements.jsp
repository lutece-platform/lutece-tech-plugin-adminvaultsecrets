<jsp:useBean id="manageapplicationEnvironnement" scope="session" class="fr.paris.lutece.plugins.vault.web.EnvironnementJspBean" />
<% String strContent = manageapplicationEnvironnement.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
