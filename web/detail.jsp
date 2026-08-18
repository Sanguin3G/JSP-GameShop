<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html lang="en">
    <head>
        <jsp:include page="common/header.jsp" />
        <title>${requestScope.detail.name} | GameShop</title>
    </head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell">
            <jsp:include page="bar.jsp" />
            <section class="detail-hero">
                <div class="container">
                    <div class="row align-items-center g-5">
                        <div class="col-lg-6">
                            <img class="detail-image" src="${requestScope.detail.image}" alt="Cover art for ${requestScope.detail.name}">
                        </div>
                        <div class="col-lg-6 detail-copy">
                            <div class="eyebrow">${requestScope.detail.cat.name} · ${requestScope.detail.releasedate}</div>
                            <h1 class="display-5 fw-bold">${requestScope.detail.name}</h1>
                            <div class="rating mb-3">★ ${requestScope.detail.rating}/5</div>
                            <p class="lead">${requestScope.detail.description}</p>
                            <div class="price mb-4"><fmt:formatNumber value="${requestScope.detail.price}" type="currency" currencySymbol="$" /></div>
                            <c:choose>
                                <c:when test="${sessionScope.account != null}">
                                    <form action="${pageContext.request.contextPath}/cart?pid=${requestScope.detail.id}" method="post" class="d-flex gap-2 align-items-center">
                                        <label class="visually-hidden" for="quantity">Quantity</label>
                                        <input id="quantity" name="num" class="form-control" type="number" min="1" max="20" value="1" style="max-width: 90px">
                                        <button class="btn btn-accent" type="submit">Add to cart</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <a class="btn btn-accent" href="${pageContext.request.contextPath}/login.jsp">Sign in to add to cart</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </section>
            <section class="container py-5">
                <div class="section-heading"><div class="eyebrow">Keep browsing</div><h2>More from this shelf</h2></div>
                <div class="row g-4">
                    <c:forEach items="${requestScope.relatedProducts}" var="p">
                        <div class="col-sm-6 col-lg-3">
                            <article class="product-card">
                                <img src="${p.image}" alt="Cover art for ${p.name}" loading="lazy">
                                <div class="card-body"><h5>${p.name}</h5><div class="rating">★ ${p.rating}/5</div></div>
                                <div class="card-footer p-3 pt-0"><a class="btn btn-outline-primary w-100" href="${pageContext.request.contextPath}/detail?pid=${p.id}">View details</a></div>
                            </article>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
