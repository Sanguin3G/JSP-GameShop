<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<header class="site-header">
    <div class="container">
        <nav class="navbar navbar-expand-lg">
            <a class="navbar-brand brand-mark" href="${pageContext.request.contextPath}/home">
                <span class="brand-icon">✦</span> GameShop
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                    aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="mainNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Browse</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/about.jsp">About</a></li>
                    <c:if test="${sessionScope.account.adminLevel == 1}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/list">Manage</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/chart">Stats</a></li>
                    </c:if>
                </ul>
                <form action="${pageContext.request.contextPath}/search" method="get" class="search-box me-lg-3 mb-3 mb-lg-0">
                    <label class="visually-hidden" for="site-search">Search games</label>
                    <input id="site-search" name="search" value="${requestScope.searchTerm}" type="search" placeholder="Search games..." autocomplete="off">
                    <button type="submit" aria-label="Search">⌕</button>
                </form>
                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/cart" class="cart-link">
                        Cart <span class="cart-count">${empty sessionScope.size ? 0 : sessionScope.size}</span>
                    </a>
                    <c:choose>
                        <c:when test="${sessionScope.account != null}">
                            <div class="dropdown">
                                <button class="btn account-chip dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    ${sessionScope.account.username}
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/account">Account</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Sign out</a></li>
                                </ul>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-primary">Sign in</a>
                            <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary">Join</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </nav>
    </div>
</header>
