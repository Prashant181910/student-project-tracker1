<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="nav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/_header.jspf" %>
<div class="stats">
  <div class="stat"><b>${total}</b><span>Total projects</span></div>
  <div class="stat"><b>${pending}</b><span>Pending review</span></div>
  <div class="stat"><b>${approved}</b><span>Approved</span></div>
  <div class="stat"><b>${revision}</b><span>Needs revision</span></div>
  <div class="stat"><b>${avg}%</b><span>Average progress</span></div>
</div>
<div class="card">
  <h2>Recent projects</h2>
  <c:forEach var="p" items="${recent}">
    <t:projectCard p="${p}"/>
  </c:forEach>
  <c:if test="${empty recent}"><p class="muted">No projects yet.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
