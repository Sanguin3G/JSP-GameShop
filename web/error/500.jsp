<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Server Error</title>
        <jsp:include page="../common/header.jsp" />
    </head>
    <body>
        <main class="page-shell container py-5"><div class="paper-panel text-center">
                <h1 class="display-1">500</h1>
                <h2>Internal Server Error</h2>
                <p class="lead">Sorry, something went wrong on our end.</p>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">Return to Home</a>
        </div></main>
        <jsp:include page="../common/scripts.jsp" />
    </body>
</html>
