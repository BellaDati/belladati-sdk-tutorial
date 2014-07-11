<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${report.name} - Step 4 - BellaDati SDK Tutorial</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/charts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/loader.js"></script>
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<!-- We're using jQuery for convenience - BellaDati charts rendering doesn't require it -->
</head>
<body onLoad="loadViews('${pageContext.request.contextPath}')">
	<header>
		<a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/images/logo.png" /></a>
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