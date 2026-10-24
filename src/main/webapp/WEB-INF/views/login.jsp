<%@ include file="/WEB-INF/views/_header.jspf" %>
<div class="card login">
  <h2>Sign in</h2>
  <form method="post" action="${ctx}/auth">
    <input type="hidden" name="action" value="login">
    <label>Email</label>
    <input type="email" name="email" required value="<c:out value='${email}'/>">
    <label>Password</label>
    <input type="password" name="password" required>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
    <div class="flex" style="margin-top:14px">
      <button type="submit">Sign in</button>
      <a class="btn ghost" href="${ctx}/auth?action=register">Create account</a>
    </div>
  </form>
  <p class="muted">Demo logins:<br>
    Student: prashant@college.edu / student123<br>
    Faculty: faculty@college.edu / faculty123</p>
</div>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
