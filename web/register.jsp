<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Join GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="row justify-content-center"><div class="col-md-7 col-lg-5"><div class="paper-panel">
                <div class="eyebrow">Make yourself at home</div><h1>Create an account</h1><p class="text-secondary">Save a cart, place an order, and develop strong opinions about release dates.</p>
                <c:if test="${not empty requestScope.error}"><div class="alert alert-danger">${requestScope.error}</div></c:if>
                <form action="${pageContext.request.contextPath}/register" method="post" class="vstack gap-3">
                    <div><label class="form-label" for="user">Username</label><input id="user" name="user" class="form-control" type="text" value="${requestScope.username}" minlength="4" maxlength="20" required></div>
                    <div><label class="form-label" for="pass">Password</label><input id="pass" name="pass" class="form-control" type="password" minlength="6" required></div>
                    <div><label class="form-label" for="repass">Confirm password</label><input id="repass" name="repass" class="form-control" type="password" minlength="6" required></div>
                    <button class="btn btn-primary" type="submit">Create account</button>
                </form>
                <p class="text-secondary mt-4 mb-0">Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Sign in</a></p>
            </div></div></div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
