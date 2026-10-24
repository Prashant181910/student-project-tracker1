<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="nav" value="${heading == 'Pending review' ? 'pending' : (sessionScope.user.student ? 'mine' : 'all')}" scope="request"/>
<%@ include file="/WEB-INF/views/_header.jspf" %>
<h2><c:out value="${heading}"/></h2>
<c:forEach var="p" items="${projects}">
  <t:projectCard p="${p}"/>
</c:forEach>
<c:if test="${empty projects}"><div class="card"><p class="muted">Nothing to show here.</p></div></c:if>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
