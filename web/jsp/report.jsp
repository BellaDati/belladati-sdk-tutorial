<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${report.name} - Step 6 - BellaDati SDK Tutorial</title>
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
		<c:if test="${attributeValues != null}">
			<div class="filter-select">
				<c:forEach var="value" items="${attributeValues}">
					<c:if test="${value.value != 'null'}">
						<div class="filter-item">
							<input type="checkbox" class="filter-value" id="${value.value}" name="${value.value}" />
							<label for="${value.value}">${value.label}</label>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</c:if>
		<script>var commonInterval = null;</script>
		<c:if test="${commonInterval != null}">
			<script>commonInterval = ${commonInterval};</script>
			<div class="date-select">
				<div class="date-from">
					<span>Show data from</span>
					<select name="month">
						<option value="1">Jan</option>
						<option value="2">Feb</option>
						<option value="3">Mar</option>
						<option value="4">Apr</option>
						<option value="5">May</option>
						<option value="6">Jun</option>
						<option value="7">Jul</option>
						<option value="8">Aug</option>
						<option value="9">Sep</option>
						<option value="10">Oct</option>
						<option value="11">Nov</option>
						<option value="12">Dec</option>
					</select>
					<select name="year">
						<option>2010</option>
						<option>2011</option>
						<option>2012</option>
						<option>2013</option>
						<option>2014</option>
						<option>2015</option>
					</select>
				</div>
				<div class="date-to">
					<span>to</span>
					<select name="month">
						<option value="1">Jan</option>
						<option value="2">Feb</option>
						<option value="3">Mar</option>
						<option value="4">Apr</option>
						<option value="5">May</option>
						<option value="6">Jun</option>
						<option value="7">Jul</option>
						<option value="8">Aug</option>
						<option value="9">Sep</option>
						<option value="10">Oct</option>
						<option value="11">Nov</option>
						<option value="12">Dec</option>
					</select>
					<select name="year">
						<option>2010</option>
						<option>2011</option>
						<option>2012</option>
						<option>2013</option>
						<option>2014</option>
						<option>2015</option>
					</select>
				</div>
			</div>
		</c:if>
		<div class="content-update">
			<button class="update">Update</button>
			<button class="reset">Reset</button>
		</div>
		<script>
			initInteractions($(".date-select"), commonInterval, $(".filter-select"), '${pageContext.request.contextPath}');
		</script>
		<div>
			<%-- iterate over all views that are charts --%>
			<c:forEach var="view" items="${report.views}">
				<c:if test="${view.type == 'CHART'}">
					<%-- build a wrapper element in which to display the chart --%>
					<div class="wrapper" data-view-id="${view.id}"
							data-use-date-interval="${view.predefinedDateInterval == commonInterval}">
						<span class="title">${view.name}</span>
						<div class="content chart" id="chart-${view.id}"></div>
					</div>
				</c:if>
			</c:forEach>
		</div>
	</section>
</body>
</html>