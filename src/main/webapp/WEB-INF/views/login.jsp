
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Log in" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="auth-page login-page">
    <section class="auth-card login-card">

        <div class="auth-heading">
            <span class="auth-eyebrow">WELCOME BACK TO MUNCHLY</span>
            <h1>Welcome back!</h1>
            <p class="muted">Your next favourite meal is waiting for you.</p>
        </div>

        <c:if test="${param.registered == '1'}">
            <div class="alert alert-success">
                Your account is ready! Please log in to continue.
            </div>
        </c:if>

        <c:if test="${param.loggedout == '1'}">
            <div class="alert alert-success">
                You've been logged out successfully.
            </div>
        </c:if>

        <c:if test="${not empty loginError}">
            <div class="alert alert-error">
                <c:out value="${loginError}" />
            </div>
        </c:if>

        <form class="auth-form"
              method="post"
              action="${ctx}/login"
              autocomplete="on">

            <input type="hidden" name="csrfToken"
                   value="<c:out value='${csrfToken}' />">

            <input type="hidden" name="next"
                   value="<c:out value='${next}' />">

            <div class="form-group">
                <label for="email">Email address</label>
                <input type="email"
                       id="email"
                       name="email"
                       required
                       maxlength="100"
                       autocomplete="email"
                       placeholder="you@example.com"
                       value="<c:out value='${form.email}' />">
            </div>

            
			<div class="form-group">
			    <label for="password">Password</label>
			
			    <div class="password-wrapper">
			        <input type="password"
			               id="password"
			               name="password"
			               required
			               autocomplete="current-password"
			               placeholder="Enter your password">
			
			        <button type="button"
			                class="password-toggle"
			                id="togglePassword"
			                aria-label="Show password"
			                aria-controls="password"
			                aria-pressed="false">
			
			            <!-- Eye icon -->
			            <svg class="eye-icon" xmlns="http://www.w3.org/2000/svg"
			                 width="20" height="20" viewBox="0 0 24 24"
			                 fill="none" stroke="currentColor"
			                 stroke-width="1.8" stroke-linecap="round"
			                 stroke-linejoin="round" aria-hidden="true">
			                <path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12Z"/>
			                <circle cx="12" cy="12" r="3"/>
			            </svg>
			        </button>
			    </div>
			</div>


            <button type="submit"
                    class="btn btn-primary btn-block auth-submit">
                Log in
            </button>
        </form>

        <p class="muted center auth-footer">
            New to Munchly?
            <a href="${ctx}/register">Create an account</a>
        </p>

    </section>
</main>


<script>
    (function () {
        const passwordInput = document.getElementById("password");
        const toggleButton = document.getElementById("togglePassword");

        if (!passwordInput || !toggleButton) return;

        toggleButton.addEventListener("click", function () {
            const shouldShow = passwordInput.type === "password";

            passwordInput.type = shouldShow ? "text" : "password";

            toggleButton.setAttribute(
                "aria-label",
                shouldShow ? "Hide password" : "Show password"
            );

            toggleButton.setAttribute(
                "aria-pressed",
                String(shouldShow)
            );

            toggleButton.innerHTML = shouldShow
                ? '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m3 3 18 18"/><path d="M10.6 10.6a2 2 0 0 0 2.8 2.8"/><path d="M9.9 5.2A10.8 10.8 0 0 1 12 5c6.4 0 10 7 10 7a15.8 15.8 0 0 1-3.1 3.9"/><path d="M6.2 6.2C3.5 8 2 12 2 12s3.6 7 10 7a10.7 10.7 0 0 0 3.1-.5"/></svg>'
                : '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/></svg>';
        });
    })();
</script>


<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
