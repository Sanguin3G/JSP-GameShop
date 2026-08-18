<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html lang="en">
    <head>
        <jsp:include page="common/header.jsp" />
        <title>Manage catalog | GameShop</title>
    </head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-4">
            <div class="d-flex justify-content-between align-items-end mb-4">
                <div><div class="eyebrow">Back office</div><h1 class="mb-1">Manage catalog</h1><p class="text-secondary mb-0">Keep the shelf tidy. Future you will appreciate it.</p></div>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/add">Add a game</a>
            </div>
            <c:if test="${not empty requestScope.errorMessage}"><div class="alert alert-danger">${requestScope.errorMessage}</div></c:if>
            <div class="paper-panel table-responsive">
                <table class="table table-striped">
                    <thead><tr><th>Game</th><th>Category</th><th>Release</th><th>Rating</th><th>Price</th><th class="text-end">Actions</th></tr></thead>
                    <tbody>
                        <c:forEach items="${requestScope.products}" var="p">
                            <tr>
                                <td><div class="d-flex align-items-center gap-3"><img src="${p.image}" width="120" height="60" alt="${p.name}"><strong>${p.name}</strong></div></td>
                                <td>${p.cat.name}</td><td>${p.releasedate}</td><td>★ ${p.rating}</td>
                                <td><fmt:formatNumber value="${p.price}" type="currency" currencySymbol="$" /></td>
                                <td class="text-end text-nowrap"><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/update?pid=${p.id}">Edit</a> <a class="btn btn-sm btn-outline-danger" href="${pageContext.request.contextPath}/delete?pid=${p.id}" onclick="return confirm('Remove this game from the catalog?');">Delete</a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <c:if test="${empty requestScope.products}"><div class="empty-state">The catalog is empty. That is technically a very exclusive store.</div></c:if>
            </div>
            <c:if test="${requestScope.totalPages > 1}">
                <nav class="d-flex justify-content-center mt-4"><ul class="pagination"><c:forEach begin="1" end="${requestScope.totalPages}" var="i"><li class="page-item ${i == requestScope.page ? 'active' : ''}"><a class="page-link" href="${pageContext.request.contextPath}/list?page=${i}">${i}</a></li></c:forEach></ul></nav>
            </c:if>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
