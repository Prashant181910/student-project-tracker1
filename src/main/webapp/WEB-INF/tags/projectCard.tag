<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="p" type="com.g3.sppts.model.Project" required="true" %>
<div class="card tight">
  <div class="flex">
    <h3><c:out value="${p.title}"/></h3>
    <span class="badge ${p.status}"><c:out value="${p.statusLabel}"/></span>
    <a class="btn ghost right" href="${pageContext.request.contextPath}/project?action=view&amp;id=${p.id}">Open</a>
  </div>
  <div class="team">Group <c:out value="${empty p.groupName ? '-' : p.groupName}"/> &middot;
    <c:out value="${p.team}"/> &middot; by <c:out value="${p.ownerName}"/></div>
  <p class="muted"><c:out value="${fn:substring(p.description, 0, 160)}"/><c:if test="${fn:length(p.description) > 160}">...</c:if></p>
  <div class="bar"><i style="width:${p.progress}%"></i></div>
  <div class="muted">${p.progress}% complete</div>
</div>
