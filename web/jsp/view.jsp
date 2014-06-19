<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Step 1 - BellaDati SDK Tutorial</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/charts.js"></script>
</head>
<body>
	<header>
		<img src="${pageContext.request.contextPath}/images/logo.png" />
		<a class="logout button" href="${pageContext.request.contextPath}/logout">Logout</a>
	</header>
	<!-- chart contents will be displayed in the div below -->
	<div id="chart"></div>
	<script>
		// inject the chart JSON (server-side)
		var json = ${chart};
		
		// create the chart object using the charts library
		// first parameter "chart" is the ID of the element where charts are inserted
		// second parameter is the chart data itself
		var chart = Charts.create("chart", json.content);
		
		// set the chart size, triggering the chart render
		chart.resize(800, 600);
	</script>
</body>
</html>