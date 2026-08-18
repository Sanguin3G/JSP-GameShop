<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Edit account | GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="row justify-content-center"><div class="col-lg-6"><div class="paper-panel">
                <div class="eyebrow">Administrator tools</div><h1>Edit account</h1>
                <c:set var="a" value="${requestScope.account}" />
                <c:if test="${not empty requestScope.errorMessage}"><div class="alert alert-danger">${requestScope.errorMessage}</div></c:if>
                <form method="post" action="${pageContext.request.contextPath}/updateacc" class="vstack gap-3">
                    <input type="hidden" name="aid" value="${a.id}">
                    <div><label class="form-label" for="username">Username</label><input id="username" class="form-control" type="text" name="username" value="${a.username}" required></div>
                    <div><label class="form-label" for="pass">Password</label><input id="pass" class="form-control" type="password" name="pass" value="${a.pass}" required></div>
                    <div><label class="form-label" for="admin">Access level</label><select id="admin" class="form-select" name="admin" ${a.id == sessionScope.account.id ? 'disabled' : ''}><option value="0" ${a.adminLevel == 0 ? 'selected' : ''}>Customer</option><option value="1" ${a.adminLevel == 1 ? 'selected' : ''}>Administrator</option></select><c:if test="${a.id == sessionScope.account.id}"><input type="hidden" name="admin" value="1"><div class="form-text">You cannot remove your own administrator access.</div></c:if></div>
                    <div class="d-flex gap-2"><button class="btn btn-primary" type="submit">Save account</button><a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/account">Cancel</a></div>
                </form>
            </div></div></div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
