
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Your cart" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="cart-page">
    <div class="container">

        <div class="cart-heading">
            <div>
                <a class="back-link" href="${ctx}/home">&larr; Continue exploring</a>
                <p class="cart-eyebrow">YOUR MUNCHLY ORDER</p>
                <h1>Your cart<span class="cart-heading-dot">.</span></h1>
                <p class="cart-subtitle">Good food is just a few steps away.</p>
            </div>
            <div class="cart-heading-icon" aria-hidden="true">&#128722;</div>
        </div>

        <c:forEach var="notice" items="${cartNotices}">
            <div class="alert alert-error">
                <c:out value="${notice}" />
            </div>
        </c:forEach>

        <c:choose>
            <c:when test="${empty cartLines}">
                <section class="cart-empty">
                    <div class="cart-empty-icon" aria-hidden="true">&#127828;</div>
                    <p class="cart-eyebrow">ROOM FOR SOMETHING DELICIOUS</p>
                    <h2>Your cart is taking a little break.</h2>
                    <p>Discover your next favourite meal and bring something delicious to the table.</p>
                    <a class="btn btn-primary" href="${ctx}/home">Explore restaurants</a>
                </section>
            </c:when>

            <c:otherwise>
                <div class="cart-restaurant-note">
                    <span class="cart-restaurant-icon" aria-hidden="true">&#127860;</span>
                    <span>
                        <span class="cart-note-label">You're ordering from</span>
                        <a href="${ctx}/menu?restaurantId=${cartRestaurantId}">
                            <strong><c:out value="${cartRestaurantName}" /></strong>
                        </a>
                    </span>
                    <span class="cart-note-check" aria-hidden="true">&#10003;</span>
                </div>

                <div class="cart-layout cart-layout-premium">

                    <section class="cart-items-panel" aria-label="Items in your cart">
                        <div class="cart-panel-heading">
                            <div>
                                <h2>Your selections</h2>
                                <p>Made for your appetite.</p>
                            </div>
                            <span class="cart-secure-label">&#10003; Freshly selected</span>
                        </div>

                        <div class="cart-items">
                            <c:forEach var="line" items="${cartLines}">
                                <c:set var="lineImg"
                                    value="${empty line.imagePath ? 'images/placeholder.svg' : line.imagePath}" />

                                <article class="cart-row cart-row-premium">
                                    <div class="cart-product-image">
                                        <img
                                            src="${ctx}/<c:out value='${lineImg}' />"
                                            alt="<c:out value='${line.name}' />"
                                            data-fallback>
                                    </div>

                                    <div class="cart-product-details">
                                        <h3><c:out value="${line.name}" /></h3>
                                        <p class="cart-unit-price">
                                            &#8377;<fmt:formatNumber value="${line.unitPrice}"
                                                minFractionDigits="2" maxFractionDigits="2" /> each
                                        </p>

                                        <div class="cart-product-bottom">
                                            <div class="qty-controls qty-controls-premium">
                                                <form method="post" action="${ctx}/cart" class="inline-form">
                                                    <input type="hidden" name="csrfToken"
                                                        value="<c:out value='${csrfToken}' />">
                                                    <input type="hidden" name="action" value="decrease">
                                                    <input type="hidden" name="menuId" value="${line.menuId}">
                                                    <button type="submit" class="qty-btn"
                                                        aria-label="Decrease quantity of <c:out value='${line.name}' />">−</button>
                                                </form>

                                                <span class="qty qty-premium">
                                                    <c:out value="${line.quantity}" />
                                                </span>

                                                <form method="post" action="${ctx}/cart" class="inline-form">
                                                    <input type="hidden" name="csrfToken"
                                                        value="<c:out value='${csrfToken}' />">
                                                    <input type="hidden" name="action" value="increase">
                                                    <input type="hidden" name="menuId" value="${line.menuId}">
                                                    <button type="submit" class="qty-btn"
                                                        aria-label="Increase quantity of <c:out value='${line.name}' />"
                                                        <c:if test="${line.quantity >= maxQty}">disabled</c:if>>+</button>
                                                </form>
                                            </div>

                                            <form method="post" action="${ctx}/cart" class="inline-form cart-remove-form">
                                                <input type="hidden" name="csrfToken"
                                                    value="<c:out value='${csrfToken}' />">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="menuId" value="${line.menuId}">
                                                <button type="submit" class="link-btn cart-remove-btn"
                                                    aria-label="Remove <c:out value='${line.name}' />">Remove</button>
                                            </form>
                                        </div>
                                    </div>

                                    <div class="cart-line-price">
                                        <span class="cart-line-price-label">Item total</span>
                                        <strong>&#8377;<fmt:formatNumber value="${line.subtotal}"
                                            minFractionDigits="2" maxFractionDigits="2" /></strong>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>

                        <div class="cart-items-footer">
                            <form method="post" action="${ctx}/cart" class="clear-form"
                                onsubmit="return confirm('Are you sure you want to clear your cart?');">
                                <input type="hidden" name="csrfToken"
                                    value="<c:out value='${csrfToken}' />">
                                <input type="hidden" name="action" value="clear">
                                <button type="submit" class="link-btn danger">Clear cart</button>
                            </form>

                            <a class="cart-continue-link"
                                href="${ctx}/menu?restaurantId=${cartRestaurantId}">
                                + Add more items
                            </a>
                        </div>
                    </section>

                    <aside class="summary summary-premium">
                        <div class="summary-top">
                            <p class="cart-eyebrow">THE FINAL COUNTDOWN</p>
                            <h2>Order summary</h2>
                            <p class="summary-description">A little something to look forward to.</p>
                        </div>

                        <div class="summary-breakdown">
                            <div class="summary-row">
                                <span>Item subtotal</span>
                                <span>&#8377;<fmt:formatNumber value="${cartTotal}"
                                    minFractionDigits="2" maxFractionDigits="2" /></span>
                            </div>
                            <div class="summary-row">
                                <span>Delivery fee</span>
                                <span class="summary-free">FREE</span>
                            </div>
                        </div>

                        <div class="summary-row total summary-total-premium">
                            <span>Total</span>
                            <strong>&#8377;<fmt:formatNumber value="${cartTotal}"
                                minFractionDigits="2" maxFractionDigits="2" /></strong>
                        </div>

                        <a class="btn btn-primary btn-block cart-checkout-btn"
                            href="${ctx}/checkout">
                            Proceed to checkout <span aria-hidden="true">&rarr;</span>
                        </a>

                        <p class="cart-payment-note">
                            <span aria-hidden="true">&#128274;</span>
                            Your order total is calculated securely.
                        </p>

                        <div class="cart-summary-footer">
                            <span aria-hidden="true">&#10003;</span>
                            Delicious food, coming your way.
                        </div>
                    </aside>

                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>





<%-- 

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
--%>

