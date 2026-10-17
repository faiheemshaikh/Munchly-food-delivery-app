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
