<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Add a game | GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="row justify-content-center"><div class="col-lg-8">
                <div class="paper-panel">
                    <div class="eyebrow">Catalog editor</div><h1>Add a game</h1><p class="text-secondary">Give it a useful description. “It is good” is not a useful description.</p>
                    <c:if test="${not empty requestScope.errorMessage}"><div class="alert alert-danger">${requestScope.errorMessage}</div></c:if>
                    <form action="${pageContext.request.contextPath}/add" method="post" class="row g-3">
                        <div class="col-md-8"><label class="form-label" for="name">Name</label><input id="name" class="form-control" type="text" name="name" required></div>
                        <div class="col-md-4"><label class="form-label" for="price">Price (USD)</label><input id="price" class="form-control" type="number" name="price" min="0" step="0.01" required></div>
                        <div class="col-12"><label class="form-label" for="image">Cover image URL</label><input id="image" class="form-control" type="url" name="image" placeholder="https://..." required></div>
                        <div class="col-md-6"><label class="form-label" for="releasedate">Release date</label><input id="releasedate" class="form-control" type="date" name="releasedate" required></div>
                        <div class="col-md-3"><label class="form-label" for="rating">Rating</label><input id="rating" class="form-control" type="number" name="rating" min="0" max="5" step="0.1" required></div>
                        <div class="col-md-3"><label class="form-label" for="cid">Category</label><select id="cid" class="form-select" name="cid" required><option value="">Choose...</option><c:forEach items="${requestScope.categories}" var="c"><option value="${c.id}">${c.name}</option></c:forEach></select></div>
                        <div class="col-12"><label class="form-label" for="description">Description</label><textarea id="description" class="form-control" name="description" rows="5" required></textarea></div>
                        <div class="col-12 d-flex gap-2"><button class="btn btn-primary" type="submit">Add to catalog</button><a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/list">Cancel</a></div>
                    </form>
                </div>
            </div></div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
