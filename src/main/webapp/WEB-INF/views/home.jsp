<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Restaurants near you" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
	
<section class="hero hero-home">
    <div class="container hero-layout">
        <div class="hero-content">
            <div class="hero-eyebrow">
                <span>GOOD FOOD, GOOD MOOD</span>
                <span class="eyebrow-line"></span>
            </div>

            <h1>Your next favourite meal<br>
                is just a few clicks away.</h1>

            <p>
                Discover the best local restaurants, explore diverse cuisines
                and get your favourite food delivered to your door.
            </p>

            <c:if test="${not dbError and not empty restaurants}">
                <div class="search-box">
                    <span class="search-icon" aria-hidden="true">&#9906;</span>
                    <input type="search"
                           id="restaurant-search"
                           placeholder="Search restaurants or cuisines..."
                           aria-label="Search restaurants or cuisines"
                           autocomplete="off">
                </div>
            </c:if>
        </div>

        <div class="hero-visual" aria-hidden="true">
            <div class="hero-image hero-image-main">
                <img src="${ctx}/images/restaurants/biryani-junction.jpg"
                     alt="">
            </div>

            <div class="hero-image hero-image-secondary">
                <img src="${ctx}/images/restaurants/dosa-corner.jpg"
                     alt="">
            </div>

            <div class="hero-note">
                <span class="hero-note-icon">&#127860;</span>
                <span>
                    <strong><c:out value="${fn:length(restaurants)}"/>+</strong>
                    restaurants to explore
                </span>
            </div>
        </div>
    </div>
</section>
	

    <%--<section class="hero hero-home">
        <div class="container">
            <h1>Hungry? Order from the best restaurants.</h1>
            <p>Browse menus, fill your cart and get food delivered to your door.</p>
            <c:if test="${not dbError and not empty restaurants}">
                <div class="search-box">
                    <input type="search" id="restaurant-search" placeholder="Search restaurants or cuisines&hellip;"
                           aria-label="Search restaurants or cuisines" autocomplete="off">
                </div>
            </c:if>
        </div>
    </section> --%>
    

    <div class="container">
        <c:choose>

            <%-- Error state: the database could not be reached --%>
            <c:when test="${dbError}">
                <div class="empty-state">
                    <h2>We&rsquo;re having trouble loading restaurants</h2>
                    <p>Our kitchen systems are temporarily unavailable. Please try again in a few minutes.</p>
                    <a class="btn btn-primary" href="${ctx}/home">Try again</a>
                </div>
            </c:when>

            <%-- Empty state: connected, but no active restaurants --%>
            <c:when test="${empty restaurants}">
                <div class="empty-state">
                    <h2>No restaurants available yet</h2>
                    <p>Please check back soon, new restaurants are added regularly.</p>
                </div>
            </c:when>

            <c:otherwise>
                <div class="chip-row" role="group" aria-label="Filter by cuisine">
                    <button type="button" class="chip active" data-cuisine="" aria-pressed="true">All</button>
                    <c:forEach var="cuisine" items="${cuisines}">
                        <button type="button" class="chip" data-cuisine="<c:out value='${cuisine}' />"
                                aria-pressed="false"><c:out value="${cuisine}" /></button>
                    </c:forEach>
                </div>

                <p class="muted" id="result-count" aria-live="polite">
                    <c:out value="${fn:length(restaurants)}" /> restaurants
                </p>

                <div class="grid" id="restaurant-grid">
                    <c:forEach var="r" items="${restaurants}">
                        <c:set var="img" value="${empty r.imagePath ? 'images/placeholder.svg' : r.imagePath}" />
                        <article class="restaurant-card"
                                 data-search="<c:out value='${fn:toLowerCase(r.name)} ${fn:toLowerCase(r.cuisine)}' />"
                                 data-cuisine="<c:out value='${r.cuisine}' />">
                            <a class="card-link" href="${ctx}/menu?restaurantId=${r.restaurantId}">
                                <div class="card-img">
                                    <img src="${ctx}/<c:out value='${img}' />" alt="<c:out value='${r.name}' />"
                                         loading="lazy" data-fallback>
                                    <c:if test="${r.rating > 0}">
                                        <span class="badge rating">&#9733;
                                            <fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1" />
                                        </span>
                                    </c:if>
                                </div>
                                <div class="card-body">
                                    <h3><c:out value="${r.name}" /></h3>
                                    <p class="cuisine"><c:out value="${r.cuisine}" /></p>
                                    <p class="desc"><c:out value="${r.description}" /></p>
                                    <span class="btn btn-outline btn-small">View menu</span>
                                </div>
                            </a>
                        </article>
                    </c:forEach>
                </div>

                <div class="empty-state" id="no-match" hidden>
                    <h2>No matches</h2>
                    <p>Try a different search or choose another cuisine.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
