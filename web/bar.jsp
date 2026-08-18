<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<nav class="category-bar">
    <div class="container d-flex flex-wrap align-items-center gap-2">
        <span class="eyebrow me-2">Browse by mood</span>
        <a class="nav-link ${empty tag ? 'active' : ''}" href="${pageContext.request.contextPath}/home">All games</a>
        <c:forEach items="${requestScope.categories}" var="category">
            <a class="nav-link ${tag == category.id ? 'active' : ''}" href="${pageContext.request.contextPath}/category?cid=${category.id}">${category.name}</a>
        </c:forEach>
    </div>
</nav>
