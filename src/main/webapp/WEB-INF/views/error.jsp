<%@ page isErrorPage="true" %>
<%@ include file="/WEB-INF/views/_header.jspf" %>
<div class="card">
  <h2>Something went wrong</h2>
  <p>
    <c:choose>
      <c:when test="${pageContext.errorData.statusCode == 403}">You are not allowed to do that.</c:when>
      <c:when test="${pageContext.errorData.statusCode == 404}">That page or project could not be found.</c:when>
      <c:otherwise>An unexpected error occurred. Please try again.</c:otherwise>
    </c:choose>
  </p>
  <a class="btn" href="${ctx}/dashboard">Go to dashboard</a>
</div>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
