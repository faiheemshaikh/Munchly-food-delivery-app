
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Manage orders" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="admin-orders-page">
    <div class="container admin-orders-container">

        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>

        <section class="admin-orders-intro">
            <div>
                <span class="admin-orders-eyebrow">MUNCHLY MANAGEMENT</span>
                <h1>Order management<span class="admin-orders-title-dot">.</span></h1>
                <p>
                    Keep track of customer orders and manage every step
                    of the food delivery journey.
                </p>
            </div>
        </section>

        <section class="admin-orders-overview"
                 aria-label="Order management overview">
            <div class="admin-orders-overview-icon" aria-hidden="true">▤</div>
            <div>
                <span class="admin-orders-overview-label">ORDER DIRECTORY</span>
                <h2>All orders</h2>
                <p>Review orders, update their status, and view order details.</p>
            </div>
        </section>

        <c:choose>
            <c:when test="${empty orders}">
                <section class="admin-orders-empty">
                    <div class="admin-orders-empty-icon" aria-hidden="true">♡</div>
                    <h2>No orders yet</h2>
                    <p>
                        Orders will appear here as soon as customers
                        place their first order.
                    </p>
                </section>
            </c:when>

            <c:otherwise>
                <section class="admin-orders-table-section"
                         aria-label="All customer orders">

                    <div class="admin-orders-table-heading">
                        <div>
                            <h2>Recent orders</h2>
                            <p>Manage order status and access order details.</p>
                        </div>
                    </div>

                    <div class="card table-wrap admin-orders-table-wrap">
                        <table class="table admin-orders-table">
                            <thead>
                                <tr>
                                    <th>Order</th>
                                    <th>Customer</th>
                                    <th>Date</th>
                                    <th class="num">Total</th>
                                    <th>Status</th>
                                    <th class="admin-orders-details-heading">Details</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="o" items="${orders}">
                                    <tr>
                                        <td class="admin-order-id">
                                            <span>#<c:out value="${o.orderId}" /></span>
                                        </td>

                                        <td class="admin-order-customer">
                                            <c:out value="${usernames[o.userId]}" />
                                        </td>

                                        <td class="admin-order-date">
                                            <fmt:formatDate
                                                value="${o.orderDate}"
                                                pattern="dd MMM yyyy, hh:mm a" />
                                        </td>

                                        <td class="num admin-order-total">
                                            ₹<fmt:formatNumber
                                                value="${o.totalAmount}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2" />
                                        </td>

                                        <td>
                                            <form method="post"
                                                  action="${ctx}/admin/orders"
                                                  class="status-form admin-order-status-form">

                                                <input type="hidden"
                                                       name="csrfToken"
                                                       value="<c:out value='${csrfToken}' />">

                                                <input type="hidden"
                                                       name="orderId"
                                                       value="<c:out value='${o.orderId}' />">

                                                <select name="status"
                                                        class="admin-order-status-select"
                                                        aria-label="Status of order ${o.orderId}">
                                                    <c:forEach var="s" items="${statuses}">
                                                        <option value="<c:out value='${s}' />"
                                                            <c:if test="${s == o.status}">selected</c:if>>
                                                            <c:out value="${s}" />
                                                        </option>
                                                    </c:forEach>
                                                </select>

                                                <button type="submit"
                                                        class="btn btn-outline btn-small admin-order-update">
                                                    Update
                                                </button>
                                            </form>
                                        </td>

                                        <td class="admin-order-details-cell">
                                            <a class="admin-order-details-link"
                                               href="${ctx}/admin/orders/details?orderId=${o.orderId}">
                                                View details
                                                <span aria-hidden="true">→</span>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                </section>
            </c:otherwise>
        </c:choose>

        <div class="admin-orders-footer-note">
            <span aria-hidden="true">✦</span>
            <p>Every order is another opportunity to deliver a great experience.</p>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>





<%-- 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage orders" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>
        <h1>All orders</h1>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state"><h2>No orders yet</h2><p>Orders appear here as soon as customers place them.</p></div>
            </c:when>
            <c:otherwise>
                <div class="card table-wrap">
                    <table class="table">
                        <thead>
                        <tr><th>Order</th><th>Customer</th><th>Date</th><th class="num">Total</th><th>Status</th><th></th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="o" items="${orders}">
                            <tr>
                                <td>#<c:out value="${o.orderId}" /></td>
                                <td><c:out value="${usernames[o.userId]}" /></td>
                                <td><fmt:formatDate value="${o.orderDate}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                <td class="num">&#8377;<fmt:formatNumber value="${o.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                <td>
                                    <form method="post" action="${ctx}/admin/orders" class="status-form">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                        <input type="hidden" name="orderId" value="${o.orderId}">
                                        <select name="status" aria-label="Status of order ${o.orderId}">
                                            <c:forEach var="s" items="${statuses}">
                                                <option value="${s}" <c:if test="${s == o.status}">selected</c:if>><c:out value="${s}" /></option>
                                            </c:forEach>
                                        </select>
                                        <button type="submit" class="btn btn-outline btn-small">Update</button>
                                    </form>
                                </td>
                                <td class="num"><a href="${ctx}/admin/orders/details?orderId=${o.orderId}">Details</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
--%>