
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Admin dashboard" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="admin-dashboard">
    <div class="container admin-dashboard-container">

        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>

        <section class="dashboard-welcome">
            <div class="dashboard-welcome-text">
                <span class="dashboard-eyebrow">MUNCHLY MANAGEMENT</span>
                <h1>Dashboard<span class="dashboard-title-dot">.</span></h1>
                <p class="dashboard-subtitle">
                    Here's what's happening with your food delivery platform.
                </p>
            </div>

            <div class="dashboard-greeting">
                <span class="greeting-icon" aria-hidden="true">✦</span>
                <div>
                    <span class="greeting-label">SIGNED IN AS</span>
                    <strong>
                        <c:out value="${sessionScope.username}" />
                    </strong>
                </div>
            </div>
        </section>

        <section class="dashboard-overview" aria-labelledby="overview-title">
            <div class="dashboard-section-heading">
                <div>
                    <h2 id="overview-title">Platform overview</h2>
                    <p>Your key numbers at a glance.</p>
                </div>
                <span class="overview-indicator">
                    <span class="overview-indicator-dot"></span>
                    Live overview
                </span>
            </div>

            <div class="stat-grid">

                <a class="stat card dashboard-stat dashboard-stat-restaurants"
                   href="${ctx}/admin/restaurants">
                    <div class="dashboard-stat-top">
                        <span class="dashboard-stat-icon" aria-hidden="true">⌂</span>
                        <span class="dashboard-stat-caption">RESTAURANTS</span>
                    </div>
                    <span class="stat-num dashboard-stat-number">
                        <c:out value="${restaurantActive}" />
                        <span class="dashboard-stat-divider">/</span>
                        <span class="dashboard-stat-total">
                            <c:out value="${restaurantTotal}" />
                        </span>
                    </span>
                    <span class="dashboard-stat-label">Active restaurants</span>
                    <span class="dashboard-stat-footer">
                        Manage restaurants <span aria-hidden="true">→</span>
                    </span>
                </a>

                <div class="stat card dashboard-stat dashboard-stat-menu">
                    <div class="dashboard-stat-top">
                        <span class="dashboard-stat-icon" aria-hidden="true">✳</span>
                        <span class="dashboard-stat-caption">YOUR MENU</span>
                    </div>
                    <span class="stat-num dashboard-stat-number">
                        <c:out value="${menuItemCount}" />
                    </span>
                    <span class="dashboard-stat-label">Menu items</span>
                    <span class="dashboard-stat-footer dashboard-stat-footer-static">
                        Items across your platform
                    </span>
                </div>

                <a class="stat card dashboard-stat dashboard-stat-orders"
                   href="${ctx}/admin/orders">
                    <div class="dashboard-stat-top">
                        <span class="dashboard-stat-icon" aria-hidden="true">▤</span>
                        <span class="dashboard-stat-caption">ORDERS</span>
                    </div>
                    <span class="stat-num dashboard-stat-number">
                        <c:out value="${newOrderCount}" />
                    </span>
                    <span class="dashboard-stat-label">New orders</span>
                    <span class="dashboard-stat-footer">
                        <c:out value="${orderCount}" /> orders in total
                        <span aria-hidden="true">→</span>
                    </span>
                </a>

                <div class="stat card dashboard-stat dashboard-stat-users">
                    <div class="dashboard-stat-top">
                        <span class="dashboard-stat-icon" aria-hidden="true">♧</span>
                        <span class="dashboard-stat-caption">COMMUNITY</span>
                    </div>
                    <span class="stat-num dashboard-stat-number">
                        <c:out value="${userCount}" />
                    </span>
                    <span class="dashboard-stat-label">Registered users</span>
                    <span class="dashboard-stat-footer dashboard-stat-footer-static">
                        Your Munchly community
                    </span>
                </div>

            </div>
        </section>

        <section class="dashboard-bottom-note">
            <div class="dashboard-note-icon" aria-hidden="true">♡</div>
            <div>
                <h2>Good food starts with good management.</h2>
                <p>Keep your restaurants and orders running smoothly with Munchly.</p>
            </div>
        </section>

    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>



<%-- 

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
--%>