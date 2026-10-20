<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Order confirmed" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>



<main class="order-confirmation-page">
    <div class="confirmation-scene">

        <!-- Left decorative panel -->
        <aside class="confirmation-side confirmation-side-left"
               aria-label="Munchly food inspiration">
            <div class="side-decoration side-decoration-left">
                <span class="decor-leaf decor-leaf-one"></span>
                <span class="decor-leaf decor-leaf-two"></span>
                <span class="decor-spark decor-spark-one"></span>
                <span class="decor-spark decor-spark-two"></span>
            </div>

            <div class="side-message">
                <span class="side-message-label">A little Munchly magic</span>
                <p>Good food<br>brings people<br>together <span aria-hidden="true">♡</span></p>
                <span class="side-message-line"></span>
            </div>

            <div class="side-food-illustration" aria-hidden="true">
                <span class="food-plate">
                    <span class="food-herb food-herb-one"></span>
                    <span class="food-herb food-herb-two"></span>
                    <span class="food-piece food-piece-one"></span>
                    <span class="food-piece food-piece-two"></span>
                    <span class="food-piece food-piece-three"></span>
                </span>
            </div>
        </aside>

        <!-- Existing order confirmation card -->
        <div class="card confirmation">

            <div class="tick" aria-hidden="true">&#10003;</div>
            
            <!-- enhanced card wording and ui -->
			<h1>Order placed successfully!</h1>

			<p class="confirmation-subtitle">
    			Thank you for ordering with Munchly.
    			Your meal is one step closer to you.
			</p>

			<p class="order-number-pill">
			    Order number #<strong><c:out value="${order.orderId}" /></strong>
			</p>
			
			
			<div class="confirmation-details">
			
			    <div class="confirmation-detail">
			        <span class="detail-icon" aria-hidden="true">&#128197;</span>
			        <div>
			            <span class="detail-label">Status</span>
			            <span class="status-pill">
			                <c:out value="${order.status}" />
			            </span>
			        </div>
			    </div>
			
			    <div class="confirmation-detail">
			        <span class="detail-icon" aria-hidden="true">&#128336;</span>
			        <div>
			            <span class="detail-label">Placed on</span>
			            <span class="detail-value">
			                <fmt:formatDate value="${order.orderDate}"
			                                pattern="dd MMM yyyy, hh:mm a" />
			            </span>
			        </div>
			    </div>
			
			    <div class="confirmation-detail">
			        <span class="detail-icon" aria-hidden="true">&#128205;</span>
			        <div>
			            <span class="detail-label">Deliver to</span>
			            <span class="detail-value">
			                <c:out value="${order.deliveryAddress}" />
			            </span>
			        </div>
			    </div>
			
			</div>
			
            <h2 class="confirmation-summary-title">Order summary</h2>
            
            <%-- 
            <h1>Thank you! Your order is placed.</h1>

            <p class="muted">
                Order number
                <strong>#<c:out value="${order.orderId}" /></strong>
            </p>
            

            <dl class="details">
                <dt>Status</dt>
                <dd><c:out value="${order.status}" /></dd>

                <dt>Placed on</dt>
                <dd>
                    <fmt:formatDate value="${order.orderDate}"
                                    pattern="dd MMM yyyy, hh:mm a" />
                </dd>

                <dt>Deliver to</dt>
                <dd><c:out value="${order.deliveryAddress}" /></dd>
            </dl>
            
            --%>
            
            

            <table class="table">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th class="num">Qty</th>
                        <th class="num">Price</th>
                        <th class="num">Amount</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="line" items="${orderLines}">
                        <tr>
                            <td><c:out value="${line.itemName}" /></td>
                            <td class="num"><c:out value="${line.quantity}" /></td>
                            <td class="num">
                                &#8377;<fmt:formatNumber value="${line.unitPrice}"
                                    minFractionDigits="2" maxFractionDigits="2" />
                            </td>
                            <td class="num">
                                &#8377;<fmt:formatNumber value="${line.subtotal}"
                                    minFractionDigits="2" maxFractionDigits="2" />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>

                <tfoot>
                    <tr>
                        <td colspan="3"><strong>Total</strong></td>
                        <td class="num">
                            <strong>
                                &#8377;<fmt:formatNumber value="${order.totalAmount}"
                                    minFractionDigits="2" maxFractionDigits="2" />
                            </strong>
                        </td>
                    </tr>
                </tfoot>
            </table>

            <div class="confirmation-actions">
                <a class="btn btn-primary" href="${ctx}/orders">View my orders</a>
                <a class="btn btn-outline" href="${ctx}/home">Order more food</a>
            </div>
        </div>

        <!-- Right decorative panel -->
        <aside class="confirmation-side confirmation-side-right"
               aria-label="Fresh food from Munchly">
            <div class="side-decoration side-decoration-right">
                <span class="decor-leaf decor-leaf-three"></span>
                <span class="decor-spark decor-spark-three"></span>
                <span class="decor-spark decor-spark-four"></span>
            </div>

            <div class="side-message">
                <span class="chef-hat" aria-hidden="true">&#9825;</span>
                <span class="side-message-label">Made with care</span>
                <p>Freshly made<br>just for you <span aria-hidden="true">♡</span></p>
                <span class="side-message-line"></span>
            </div>

            <div class="side-food-illustration side-food-illustration-right"
                 aria-hidden="true">
                <span class="pizza-plate">
                    <span class="pizza-topping topping-one"></span>
                    <span class="pizza-topping topping-two"></span>
                    <span class="pizza-topping topping-three"></span>
                    <span class="pizza-leaf"></span>
                </span>
            </div>
        </aside>

    </div>
</main>




<%-- 
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
--%>


<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
