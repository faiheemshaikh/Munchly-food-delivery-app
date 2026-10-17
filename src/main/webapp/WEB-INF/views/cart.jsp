<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Your cart" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <h1>Your cart</h1>

        <c:forEach var="notice" items="${cartNotices}">
            <div class="alert alert-error"><c:out value="${notice}" /></div>
        </c:forEach>

        <c:choose>
            <c:when test="${empty cartLines}">
                <div class="empty-state">
                    <h2>Your cart is empty</h2>
                    <p>Pick a restaurant and add something delicious.</p>
                    <a class="btn btn-primary" href="${ctx}/home">Browse restaurants</a>
                </div>
            </c:when>

            <c:otherwise>
                <p class="muted">Ordering from
                    <a href="${ctx}/menu?restaurantId=${cartRestaurantId}"><c:out value="${cartRestaurantName}" /></a></p>

                <div class="cart-layout">
                    <section class="card cart-items">
                        <c:forEach var="line" items="${cartLines}">
                            <c:set var="lineImg" value="${empty line.imagePath ? 'images/placeholder.svg' : line.imagePath}" />
                            <div class="cart-row">
                                <img class="menu-thumb" src="${ctx}/<c:out value='${lineImg}' />"
                                     alt="<c:out value='${line.name}' />" data-fallback>
                                <div class="cart-name">
                                    <strong><c:out value="${line.name}" /></strong>
                                    <span class="muted">&#8377;<fmt:formatNumber value="${line.unitPrice}" minFractionDigits="2" maxFractionDigits="2" /> each</span>
                                </div>

                                <div class="qty-controls">
                                    <form method="post" action="${ctx}/cart" class="inline-form">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                        <input type="hidden" name="action" value="decrease">
                                        <input type="hidden" name="menuId" value="${line.menuId}">
                                        <button type="submit" class="qty-btn" aria-label="Decrease quantity of <c:out value='${line.name}' />">&minus;</button>
                                    </form>
                                    <span class="qty"><c:out value="${line.quantity}" /></span>
                                    <form method="post" action="${ctx}/cart" class="inline-form">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                        <input type="hidden" name="action" value="increase">
                                        <input type="hidden" name="menuId" value="${line.menuId}">
                                        <button type="submit" class="qty-btn" aria-label="Increase quantity of <c:out value='${line.name}' />"
                                                <c:if test="${line.quantity >= maxQty}">disabled</c:if>>+</button>
                                    </form>
                                </div>

                                <div class="line-total">&#8377;<fmt:formatNumber value="${line.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></div>

                                <form method="post" action="${ctx}/cart" class="inline-form">
                                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="menuId" value="${line.menuId}">
                                    <button type="submit" class="link-btn" aria-label="Remove <c:out value='${line.name}' />">Remove</button>
                                </form>
                            </div>
                        </c:forEach>

                        <form method="post" action="${ctx}/cart" class="clear-form">
                            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                            <input type="hidden" name="action" value="clear">
                            <button type="submit" class="link-btn danger">Clear cart</button>
                        </form>
                    </section>

                    <aside class="card summary">
                        <h2>Order summary</h2>
                        <div class="summary-row"><span>Subtotal</span>
                            <span>&#8377;<fmt:formatNumber value="${cartTotal}" minFractionDigits="2" maxFractionDigits="2" /></span></div>
                        <div class="summary-row"><span>Delivery</span><span>Free</span></div>
                        <div class="summary-row total"><span>Total</span>
                            <span>&#8377;<fmt:formatNumber value="${cartTotal}" minFractionDigits="2" maxFractionDigits="2" /></span></div>
                        <a class="btn btn-primary btn-block" href="${ctx}/checkout">Proceed to checkout</a>
                        <a class="btn btn-outline btn-block" href="${ctx}/menu?restaurantId=${cartRestaurantId}">Add more items</a>
                    </aside>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
