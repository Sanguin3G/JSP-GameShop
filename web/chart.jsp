<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
    <head>
        <jsp:include page="common/header.jsp" />
        <title>Store stats | GameShop</title>
        <script src="https://www.google.com/jsapi"></script>
        <script>
            google.load('visualization', '1.0', {'packages':['corechart']});
            google.setOnLoadCallback(drawCharts);
            function drawCharts() {
                var priceData = new google.visualization.DataTable();
                priceData.addColumn('string', 'Game');
                priceData.addColumn('number', 'Price');
                priceData.addRows(${requestScope.topProductsJson});
                var ratingData = new google.visualization.DataTable();
                ratingData.addColumn('string', 'Game');
                ratingData.addColumn('number', 'Rating');
                ratingData.addRows(${requestScope.topRatedProductsJson});
                var options = {backgroundColor: 'transparent', legend: {position: 'none'}, colors: ['#d97852'], chartArea: {left: 80, top: 20, width: '82%', height: '78%'}};
                new google.visualization.ColumnChart(document.getElementById('price-chart')).draw(priceData, Object.assign({}, options, {title: 'Highest-priced games'}));
                new google.visualization.BarChart(document.getElementById('rating-chart')).draw(ratingData, Object.assign({}, options, {title: 'Highest-rated games', colors: ['#386f72'], hAxis: {minValue: 0, maxValue: 5}}));
            }
        </script>
    </head>
    <body>
        <jsp:include page="menu.jsp" />
        <main class="page-shell container py-5">
            <div class="section-heading"><div class="eyebrow">A little data, for flavor</div><h1>Store stats</h1><p>Useful enough to spot a trend, not useful enough to replace playing the games.</p></div>
            <div class="row g-4"><div class="col-lg-6"><div class="paper-panel"><div id="price-chart" style="height: 420px"></div></div></div><div class="col-lg-6"><div class="paper-panel"><div id="rating-chart" style="height: 420px"></div></div></div></div>
        </main>
        <jsp:include page="footer.jsp" />
    </body>
</html>
