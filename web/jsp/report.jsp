<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Step 3 - BellaDati SDK Tutorial</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/charts.js"></script>
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<!-- We're using jQuery for convenience - BellaDati charts rendering doesn't require it -->
<script>
function loadViews() {
	// iterate over all wrappers and load their chart contents
	$(".wrapper").each(function() {
		loadViewContent($(this));
	});
}
function loadViewContent(wrapper) {
	var id = wrapper.data("view-id"); // ID of the chart
	var url = "${pageContext.request.contextPath}/chart/" + id;
	
	// get the chart contents from our server
	$.get(url, function(response) { 
		var $container = wrapper.find(".chart");
	
		// clear any existing content from the chart container
		$container.empty();
		
		// create the chart and display it
		var chart = Charts.create("chart-" + id, response.content);
		chart.resize($container.width(), $container.height());
	});
};
</script>
</head>
<body onLoad="loadViews()">
	<header>
		<img src="${pageContext.request.contextPath}/images/logo.png" />
		<a class="logout button" href="${pageContext.request.contextPath}/logout">Logout</a>
	</header>
	<section>
		<%-- Since we injected the whole report, we can conveniently access its fields --%>
		<h1>${report.name}</h1>
		<div>
			<%-- iterate over all views that are charts --%>
			<c:forEach var="view" items="${report.views}">
				<c:if test="${view.type == 'CHART'}">
					<%-- build a wrapper element in which to display the chart --%>
					<div class="wrapper" data-view-id="${view.id}">
						<span class="title">${view.name}</span>
						<div class="content chart" id="chart-${view.id}"></div>
					</div>
				</c:if>
			</c:forEach>
		</div>
	</section>
</body>
</html>