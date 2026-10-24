<%@ include file="/WEB-INF/views/_header.jspf" %>
<a class="btn ghost" href="${ctx}/project">&larr; Back</a>

<div class="card" style="margin-top:12px">
  <div class="flex">
    <h2 style="margin:0"><c:out value="${p.title}"/></h2>
    <span class="badge ${p.status}"><c:out value="${p.statusLabel}"/></span>
  </div>
  <p class="team">Group <c:out value="${empty p.groupName ? '-' : p.groupName}"/> &middot;
    <c:out value="${p.team}"/> &middot; Submitted ${p.submitted}</p>
  <p><c:out value="${p.description}"/></p>
  <p class="muted"><b>Technologies:</b> <c:out value="${empty p.tech ? '-' : p.tech}"/></p>
  <div class="bar"><i style="width:${p.progress}%"></i></div>
  <div class="muted">${p.progress}% complete</div>

  <c:if test="${p.ownerId == user.id}">
    <div class="flex" style="margin-top:12px">
      <c:if test="${p.status == 'Revision'}">
        <form method="post" action="${ctx}/project">
          <input type="hidden" name="action" value="resubmit"><input type="hidden" name="id" value="${p.id}">
          <button type="submit">Resubmit for review</button>
        </form>
      </c:if>
      <form method="post" action="${ctx}/project" onsubmit="return confirm('Delete this project?')">
        <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${p.id}">
        <button type="submit" class="ghost">Delete project</button>
      </form>
    </div>
  </c:if>
</div>

<c:if test="${user.faculty}">
  <div class="card">
    <h2>Faculty review</h2>
    <form method="post" action="${ctx}/review">
      <input type="hidden" name="id" value="${p.id}">
      <label>Comment</label>
      <textarea name="comment" maxlength="1000" placeholder="Feedback for the students"></textarea>
      <div class="flex" style="margin-top:10px">
        <button class="ok" name="status" value="Approved">Approve</button>
        <button class="warn" name="status" value="Revision">Request revision</button>
        <button class="bad" name="status" value="Rejected">Reject</button>
        <c:if test="${p.status == 'Approved'}"><button name="status" value="Completed">Mark completed</button></c:if>
      </div>
    </form>
  </div>
</c:if>

<div class="card">
  <h2>Milestones</h2>
  <c:forEach var="m" items="${p.milestones}">
    <div class="ms ${m.done ? 'done' : ''}">
      <form method="post" action="${ctx}/project" class="inline">
        <input type="hidden" name="action" value="toggleMilestone">
        <input type="hidden" name="id" value="${p.id}"><input type="hidden" name="mid" value="${m.id}">
        <input type="checkbox" ${m.done ? 'checked' : ''} ${canEdit ? '' : 'disabled'} onchange="this.form.submit()">
      </form>
      <span><c:out value="${m.title}"/></span>
      <small class="muted"><c:out value="${m.dueDate}"/></small>
      <c:if test="${canEdit}">
        <form method="post" action="${ctx}/project" class="inline">
          <input type="hidden" name="action" value="deleteMilestone">
          <input type="hidden" name="id" value="${p.id}"><input type="hidden" name="mid" value="${m.id}">
          <button class="ghost small" type="submit">x</button>
        </form>
      </c:if>
    </div>
  </c:forEach>
  <c:if test="${empty p.milestones}"><p class="muted">No milestones yet.</p></c:if>

  <c:if test="${canEdit}">
    <form method="post" action="${ctx}/project">
      <input type="hidden" name="action" value="addMilestone"><input type="hidden" name="id" value="${p.id}">
      <div class="row" style="margin-top:12px">
        <div><label>New milestone</label><input name="title" maxlength="200" required></div>
        <div><label>Due date</label><input name="due" type="date"></div>
      </div>
      <div style="margin-top:10px"><button type="submit">Add milestone</button></div>
    </form>
  </c:if>
</div>

<div class="card">
  <h2>Progress updates</h2>
  <c:forEach var="x" items="${p.updates}">
    <div class="update"><small class="muted">${x.date}</small><br><c:out value="${x.body}"/></div>
  </c:forEach>
  <c:if test="${empty p.updates}"><p class="muted">No updates yet.</p></c:if>
  <c:if test="${canEdit}">
    <form method="post" action="${ctx}/project">
      <input type="hidden" name="action" value="addUpdate"><input type="hidden" name="id" value="${p.id}">
      <label>Add update</label>
      <textarea name="body" maxlength="1000" required></textarea>
      <div style="margin-top:10px"><button type="submit">Post update</button></div>
    </form>
  </c:if>
</div>

<div class="card">
  <h2>Faculty feedback</h2>
  <c:forEach var="x" items="${p.feedback}">
    <div class="fb"><small class="muted">${x.date} &middot; <c:out value="${x.author}"/></small><br><c:out value="${x.body}"/></div>
  </c:forEach>
  <c:if test="${empty p.feedback}"><p class="muted">No feedback yet.</p></c:if>
</div>
<%@ include file="/WEB-INF/views/_footer.jspf" %>
