<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My profile" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<%-- After a failed validation the typed values (form) win; otherwise show the saved account values. --%>
<c:set var="valUsername" value="${empty form ? account.username : form.username}" />
<c:set var="valPhone" value="${empty form ? account.phone : form.phone}" />
<c:set var="valAddress" value="${empty form ? account.address : form.address}" />

<main class="container narrow">
    <div class="card">
        <h1>My profile</h1>

        <c:if test="${param.updated == '1'}">
            <div class="alert alert-success">Your details were saved.</div>
        </c:if>
        <c:if test="${not empty formError}">
            <div class="alert alert-error"><c:out value="${formError}" /></div>
        </c:if>

        <form method="post" action="${ctx}/profile" novalidate>
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">

            <div class="form-group">
                <label>Email</label>
                <input type="email" value="<c:out value='${account.email}' />" disabled>
                <span class="hint">Your email cannot be changed.</span>
            </div>

            <div class="form-group">
                <label for="username">Name</label>
                <input type="text" id="username" name="username" required maxlength="50"
                       value="<c:out value='${valUsername}' />">
                <c:if test="${not empty errors.username}">
                    <span class="field-error"><c:out value="${errors.username}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="tel" id="phone" name="phone" maxlength="20" value="<c:out value='${valPhone}' />">
                <c:if test="${not empty errors.phone}">
                    <span class="field-error"><c:out value="${errors.phone}" /></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="address">Default delivery address</label>
                <textarea id="address" name="address" rows="3" maxlength="255"><c:out value="${valAddress}" /></textarea>
                <c:if test="${not empty errors.address}">
                    <span class="field-error"><c:out value="${errors.address}" /></span>
                </c:if>
            </div>

            <button type="submit" class="btn btn-primary">Save changes</button>
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
