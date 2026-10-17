<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${editing ? 'Edit menu item' : 'Add menu item'}" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container narrow-wide">
        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>
        <a class="back-link" href="${ctx}/admin/menu?restaurantId=${restaurant.restaurantId}">&larr; Back to menu</a>
        <div class="card">
            <h1><c:out value="${editing ? 'Edit menu item' : 'Add menu item'}" /></h1>
            <p class="muted">Restaurant: <strong><c:out value="${restaurant.name}" /></strong></p>

            <c:if test="${not empty formError}">
                <div class="alert alert-error"><c:out value="${formError}" /></div>
            </c:if>

            <form method="post" action="${ctx}/admin/menu/form" novalidate>
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                <input type="hidden" name="menuId" value="<c:out value='${form.menuId}' />">
                <input type="hidden" name="restaurantId" value="<c:out value='${restaurant.restaurantId}' />">

                <div class="form-group">
                    <label for="name">Name</label>
                    <input type="text" id="name" name="name" maxlength="100" required value="<c:out value='${form.name}' />">
                    <c:if test="${not empty errors.name}"><span class="field-error"><c:out value="${errors.name}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="category">Category</label>
                    <input type="text" id="category" name="category" maxlength="50" placeholder="Mains, Desserts, Beverages&hellip;"
                           value="<c:out value='${form.category}' />">
                    <c:if test="${not empty errors.category}"><span class="field-error"><c:out value="${errors.category}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="price">Price (&#8377;)</label>
                    <input type="text" id="price" name="price" maxlength="10" inputmode="decimal" required value="<c:out value='${form.price}' />">
                    <c:if test="${not empty errors.price}"><span class="field-error"><c:out value="${errors.price}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="3" maxlength="500"><c:out value="${form.description}" /></textarea>
                    <c:if test="${not empty errors.description}"><span class="field-error"><c:out value="${errors.description}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="imagePath">Image path <span class="muted">(optional)</span></label>
                    <input type="text" id="imagePath" name="imagePath" maxlength="200" placeholder="images/food/my-dish.jpg"
                           value="<c:out value='${form.imagePath}' />">
                    <c:if test="${not empty errors.imagePath}"><span class="field-error"><c:out value="${errors.imagePath}" /></span></c:if>
                </div>
                <div class="form-group check">
                    <label><input type="checkbox" name="available" <c:if test="${form.available == 'true'}">checked</c:if>>
                        Available for ordering</label>
                </div>

                <button type="submit" class="btn btn-primary">Save</button>
                <a class="btn btn-outline" href="${ctx}/admin/menu?restaurantId=${restaurant.restaurantId}">Cancel</a>
            </form>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
