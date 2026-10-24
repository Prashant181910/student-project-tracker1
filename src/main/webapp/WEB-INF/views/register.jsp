<%@ include file="/WEB-INF/views/_header.jspf" %>
<div class="card login">
  <h2>Create account</h2>
  <form method="post" action="${ctx}/auth">
    <input type="hidden" name="action" value="register">
    <label>Full name</label>
    <input name="name" required value="<c:out value='${name}'/>">
    <label>Email</label>
    <input type="email" name="email" required value="<c:out value='${email}'/>">
    <label>Password (min 6 characters)</label>
    <input type="password" name="password" required minlength="6">
    <label>Role</label>
    <select name="role">
      <option value="student">Student</option>
      <option value="faculty">Faculty</option>
    </select>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
    <div class="flex" style="margin-top:14px">
      <button type="submit">Register</button>
      <a class="btn ghost" href="${ctx}/auth">Back to sign in</a>
    </div>
  </form>
</div>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
