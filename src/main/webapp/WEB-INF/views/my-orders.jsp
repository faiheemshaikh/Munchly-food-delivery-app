<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My orders" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <h1>My orders</h1>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state">
                    <h2>No orders yet</h2>
                    <p>When you place an order, it will show up here.</p>
                    <a class="btn btn-primary" href="${ctx}/home">Browse restaurants</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card table-wrap">
                    <table class="table">
                        <thead>
                        <tr><th>Order</th><th>Date</th><th class="num">Total</th><th>Status</th><th></th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="o" items="${orders}">
                            <tr>
                                <td>#<c:out value="${o.orderId}" /></td>
                                <td><fmt:formatDate value="${o.orderDate}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                <td class="num">&#8377;<fmt:formatNumber value="${o.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                <td><span class="status status-${o.status}"><c:out value="${o.status}" /></span></td>
                                <td class="num"><a href="${ctx}/orders/details?orderId=${o.orderId}">View details</a></td>
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
