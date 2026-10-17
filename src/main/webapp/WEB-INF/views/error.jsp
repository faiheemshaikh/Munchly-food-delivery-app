<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<c:set var="statusCode" value="${requestScope['jakarta.servlet.error.status_code']}" />

<main class="container narrow">
    <div class="card center">
        <c:choose>
            <c:when test="${statusCode == 403}">
                <h1>Not allowed</h1>
                <p>You don&rsquo;t have permission to do that, or your session expired.
                   Please go back, reload the page and try again.</p>
            </c:when>
            <c:when test="${statusCode == 404}">
                <h1>Page not found</h1>
                <p>We couldn&rsquo;t find the page you were looking for.</p>
            </c:when>
            <c:otherwise>
                <h1>Something went wrong</h1>
                <p>Sorry, something went wrong on our side. Please try again in a moment.</p>
            </c:otherwise>
        </c:choose>
        <a class="btn btn-primary" href="${ctx}/home">Back to home</a>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
