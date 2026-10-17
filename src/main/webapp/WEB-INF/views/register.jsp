<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign up" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="container narrow">
    <div class="card">
        <h1>Create your account</h1>
        <p class="muted">It takes less than a minute.</p>

        <c:if test="${not empty formError}">
            <div class="alert alert-error"><c:out value="${formError}" /></div>
        </c:if>

        <form method="post" action="${ctx}/register" novalidate>
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">

            <div class="form-group">
                <label for="username">Name</label>
                <input type="text" id="username" name="username" required maxlength="50"
                       value="<c:out value='${form.username}' />">
                <c:if test="${not empty errors.username}">
                    <span class="field-error"><c:out value="${errors.username}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required maxlength="100"
                       value="<c:out value='${form.email}' />">
                <c:if test="${not empty errors.email}">
                    <span class="field-error"><c:out value="${errors.email}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="phone">Phone <span class="muted">(optional)</span></label>
                <input type="tel" id="phone" name="phone" maxlength="20"
                       value="<c:out value='${form.phone}' />">
                <c:if test="${not empty errors.phone}">
                    <span class="field-error"><c:out value="${errors.phone}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="address">Default delivery address <span class="muted">(optional)</span></label>
                <textarea id="address" name="address" rows="2" maxlength="255"><c:out value="${form.address}" /></textarea>
                <c:if test="${not empty errors.address}">
                    <span class="field-error"><c:out value="${errors.address}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="new-password">
                <span class="hint">At least 8 characters, with a letter and a number.</span>
                <c:if test="${not empty errors.password}">
                    <span class="field-error"><c:out value="${errors.password}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required autocomplete="new-password">
                <c:if test="${not empty errors.confirmPassword}">
                    <span class="field-error"><c:out value="${errors.confirmPassword}" /></span>
                </c:if>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Sign up</button>
        </form>

        <p class="muted center">Already registered? <a href="${ctx}/login">Log in</a></p>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
