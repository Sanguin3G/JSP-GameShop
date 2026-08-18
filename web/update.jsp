<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head><jsp:include page="common/header.jsp" /><title>Edit game | GameShop</title></head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="row justify-content-center"><div class="col-lg-8"><div class="paper-panel">
                <div class="eyebrow">Catalog editor</div><h1>Edit game</h1>
                <c:if test="${not empty requestScope.errorMessage}"><div class="alert alert-danger">${requestScope.errorMessage}</div></c:if>
                <c:set var="p" value="${requestScope.product}" />
                <form action="${pageContext.request.contextPath}/update" method="post" class="row g-3">
                    <input type="hidden" name="id" value="${p.id}">
                    <div class="col-md-8"><label class="form-label" for="name">Name</label><input id="name" class="form-control" type="text" name="name" value="${p.name}" required></div>
                    <div class="col-md-4"><label class="form-label" for="price">Price (USD)</label><input id="price" class="form-control" type="number" name="price" min="0" step="0.01" value="${p.price}" required></div>
                    <div class="col-12"><label class="form-label" for="image">Cover image URL</label><input id="image" class="form-control" type="url" name="image" value="${p.image}" required></div>
                    <div class="col-md-6"><label class="form-label" for="releasedate">Release date</label><input id="releasedate" class="form-control" type="date" name="releasedate" value="${p.releaseDateInput}" required></div>
                    <div class="col-md-3"><label class="form-label" for="rating">Rating</label><input id="rating" class="form-control" type="number" name="rating" min="0" max="5" step="0.1" value="${p.rating}" required></div>
                    <div class="col-md-3"><label class="form-label" for="cid">Category</label><select id="cid" class="form-select" name="cid" required><c:forEach items="${requestScope.categories}" var="c"><option value="${c.id}" ${p.cat.id == c.id ? 'selected' : ''}>${c.name}</option></c:forEach></select></div>
                    <div class="col-12"><label class="form-label" for="description">Description</label><textarea id="description" class="form-control" name="description" rows="5" required>${p.description}</textarea></div>
                    <div class="col-12 d-flex gap-2"><button class="btn btn-primary" type="submit">Save changes</button><a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/list">Cancel</a></div>
                </form>
            </div></div></div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
