<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Order confirmed" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container narrow-wide">
        <div class="card confirmation">
            <div class="tick" aria-hidden="true">&#10003;</div>
            <h1>Thank you! Your order is placed.</h1>
            <p class="muted">Order number <strong>#<c:out value="${order.orderId}" /></strong></p>

            <dl class="details">
                <dt>Status</dt><dd><c:out value="${order.status}" /></dd>
                <dt>Placed on</dt><dd><fmt:formatDate value="${order.orderDate}" pattern="dd MMM yyyy, hh:mm a" /></dd>
                <dt>Deliver to</dt><dd><c:out value="${order.deliveryAddress}" /></dd>
            </dl>

            <table class="table">
                <thead><tr><th>Item</th><th class="num">Qty</th><th class="num">Price</th><th class="num">Amount</th></tr></thead>
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

            <a class="btn btn-primary" href="${ctx}/orders">View my orders</a>
            <a class="btn btn-outline" href="${ctx}/home">Order more food</a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
