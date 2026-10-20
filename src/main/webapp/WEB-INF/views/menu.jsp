<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${restaurant.name}" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<c:set var="bannerImg" value="${empty restaurant.imagePath ? 'images/placeholder.svg' : restaurant.imagePath}" />

<main>
    <div class="container">
        <a class="back-link" href="${ctx}/home">&larr; All restaurants</a>

        <section class="menu-banner">
            <div class="banner-img">
                <img src="${ctx}/<c:out value='${bannerImg}' />" alt="<c:out value='${restaurant.name}' />" data-fallback>
            </div>
            <div class="banner-info">
                <h1><c:out value="${restaurant.name}" /></h1>
                <p class="cuisine"><c:out value="${restaurant.cuisine}" />
                    <c:if test="${restaurant.rating > 0}">
                        &middot; <span class="rating-inline">&#9733;
                            <fmt:formatNumber value="${restaurant.rating}" minFractionDigits="1" maxFractionDigits="1" />
                        </span>
                    </c:if>
                </p>
                <p><c:out value="${restaurant.description}" /></p>
                <p class="muted">&#128205; <c:out value="${restaurant.address}" /></p>
            </div>
        </section>

        <c:choose>
            <c:when test="${itemCount == 0}">
                <div class="empty-state">
                    <h2>No items available right now</h2>
                    <p>This restaurant has nothing on its menu at the moment. Please check back later or explore other restaurants.</p>
                    <a class="btn btn-primary" href="${ctx}/home">Browse restaurants</a>
                </div>
            </c:when>

            <c:otherwise>
                <nav class="chip-row" aria-label="Menu categories">
                    <c:forEach var="entry" items="${menuByCategory}" varStatus="st">
                        <a class="chip" href="#cat-${st.index}"><c:out value="${entry.key}" /></a>
                    </c:forEach>
                </nav>

                <c:url var="loginUrl" value="/login">
                    <c:param name="next" value="/menu?restaurantId=${restaurant.restaurantId}" />
                </c:url>

                <c:forEach var="entry" items="${menuByCategory}" varStatus="st">
                    <section class="menu-section" id="cat-${st.index}">
                        <h2><c:out value="${entry.key}" /></h2>
                        <div class="menu-grid">
                            <c:forEach var="item" items="${entry.value}">
                                <c:set var="itemImg" value="${empty item.imagePath ? 'images/placeholder.svg' : item.imagePath}" />
                                <article class="menu-item">
                                    <img class="menu-thumb" src="${ctx}/<c:out value='${itemImg}' />"
                                         alt="<c:out value='${item.name}' />" loading="lazy" data-fallback>
                                    <div class="menu-info">
                                        <h3><c:out value="${item.name}" /></h3>
                                        <p class="desc"><c:out value="${item.description}" /></p>
                                        <div class="menu-actions">
                                            <span class="price">&#8377;<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2" /></span>

                                            <c:choose>
                                                <c:when test="${not empty sessionScope.userId}">
                                                    <%-- Posts to /cart (implemented in Phase 6). Only the id and quantity are sent;
                                                         the price is always re-read from the database on the server. --%>
                                                    
                                                    <%-- <form method="post" action="${ctx}/cart" class="inline-form">
                                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                                        <input type="hidden" name="action" value="add">
                                                        <input type="hidden" name="menuId" value="${item.menuId}">
                                                        <input type="hidden" name="quantity" value="1">
                                                        <button type="submit" class="btn btn-primary btn-small">Add to cart</button>
                                                    </form> --%>
                                                    
                                                    <form method="post" action="${ctx}/cart" class="inline-form menu-add-form">
                                                    	<input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
    													<input type="hidden" name="action" value="add">
    													<input type="hidden" name="menuId" value="${item.menuId}">

    													<div class="quantity-picker">
        													<button type="button"
                													class="quantity-btn"
                													data-qty-change="-1"
                													aria-label="Decrease quantity">−</button>

        													<input type="number"
               													name="quantity"
              													class="quantity-input"
               													value="1"
               													min="1"
               													max="20"
               													step="1"
               													required
               													aria-label="Quantity">

        													<button type="button"
                													class="quantity-btn"
                													data-qty-change="1"
                													aria-label="Increase quantity">+</button>
    													</div>

    													<button type="submit" class="btn btn-primary btn-small">
        													Add to cart
    													</button>
													</form>
                                                    
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="btn btn-outline btn-small" href="${loginUrl}">Log in to order</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>
                    </section>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</main>


<script>
document.addEventListener("click", function (event) {
    const button = event.target.closest("[data-qty-change]");
    if (!button) return;

    const form = button.closest("form");
    if (!form) return;

    const input = form.querySelector(".quantity-input");
    if (!input) return;

    const current = Number.parseInt(input.value, 10);
    const quantity = Number.isFinite(current) ? current : 1;
    const delta = Number.parseInt(button.dataset.qtyChange, 10);

    input.value = Math.max(1, Math.min(20, quantity + delta));
    input.dispatchEvent(new Event("change", { bubbles: true }));
});

document.addEventListener("input", function (event) {
    if (!event.target.matches(".quantity-input")) return;

    const input = event.target;
    const value = input.value.trim();

    // Allow an empty value while the user is editing.
    if (value === "") return;

    const quantity = Number(value);
    if (!Number.isInteger(quantity)) {
        input.setCustomValidity("Enter a whole number from 1 to 20.");
    } else if (quantity < 1 || quantity > 20) {
        input.setCustomValidity("Quantity must be between 1 and 20.");
    } else {
        input.setCustomValidity("");
    }
});

document.addEventListener("change", function (event) {
    if (!event.target.matches(".quantity-input")) return;

    const input = event.target;
    if (input.value.trim() === "") return;

    const quantity = Number(input.value);
    if (Number.isInteger(quantity)) {
        input.value = Math.max(1, Math.min(20, quantity));
        input.setCustomValidity("");
    }
});
</script>


<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
