<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html lang="en">
    <head>
        <jsp:include page="common/header.jsp" />
        <title>Browse games | GameShop</title>
    </head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell">
            <section class="hero">
                <div class="container">
                    <div class="eyebrow">A well-curated digital shelf</div>
                    <h1 class="display-4">Find the next game you’ll happily lose a weekend to.</h1>
                    <p>Big adventures, clever indies, and a few excellent excuses to postpone doing the dishes.</p>
                    <a class="btn btn-accent mt-2" href="#catalog">Explore the catalog</a>
                </div>
            </section>
            <jsp:include page="bar.jsp" />
            <section id="catalog" class="container py-3">
                <c:if test="${not empty requestScope.successMessage}"><div class="alert alert-success mt-4">${requestScope.successMessage}</div></c:if>
                <div class="section-heading d-flex justify-content-between align-items-end gap-3">
                    <div>
                        <c:choose>
                            <c:when test="${not empty requestScope.searchTerm}">
                                <div class="eyebrow">Search results</div>
                                <h2>Games matching “${requestScope.searchTerm}”</h2>
                                <p>${requestScope.totalResults} result(s), carefully unearthed.</p>
                            </c:when>
                            <c:when test="${not empty requestScope.currentCategory}">
                                <div class="eyebrow">Category shelf</div>
                                <h2>${requestScope.currentCategory.name}</h2>
                                <p>${requestScope.currentCategory.description}</p>
                            </c:when>
                            <c:otherwise>
                                <div class="eyebrow">Freshly arranged</div>
                                <h2>Popular on the shelf</h2>
                                <p>Reliable favorites and a few wild cards, all in one place.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${empty requestScope.products}">
                        <div class="empty-state">
                            <h3>No games found</h3>
                            <p>Try a broader search or wander back to the full catalog.</p>
                            <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/home">Show all games</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-4">
                            <c:forEach items="${requestScope.products}" var="p">
                                <div class="col-sm-6 col-lg-3">
                                    <article class="product-card">
                                        <img src="${p.image}" alt="Cover art for ${p.name}" loading="lazy">
                                        <div class="card-body">
                                            <div class="meta mb-1">${p.cat.name} · ${p.releasedate}</div>
                                            <h5>${p.name}</h5>
                                            <div class="rating mb-2">★ ${p.rating}/5</div>
                                            <div class="price"><fmt:formatNumber value="${p.price}" type="currency" currencySymbol="$" /></div>
                                        </div>
                                        <div class="card-footer p-3 pt-0">
                                            <a class="btn btn-outline-primary w-100" href="${pageContext.request.contextPath}/detail?pid=${p.id}">View details</a>
                                        </div>
                                    </article>
                                </div>
                            </c:forEach>
                        </div>
                        <c:if test="${requestScope.totalPages > 1}">
                            <nav class="d-flex justify-content-center mt-5" aria-label="Catalog pages">
                                <ul class="pagination">
                                    <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                                        <li class="page-item ${i == requestScope.page ? 'active' : ''}">
                                            <c:choose>
                                                <c:when test="${not empty requestScope.searchTerm}"><a class="page-link" href="${pageContext.request.contextPath}/search?search=${requestScope.searchTerm}&amp;page=${i}">${i}</a></c:when>
                                                <c:when test="${not empty requestScope.currentCategory}"><a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCategory.id}&amp;page=${i}">${i}</a></c:when>
                                                <c:otherwise><a class="page-link" href="${pageContext.request.contextPath}/home?page=${i}">${i}</a></c:otherwise>
                                            </c:choose>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </nav>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
