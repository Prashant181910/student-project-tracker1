<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="nav" value="new" scope="request"/>
<%@ include file="/WEB-INF/views/_header.jspf" %>
<div class="card">
  <h2>Submit a project proposal</h2>
  <form method="post" action="${ctx}/project">
    <input type="hidden" name="action" value="create">
    <div class="row">
      <div><label>Group</label>
        <input name="group" maxlength="20" value="<c:out value='${empty param.group ? "G3" : param.group}'/>"></div>
      <div><label>Team members</label>
        <input name="team" maxlength="300" value="<c:out value='${empty param.team ? user.name : param.team}'/>"></div>
    </div>
    <label>Project title</label>
    <input name="title" maxlength="200" required value="<c:out value='${param.title}'/>">
    <label>Description / objectives</label>
    <textarea name="description" maxlength="2000" required><c:out value="${param.description}"/></textarea>
    <label>Technologies</label>
    <input name="tech" maxlength="200" placeholder="e.g. Servlets, JSP, JDBC, MySQL" value="<c:out value='${param.tech}'/>">
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
    <div style="margin-top:14px"><button type="submit">Submit proposal</button></div>
  </form>
</div>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
