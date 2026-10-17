<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${editing ? 'Edit restaurant' : 'Add restaurant'}" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main>
    <div class="container narrow-wide">
        <%@ include file="/WEB-INF/views/fragments/admin-nav.jspf" %>
        <div class="card">
            <h1><c:out value="${editing ? 'Edit restaurant' : 'Add restaurant'}" /></h1>

            <c:if test="${not empty formError}">
                <div class="alert alert-error"><c:out value="${formError}" /></div>
            </c:if>

            <form method="post" action="${ctx}/admin/restaurants/form" novalidate>
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                <input type="hidden" name="restaurantId" value="<c:out value='${form.restaurantId}' />">

                <div class="form-group">
                    <label for="name">Name</label>
                    <input type="text" id="name" name="name" maxlength="100" required value="<c:out value='${form.name}' />">
                    <c:if test="${not empty errors.name}"><span class="field-error"><c:out value="${errors.name}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="cuisine">Cuisine</label>
                    <input type="text" id="cuisine" name="cuisine" maxlength="100" required value="<c:out value='${form.cuisine}' />">
                    <c:if test="${not empty errors.cuisine}"><span class="field-error"><c:out value="${errors.cuisine}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="address">Address</label>
                    <input type="text" id="address" name="address" maxlength="255" required value="<c:out value='${form.address}' />">
                    <c:if test="${not empty errors.address}"><span class="field-error"><c:out value="${errors.address}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="3" maxlength="500"><c:out value="${form.description}" /></textarea>
                    <c:if test="${not empty errors.description}"><span class="field-error"><c:out value="${errors.description}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="imagePath">Image path <span class="muted">(optional)</span></label>
                    <input type="text" id="imagePath" name="imagePath" maxlength="200" placeholder="images/restaurants/my-photo.jpg"
                           value="<c:out value='${form.imagePath}' />">
                    <span class="hint">Relative to the web app folder. If the file is missing, a placeholder is shown.</span>
                    <c:if test="${not empty errors.imagePath}"><span class="field-error"><c:out value="${errors.imagePath}" /></span></c:if>
                </div>
                <div class="form-group">
                    <label for="rating">Rating (0 to 5)</label>
                    <input type="text" id="rating" name="rating" maxlength="4" inputmode="decimal" value="<c:out value='${form.rating}' />">
                    <c:if test="${not empty errors.rating}"><span class="field-error"><c:out value="${errors.rating}" /></span></c:if>
                </div>
                <div class="form-group check">
                    <label><input type="checkbox" name="active" <c:if test="${form.active == 'true'}">checked</c:if>>
                        Active (visible to customers)</label>
                </div>

                <button type="submit" class="btn btn-primary">Save</button>
                <a class="btn btn-outline" href="${ctx}/admin/restaurants">Cancel</a>
            </form>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
