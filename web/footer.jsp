<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<footer class="site-footer">
    <div class="container d-flex flex-column flex-md-row justify-content-between gap-3">
        <div>
            <div class="footer-brand">GameShop</div>
            <small>Good games, sensible shelves, no mysterious launcher required.</small>
        </div>
        <div class="d-flex gap-3 align-items-center">
            <a href="${pageContext.request.contextPath}/about.jsp">About</a>
            <a href="${pageContext.request.contextPath}/home">Browse games</a>
            <c:if test="${sessionScope.account.adminLevel == 1}"><a href="${pageContext.request.contextPath}/chart">Stats</a></c:if>
        </div>
    </div>
</footer>
<jsp:include page="common/scripts.jsp" />
