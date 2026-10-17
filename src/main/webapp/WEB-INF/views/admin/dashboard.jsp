<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin dashboard" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>
        <h1>Admin dashboard</h1>
        <p class="muted">Signed in as <strong><c:out value="${sessionScope.username}" /></strong>.</p>

        <div class="stat-grid">
            <a class="stat card" href="${ctx}/admin/restaurants">
                <span class="stat-num"><c:out value="${restaurantActive}" /> / <c:out value="${restaurantTotal}" /></span>
                <span class="muted">Active restaurants</span></a>
            <div class="stat card">
                <span class="stat-num"><c:out value="${menuItemCount}" /></span>
                <span class="muted">Menu items</span></div>
            <a class="stat card" href="${ctx}/admin/orders">
                <span class="stat-num"><c:out value="${newOrderCount}" /></span>
                <span class="muted">New orders (of <c:out value="${orderCount}" /> total)</span></a>
            <div class="stat card">
                <span class="stat-num"><c:out value="${userCount}" /></span>
                <span class="muted">Registered users</span></div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
