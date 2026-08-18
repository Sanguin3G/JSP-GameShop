<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Sign in | GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="row justify-content-center"><div class="col-md-7 col-lg-5"><div class="paper-panel">
                <div class="eyebrow">Welcome back</div><h1>Sign in</h1><p class="text-secondary">Your next good game is waiting patiently.</p>
                <c:if test="${not empty requestScope.error}"><div class="alert alert-danger">${requestScope.error}</div></c:if>
                <c:if test="${not empty sessionScope.registrationSuccess}"><div class="alert alert-success">${sessionScope.registrationSuccess}</div><c:remove var="registrationSuccess" scope="session" /></c:if>
                <form action="${pageContext.request.contextPath}/login" method="post" class="vstack gap-3">
                    <div><label class="form-label" for="user">Username</label><input id="user" name="user" class="form-control" type="text" value="${cookie.username.value}" required autofocus></div>
                    <div><label class="form-label" for="pass">Password</label><input id="pass" name="pass" class="form-control" type="password" required></div>
                    <div class="form-check"><input id="remember" name="remember" value="on" class="form-check-input" type="checkbox" ${cookie.remember.value eq '1' ? 'checked' : ''}><label class="form-check-label" for="remember">Remember my username</label></div>
                    <button class="btn btn-primary" type="submit">Sign in</button>
                </form>
                <p class="text-secondary mt-4 mb-0">New here? <a href="${pageContext.request.contextPath}/register.jsp">Create an account</a></p>
            </div></div></div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
