<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Account | GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="section-heading"><div class="eyebrow">Your corner of the shop</div><h1>Account</h1><p>Signed in as <strong>${sessionScope.account.username}</strong>.</p></div>
            <div class="row g-4">
                <div class="col-md-5"><div class="paper-panel"><h3>Account details</h3><dl class="row mt-3 mb-0"><dt class="col-5 text-secondary">Username</dt><dd class="col-7">${sessionScope.account.username}</dd><dt class="col-5 text-secondary">Access</dt><dd class="col-7"><c:choose><c:when test="${sessionScope.account.adminLevel == 1}">Administrator</c:when><c:otherwise>Customer</c:otherwise></c:choose></dd></dl><c:if test="${sessionScope.account.adminLevel == 1}"><a class="btn btn-outline-primary mt-4" href="${pageContext.request.contextPath}/updateacc?aid=${sessionScope.account.id}">Edit account</a></c:if></div></div>
                <c:if test="${sessionScope.account.adminLevel == 1}"><div class="col-md-7"><div class="paper-panel"><div class="d-flex justify-content-between align-items-center mb-3"><h3 class="mb-0">Customer accounts</h3><span class="badge text-bg-light">${requestScope.totalPages > 0 ? 'Admin view' : ''}</span></div><div class="table-responsive"><table class="table table-sm"><thead><tr><th>Username</th><th class="text-end">Action</th></tr></thead><tbody><c:forEach items="${requestScope.accounts}" var="a"><tr><td>${a.username}</td><td class="text-end"><c:if test="${a.id != sessionScope.account.id}"><a class="btn btn-sm btn-outline-danger" href="${pageContext.request.contextPath}/remove?uid=${a.id}" onclick="return confirm('Remove this account?');">Remove</a></c:if></td></tr></c:forEach></tbody></table></div></div></div></c:if>
            </div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
