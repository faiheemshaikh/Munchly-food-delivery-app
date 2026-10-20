
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Manage restaurants" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="restaurant-admin-page">
    <div class="container restaurant-admin-container">

        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>

        <section class="restaurant-page-intro">
            <div class="restaurant-page-title">
                <span class="restaurant-eyebrow">MUNCHLY MANAGEMENT</span>
                <h1>Restaurants<span class="restaurant-title-dot">.</span></h1>
                <p>
                    Manage your restaurant partners and keep your food
                    marketplace running smoothly.
                </p>
            </div>

            <a class="btn btn-primary restaurant-add-button"
               href="${ctx}/admin/restaurants/form">
                <span aria-hidden="true">+</span>
                Add restaurant
            </a>
        </section>

        <section class="restaurant-summary" aria-label="Restaurant summary">
            <div class="restaurant-summary-icon" aria-hidden="true">⌂</div>
            <div>
                <span class="restaurant-summary-label">YOUR RESTAURANT DIRECTORY</span>
                <div class="restaurant-summary-value">
                    <c:out value="${restaurants.size()}" />
                    <span>restaurants listed</span>
                </div>
            </div>
        </section>

        <section class="restaurant-table-section"
                 aria-label="Manage restaurants">
            <div class="restaurant-table-heading">
                <div>
                    <h2>All restaurants</h2>
                    <p>Review details, manage menus, and update availability.</p>
                </div>
            </div>

            <div class="card table-wrap restaurant-table-wrap">
                <table class="table restaurant-table">
                    <thead>
                        <tr>
                            <th class="restaurant-id-column">ID</th>
                            <th>Name</th>
                            <th>Cuisine</th>
                            <th class="num">Rating</th>
                            <th>Status</th>
                            <th class="restaurant-actions-column">Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="r" items="${restaurants}">
                            <tr>
                                <td class="restaurant-id">
                                    <c:out value="${r.restaurantId}" />
                                </td>

                                <td class="restaurant-name">
                                    <span><c:out value="${r.name}" /></span>
                                </td>

                                <td class="restaurant-cuisine">
                                    <c:out value="${r.cuisine}" />
                                </td>

                                <td class="num restaurant-rating">
                                    <span class="rating-star" aria-hidden="true">★</span>
                                    <fmt:formatNumber
                                        value="${r.rating}"
                                        minFractionDigits="1"
                                        maxFractionDigits="1" />
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${r.active}">
                                            <span class="restaurant-status restaurant-status-active">
                                                <span class="restaurant-status-dot"></span>
                                                Active
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="restaurant-status restaurant-status-inactive">
                                                <span class="restaurant-status-dot"></span>
                                                Inactive
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <div class="restaurant-actions">
                                        <a class="restaurant-action-link"
                                           href="${ctx}/admin/restaurants/form?restaurantId=${r.restaurantId}">
                                            Edit
                                        </a>

                                        <a class="restaurant-action-link"
                                           href="${ctx}/admin/menu?restaurantId=${r.restaurantId}">
                                            Menu
                                        </a>

                                        <form method="post"
                                              action="${ctx}/admin/restaurants"
                                              class="inline-form restaurant-status-form">

                                            <input type="hidden"
                                                   name="csrfToken"
                                                   value="<c:out value='${csrfToken}' />">

                                            <input type="hidden"
                                                   name="restaurantId"
                                                   value="<c:out value='${r.restaurantId}' />">

                                            <c:choose>
                                                <c:when test="${r.active}">
                                                    <input type="hidden"
                                                           name="action"
                                                           value="deactivate">

                                                    <button type="submit"
                                                            class="restaurant-action-link restaurant-action-danger">
                                                        Deactivate
                                                    </button>
                                                </c:when>

                                                <c:otherwise>
                                                    <input type="hidden"
                                                           name="action"
                                                           value="activate">

                                                    <button type="submit"
                                                            class="restaurant-action-link restaurant-action-activate">
                                                        Activate
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty restaurants}">
                            <tr>
                                <td colspan="6" class="restaurant-empty-cell">
                                    <div class="restaurant-empty-state">
                                        <span aria-hidden="true">♡</span>
                                        <h3>No restaurants yet</h3>
                                        <p>Add your first restaurant to get started.</p>
                                        <a class="btn btn-primary"
                                           href="${ctx}/admin/restaurants/form">
                                            Add restaurant
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </section>

        <div class="restaurant-page-note">
            <span aria-hidden="true">✦</span>
            <p>Great food experiences start with great restaurant partners.</p>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>



<%-- 

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage restaurants" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>
        <div class="page-head">
            <h1>Restaurants</h1>
            <a class="btn btn-primary" href="${ctx}/admin/restaurants/form">Add restaurant</a>
        </div>

        <div class="card table-wrap">
            <table class="table">
                <thead>
                <tr><th>ID</th><th>Name</th><th>Cuisine</th><th class="num">Rating</th><th>Status</th><th>Actions</th></tr>
                </thead>
                <tbody>
                <c:forEach var="r" items="${restaurants}">
                    <tr>
                        <td><c:out value="${r.restaurantId}" /></td>
                        <td><c:out value="${r.name}" /></td>
                        <td><c:out value="${r.cuisine}" /></td>
                        <td class="num"><fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1" /></td>
                        <td>
                            <c:choose>
                                <c:when test="${r.active}"><span class="status status-DELIVERED">Active</span></c:when>
                                <c:otherwise><span class="status status-CANCELLED">Inactive</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <a href="${ctx}/admin/restaurants/form?restaurantId=${r.restaurantId}">Edit</a>
                            <a href="${ctx}/admin/menu?restaurantId=${r.restaurantId}">Menu</a>
                            <form method="post" action="${ctx}/admin/restaurants" class="inline-form">
                                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                <input type="hidden" name="restaurantId" value="${r.restaurantId}">
                                <c:choose>
                                    <c:when test="${r.active}">
                                        <input type="hidden" name="action" value="deactivate">
                                        <button type="submit" class="link-btn danger">Deactivate</button>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="action" value="activate">
                                        <button type="submit" class="link-btn">Activate</button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

--%>