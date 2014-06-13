<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Step 1 - BellaDati SDK Tutorial</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/charts.js"></script>
</head>
<body>
	<div id="chart"></div>
	<script>
		var json = ${chart};
		var chart = Charts.create("chart", json.content);
		chart.resize(800, 600);
	</script>
</body>
</html>