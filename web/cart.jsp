<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Your cart | GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="section-heading"><div class="eyebrow">Your little stack of possibilities</div><h1>Shopping cart</h1><p>Take your time. The games are not going anywhere.</p></div>
            <c:if test="${not empty requestScope.errorMessage}"><div class="alert alert-danger">${requestScope.errorMessage}</div></c:if>
            <c:choose>
                <c:when test="${empty sessionScope.cart or empty sessionScope.cart.items}">
                    <div class="empty-state"><h3>Your cart is taking a quiet moment.</h3><p>Add a game from its details page and it will appear here.</p><a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Browse games</a></div>
                </c:when>
                <c:otherwise>
                    <div class="paper-panel table-responsive">
                        <table class="table table-striped">
                            <thead><tr><th>Game</th><th>Price</th><th>Quantity</th><th class="text-end">Subtotal</th><th></th></tr></thead>
                            <tbody><c:forEach items="${sessionScope.cart.items}" var="i"><tr>
                                <td><div class="d-flex align-items-center gap-3"><img src="${i.product.image}" width="120" height="60" alt="${i.product.name}"><strong>${i.product.name}</strong></div></td>
                                <td><fmt:formatNumber value="${i.price}" type="currency" currencySymbol="$" /></td>
                                <td><div class="d-flex align-items-center gap-1"><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/process?num=-1&id=${i.product.id}">−</a><span class="px-2">${i.quantity}</span><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/process?num=1&id=${i.product.id}">+</a></div></td>
                                <td class="text-end"><fmt:formatNumber value="${i.subtotal}" type="currency" currencySymbol="$" /></td>
                                <td class="text-end"><form action="${pageContext.request.contextPath}/process" method="post"><input type="hidden" name="id" value="${i.product.id}"><button class="btn btn-sm btn-outline-danger" type="submit">Remove</button></form></td>
                            </tr></c:forEach></tbody>
                        </table>
                    </div>
                    <div class="paper-panel mt-4 d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/home">Continue shopping</a>
                        <div class="text-md-end"><div class="text-secondary">Order total</div><div class="price fs-3"><fmt:formatNumber value="${sessionScope.cart.totalMoney}" type="currency" currencySymbol="$" /></div><a class="btn btn-accent mt-2" href="${pageContext.request.contextPath}/purchase">Place order</a></div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
