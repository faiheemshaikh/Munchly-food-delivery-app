<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Checkout" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <h1>Checkout</h1>

        <c:forEach var="notice" items="${cartNotices}">
            <div class="alert alert-error"><c:out value="${notice}" /></div>
        </c:forEach>
        <c:if test="${not empty formError}">
            <div class="alert alert-error"><c:out value="${formError}" /></div>
        </c:if>

        <div class="cart-layout">
            <section class="card">
                <h2>Delivery details</h2>
                <form method="post" action="${ctx}/checkout" novalidate>
                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                    <%-- The total the customer is looking at. The server compares it with the real total and
                         never uses it as the amount to charge. --%>
                    <input type="hidden" name="displayedTotal" value="<c:out value='${checkoutTotal}' />">

                    <div class="form-group">
                        <label for="deliveryAddress">Delivery address</label>
                        <textarea id="deliveryAddress" name="deliveryAddress" rows="4" maxlength="255" required
                                  placeholder="House / flat no., street, area, city, PIN code"><c:out value="${deliveryAddress}" /></textarea>
                        <c:if test="${not empty addressError}">
                            <span class="field-error"><c:out value="${addressError}" /></span>
                        </c:if>
                        <span class="hint">Pre-filled from your profile. You can change it for this order.</span>
                    </div>

                    <p class="muted">Payment: pay on delivery (online payments are not part of this version).</p>

                    <button type="submit" class="btn btn-primary" id="place-order-btn">Place order</button>
                    <a class="btn btn-outline" href="${ctx}/cart">Back to cart</a>
                </form>
            </section>

            <aside class="card summary">
                <h2>Your order</h2>
                <p class="muted"><c:out value="${cartRestaurantName}" /></p>
                <c:forEach var="line" items="${checkoutLines}">
                    <div class="summary-row">
                        <span><c:out value="${line.quantity}" /> &times; <c:out value="${line.name}" /></span>
                        <span>&#8377;<fmt:formatNumber value="${line.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></span>
                    </div>
                </c:forEach>
                <div class="summary-row"><span>Delivery</span><span>Free</span></div>
                <div class="summary-row total"><span>Total</span>
                    <span>&#8377;<fmt:formatNumber value="${checkoutTotal}" minFractionDigits="2" maxFractionDigits="2" /></span></div>
            </aside>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
