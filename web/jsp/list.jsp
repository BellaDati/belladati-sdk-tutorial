<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Step 5 - BellaDati SDK Tutorial</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
	<header>
		<a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/images/logo.png" /></a>
		<a class="logout button" href="${pageContext.request.contextPath}/logout">Logout</a>
	</header>
	<div id="page-content">
		<section>
			<h1>Your Dashboards</h1>
			<c:forEach var="dashboard" items="${dashboards}">
				<c:if test="${dashboard.name != 'Hidden'}">
					<a class="wrapper dashboard"
						href="${pageContext.request.contextPath}/dashboard/${dashboard.id}">
						<span class="title">${dashboard.name}</span> <img
						src="${pageContext.request.contextPath}/dashboard/${dashboard.id}/thumbnail" />
					</a>
				</c:if>
			</c:forEach>
			<h1>Your Reports</h1>
			<c:forEach var="report" items="${reports}">
				<a class="wrapper report"
					href="${pageContext.request.contextPath}/report/${report.id}">
					<span class="title">${report.name}</span> <img
					src="${pageContext.request.contextPath}/report/${report.id}/thumbnail" />
				</a>
			</c:forEach>
		 </section>
	 </div>
</body>
</html>