<%--
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My orders" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="my-orders-page">
    <div class="my-orders-container">

        <header class="orders-heading">
            <span class="orders-eyebrow">YOUR MUNCHLY JOURNEY</span>
            <h1>My orders<span>.</span></h1>
            <p>Good food, good memories. Everything you've ordered, all in one place.</p>
        </header>

        <c:choose>
            <c:when test="${empty orders}">
                <section class="orders-empty-state">
                    <div class="orders-empty-icon" aria-hidden="true">♡</div>
                    <h2>Your food story starts here</h2>
                    <p>You haven't placed any orders yet. Find something delicious to enjoy.</p>
                    <a class="btn btn-primary" href="${ctx}/home">
                        Explore restaurants
                    </a>
                </section>
            </c:when>

            <c:otherwise>
                <div class="orders-list">

                    <c:forEach var="o" items="${orders}">
                        <article class="order-card">

                            <div class="order-card-top">
                                <div>
                                    <span class="order-label">ORDER</span>
                                    <h2>#<c:out value="${o.orderId}" /></h2>
                                </div>

                                <span class="status status-${o.status}">
                                    <c:out value="${o.status}" />
                                </span>
                            </div>

                            <div class="order-card-body">

                                <%-- Food image with a fallback --%> <%-- 
                                <div class="order-food-image">
                                    <c:set var="orderImg"
                                           value="images/restaurants/order-food.jpg" />

                                    <img
                                        src="${ctx}/${orderImg}"
                                        alt="Food from your Munchly order"
                                        loading="lazy"
                                        onerror="this.style.display='none'; this.parentElement.classList.add('order-food-image-fallback');"
                                    />

                                    <span class="order-food-fallback" aria-hidden="true">
                                        M
                                    </span>
                                </div>

                                <div class="order-main-info">
                                    <span class="order-date-label">PLACED ON</span>

                                    <p class="order-date">
                                        <fmt:formatDate
                                            value="${o.orderDate}"
                                            pattern="dd MMM yyyy, hh:mm a" />
                                    </p>

                                    <span class="order-small-note">
                                        Thank you for choosing Munchly.
                                    </span>
                                </div>

                                <div class="order-total">
                                    <span class="order-date-label">ORDER TOTAL</span>

                                    <strong>
                                        &#8377;<fmt:formatNumber
                                            value="${o.totalAmount}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />
                                    </strong>
                                </div>

                            </div>

                            <footer class="order-card-footer">
                                <span class="order-footer-note">
                                    Order #<c:out value="${o.orderId}" />
                                </span>

                                <a class="order-details-link"
                                   href="${ctx}/orders/details?orderId=${o.orderId}">
                                    View order details
                                    <span aria-hidden="true">→</span>
                                </a>
                            </footer>

                        </article>
                    </c:forEach>

                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>







 VERSION 2 --%>



<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My orders" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="my-orders-page">
    <div class="my-orders-container">

        <header class="orders-heading">
            <span class="orders-eyebrow">YOUR MUNCHLY JOURNEY</span>
            <h1>My orders<span>.</span></h1>
            <p>Good food, good memories. Everything you've ordered, all in one place.</p>
        </header>

        <c:choose>
            <c:when test="${empty orders}">
                <section class="orders-empty-state">
                    <div class="orders-empty-icon" aria-hidden="true">♡</div>
                    <h2>Your food story starts here</h2>
                    <p>You haven't placed any orders yet. Find something delicious to enjoy.</p>
                    <a class="btn btn-primary" href="${ctx}/home">Explore restaurants</a>
                </section>
            </c:when>

            <c:otherwise>
                <div class="orders-list">
                    <c:forEach var="o" items="${orders}">
                    
                    	<%-- order image --%>
                    	<c:set var="orderImg"
       						value="${empty orderImages[o.orderId] ? 'images/placeholder.svg' : orderImages[o.orderId]}" />
      					<%-- --%>
                    
                        <article class="order-card">

                            <div class="order-card-top">
                                <div>
                                    <span class="order-label">ORDER</span>
                                    <h2>#<c:out value="${o.orderId}" /></h2>
                                </div>

                                <span class="status status-${o.status}">
                                    <c:out value="${o.status}" />
                                </span>
                            </div>

                            <div class="order-card-body">
                                
                                
                                <%-- 
                                <div class="order-food-image" aria-hidden="true">
                                    <img
                                        src="${ctx}/images/restaurants/order-food.jpg"
                                        alt=""
                                        loading="lazy"
                                        onerror="this.style.display='none'; this.parentElement.classList.add('order-food-image-fallback');"
                                    >
                                    <span class="order-food-fallback" aria-hidden="true">M</span>
                                </div>
                                
                                --%>
                                
                                <div class="my-order-image-wrap" area-hidden="true">
								    <img class="my-order-image"
								         src="${ctx}/<c:out value='${orderImg}' />"
								         alt="Food from order #<c:out value='${o.orderId}' />" loading="lazy" data-fallback />
								         <div class="order-food-image" aria-hidden="true">
                                    
                                    <span class="order-food-fallback" aria-hidden="true">M</span>
                                </div>
								</div>

                                <div class="order-main-info">
                                    <span class="order-date-label">PLACED ON</span>
                                    <p class="order-date">
                                        <fmt:formatDate value="${o.orderDate}"
                                            pattern="dd MMM yyyy, hh:mm a" />
                                    </p>
                                    <span class="order-small-note">Thank you for choosing Munchly.</span>
                                </div>

                                <div class="order-total">
                                    <span class="order-date-label">ORDER TOTAL</span>
                                    <strong>
                                        &#8377;<fmt:formatNumber value="${o.totalAmount}"
                                            minFractionDigits="2" maxFractionDigits="2" />
                                    </strong>
                                </div>
                            </div>

                            <footer class="order-card-footer">
                                <%-- <span class="order-footer-note">
                                    Order #<c:out value="${o.orderId}" />
                                </span>--%>
                                <a class="order-details-link"
                                   href="${ctx}/orders/details?orderId=${o.orderId}">
                                    View order details <span aria-hidden="true">→</span>
                                </a>
                            </footer>

                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

 <%-- --%>






<%-- version 1



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
--%>