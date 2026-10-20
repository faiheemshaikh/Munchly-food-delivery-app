
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Checkout" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="checkout-page">
    <div class="container checkout-container">

        <a class="checkout-back-link" href="${ctx}/cart">
            &larr; Back to cart
        </a>

        <div class="checkout-heading">
            <span class="checkout-eyebrow">CHECKOUT</span>
            <h1>Complete your order.</h1>
            <p>Fresh food, safely delivered to your door.</p>
        </div>

        <c:forEach var="notice" items="${cartNotices}">
            <div class="alert alert-error">
                <c:out value="${notice}" />
            </div>
        </c:forEach>

        <c:if test="${not empty formError}">
            <div class="alert alert-error">
                <c:out value="${formError}" />
            </div>
        </c:if>

        <div class="checkout-layout">

            <!-- Delivery details and payment -->
            <section class="card checkout-form-card">

                <div class="checkout-section-heading">
                    <div class="checkout-section-icon" aria-hidden="true">
                        <span>&#9906;</span>
                    </div>
                    <div>
                        <h2>Delivery details</h2>
                        <p>We'll deliver your food to this address.</p>
                    </div>
                </div>

                <form id="checkout-order-form" method="post"
                      action="${ctx}/checkout"
                      class="checkout-form"
                      novalidate>

                    <input type="hidden"
                           name="csrfToken"
                           value="<c:out value='${csrfToken}' />">

                    <%-- The server validates this against the real cart total. --%>
                    <input type="hidden"
                           name="displayedTotal"
                           value="<c:out value='${checkoutTotal}' />">

                    <div class="form-group checkout-address-group">
                        <label for="deliveryAddress">
                            Delivery address <span class="required-mark">*</span>
                        </label>

                        <textarea
                            id="deliveryAddress"
                            name="deliveryAddress"
                            rows="3"
                            maxlength="255"
                            required
                            placeholder="House / flat no., street, area, city, PIN code"><c:out value="${deliveryAddress}" /></textarea>

                        <c:if test="${not empty addressError}">
                            <span class="field-error">
                                <c:out value="${addressError}" />
                            </span>
                        </c:if>

                        <span class="hint">
                            Pre-filled from your profile. You can change it for this order.
                        </span>
                    </div>
                    
                    
					<div class="form-group checkout-phone-group">
    					<label for="profilePhone">Contact number</label>
    					<input
        					type="tel"
        					id="profilePhone"
        					name="profilePhone"
        					value="<c:out value='${profilePhone}' />"
        					readonly
        					aria-describedby="profilePhoneHint"
        					placeholder="No phone number saved in your profile"
    					>
    					<span class="hint" id="profilePhoneHint">
        					This number comes from your profile and cannot be changed during checkout.
    					</span>
					</div>
                    
                    

                    <%-- Existing payment method: Cash on Delivery. --%>
                    <div class="checkout-payment-section">
                        <div class="checkout-section-heading">
                            <div class="checkout-section-icon" aria-hidden="true">
                                <span>&#8377;</span>
                            </div>
                            <div>
                                <h2>Payment method</h2>
                                <p>Choose how you'd like to pay.</p>
                            </div>
                        </div>

                        <div class="checkout-payment-option">
                            <span class="checkout-payment-check" aria-hidden="true">
                                &#10003;
                            </span>

                            <div class="checkout-payment-symbol" aria-hidden="true">
                                &#128179;
                            </div>

                            <div class="checkout-payment-copy">
                                <strong>Cash on Delivery</strong>
                                <span>Pay when your order arrives.</span>
                            </div>
                        </div>
                    </div>

                    <div class="checkout-security-note">
                        <span aria-hidden="true">&#128274;</span>
                        <p>
                            Your order is secure. Your total is verified
                            before the order is placed.
                        </p>
                    </div>

                    <div class="checkout-mobile-actions">
                        <%-- <button type="submit"
                                class="btn btn-primary"
                                id="place-order-btn">
                            Place order
                        </button>--%>

                        <a class="btn btn-outline" href="${ctx}/cart">
                            Back to cart
                        </a>
                    </div>

                </form>
            </section>

            <!-- Order summary -->
            <aside class="card checkout-summary-card">

                <div class="checkout-section-heading">
                    <div class="checkout-section-icon" aria-hidden="true">
                        <span>&#9776;</span>
                    </div>
                    <div>
                        <h2>Order summary</h2>
                        <p>Review your items before placing the order.</p>
                    </div>
                </div>

                <div class="checkout-restaurant">
                    <div class="checkout-restaurant-icon" aria-hidden="true">
                        &#127860;
                    </div>
                    <div>
                        <h3><c:out value="${cartRestaurantName}" /></h3>
                        <p>Your selected restaurant</p>
                    </div>
                </div>

                <div class="checkout-items">
                    <c:forEach var="line" items="${checkoutLines}">
                        <div class="checkout-item">
                            <div class="checkout-item-details">
                                <p class="checkout-item-name">
                                    <c:out value="${line.quantity}" /> &times;
                                    <c:out value="${line.name}" />
                                </p>

                                <p class="checkout-item-unit-price">
                                    &#8377;<fmt:formatNumber
                                        value="${line.unitPrice}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2" />
                                    each
                                </p>
                            </div>

                            <strong class="checkout-item-subtotal">
                                &#8377;<fmt:formatNumber
                                    value="${line.subtotal}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2" />
                            </strong>
                        </div>
                    </c:forEach>
                </div>

                <div class="checkout-price-breakdown">
                    <div class="checkout-price-row">
                        <span>Item subtotal</span>
                        <span>
                            &#8377;<fmt:formatNumber
                                value="${checkoutTotal}"
                                minFractionDigits="2"
                                maxFractionDigits="2" />
                        </span>
                    </div>

                    <div class="checkout-price-row">
                        <span>Delivery fee</span>
                        <strong class="checkout-free-label">FREE</strong>
                    </div>

                    <div class="checkout-total-row">
                        <strong>Total</strong>
                        <strong>
                            &#8377;<fmt:formatNumber
                                value="${checkoutTotal}"
                                minFractionDigits="2"
                                maxFractionDigits="2" />
                        </strong>
                    </div>
                </div>

                <div class="checkout-desktop-actions">
                    <button type="submit"
                            class="btn btn-primary checkout-place-order"
                            form="checkout-order-form">
                        Place order
                    </button>
                </div>

                <p class="checkout-terms">
                    By placing your order, you confirm your order details.
                </p>

            </aside>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>



<%-- 
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
                         never uses it as the amount to charge. --%><%--
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

<%@ include file="/WEB-INF/views/fragments/footer.jspf" --%>

