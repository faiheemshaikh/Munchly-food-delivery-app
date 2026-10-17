<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Log in" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="container narrow">
    <div class="card">
        <h1>Welcome back</h1>
        <p class="muted">Log in to order your favourite food.</p>

        <c:if test="${param.registered == '1'}">
            <div class="alert alert-success">Account created. Please log in.</div>
        </c:if>
        <c:if test="${param.loggedout == '1'}">
            <div class="alert alert-success">You have been logged out.</div>
        </c:if>
        <c:if test="${not empty loginError}">
            <div class="alert alert-error"><c:out value="${loginError}" /></div>
        </c:if>

        <form method="post" action="${ctx}/login" autocomplete="on">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
            <input type="hidden" name="next" value="<c:out value='${next}' />">

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required maxlength="100"
                       value="<c:out value='${form.email}' />">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Log in</button>
        </form>

        <p class="muted center">New here? <a href="${ctx}/register">Create an account</a></p>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
