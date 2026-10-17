<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Order details" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<%-- Used by customers (/orders/details) and by administrators (/admin/orders/details). --%>
<main>
    <div class="container narrow-wide">
        <a class="back-link" href="${ctx}${backPath}">&larr; <c:out value="${backLabel}" /></a>

        <div class="card">
            <h1>Order #<c:out value="${order.orderId}" />
                <span class="status status-${order.status}"><c:out value="${order.status}" /></span></h1>

            <dl class="details">
                <c:if test="${not empty customerName}">
                    <dt>Customer</dt><dd><c:out value="${customerName}" /></dd>
                </c:if>
                <dt>Placed on</dt><dd><fmt:formatDate value="${order.orderDate}" pattern="dd MMM yyyy, hh:mm a" /></dd>
                <dt>Deliver to</dt><dd><c:out value="${order.deliveryAddress}" /></dd>
            </dl>

            <table class="table">
                <thead><tr><th>Item</th><th class="num">Qty</th><th class="num">Unit price</th><th class="num">Amount</th></tr></thead>
                <tbody>
                <c:forEach var="line" items="${orderLines}">
                    <tr>
                        <td><c:out value="${line.itemName}" /></td>
                        <td class="num"><c:out value="${line.quantity}" /></td>
                        <td class="num">&#8377;<fmt:formatNumber value="${line.unitPrice}" minFractionDigits="2" maxFractionDigits="2" /></td>
                        <td class="num">&#8377;<fmt:formatNumber value="${line.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></td>
                    </tr>
                </c:forEach>
                </tbody>
                <tfoot>
                    <tr><td colspan="3"><strong>Total</strong></td>
                        <td class="num"><strong>&#8377;<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></strong></td></tr>
                </tfoot>
            </table>
            <p class="muted">Unit prices are the prices at the time the order was placed.</p>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
