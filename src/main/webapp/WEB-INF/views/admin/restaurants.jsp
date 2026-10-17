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
