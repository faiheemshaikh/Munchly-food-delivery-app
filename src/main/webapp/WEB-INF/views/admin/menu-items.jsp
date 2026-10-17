<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage menu" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container">
        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>
        <a class="back-link" href="${ctx}/admin/restaurants">&larr; All restaurants</a>
        <div class="page-head">
            <h1>Menu: <c:out value="${restaurant.name}" /></h1>
            <a class="btn btn-primary" href="${ctx}/admin/menu/form?restaurantId=${restaurant.restaurantId}">Add menu item</a>
        </div>

        <c:choose>
            <c:when test="${empty items}">
                <div class="empty-state"><h2>No menu items yet</h2><p>Add the first item for this restaurant.</p></div>
            </c:when>
            <c:otherwise>
                <div class="card table-wrap">
                    <table class="table">
                        <thead>
                        <tr><th>ID</th><th>Name</th><th>Category</th><th class="num">Price</th><th>Availability</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${items}">
                            <tr>
                                <td><c:out value="${item.menuId}" /></td>
                                <td><c:out value="${item.name}" /></td>
                                <td><c:out value="${item.category}" /></td>
                                <td class="num">&#8377;<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.available}"><span class="status status-DELIVERED">Available</span></c:when>
                                        <c:otherwise><span class="status status-CANCELLED">Unavailable</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="actions">
                                    <a href="${ctx}/admin/menu/form?menuId=${item.menuId}">Edit</a>
                                    <form method="post" action="${ctx}/admin/menu" class="inline-form">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                        <input type="hidden" name="menuId" value="${item.menuId}">
                                        <c:choose>
                                            <c:when test="${item.available}">
                                                <input type="hidden" name="action" value="unavailable">
                                                <button type="submit" class="link-btn danger">Mark unavailable</button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="action" value="available">
                                                <button type="submit" class="link-btn">Mark available</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
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
