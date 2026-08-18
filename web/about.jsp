<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>About GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell">
            <section class="hero"><div class="container"><div class="eyebrow">A small shop with good taste</div><h1 class="display-5">GameShop is here to make browsing feel like browsing.</h1><p>We keep the catalog focused, the descriptions useful, and the shelves free of the digital equivalent of a junk drawer.</p></div></section>
            <section class="container py-5"><div class="row g-4"><div class="col-md-4"><div class="paper-panel h-100"><div class="eyebrow">01</div><h2>Discover</h2><p class="text-secondary">Search by name or browse categories when you know the mood but not the title.</p></div></div><div class="col-md-4"><div class="paper-panel h-100"><div class="eyebrow">02</div><h2>Choose</h2><p class="text-secondary">Every game gets a clear image, a useful summary, a release date, and a rating.</p></div></div><div class="col-md-4"><div class="paper-panel h-100"><div class="eyebrow">03</div><h2>Play</h2><p class="text-secondary">Add games to a session cart and place a demo order when your shortlist becomes decisive.</p></div></div></div><div class="text-center mt-5"><a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Browse the catalog</a></div></section>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
