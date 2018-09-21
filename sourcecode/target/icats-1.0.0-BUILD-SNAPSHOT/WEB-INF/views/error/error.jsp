<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="error.title"></c:set>
<%@ include file="/resources/layout/headerResource.jsp"%>

<link rel="stylesheet" type="text/css" href="<c:url value="${ctx}/resources/css/global.css"/>"/>
<style type="text/css">
.messagePanel {
	position: absolute;
	top: 100px;
	left: 100px;
	width: 400px;
	height: 100px;
	border: 1px solid;
	margin: 10px 0px;
	padding: 15px 10px 15px 100px;
	background-repeat: no-repeat;
	background-position: 10px center;
	color: #D8000C;
	background-color: #FFBABA;
	background-image: url("../../../resources/img/error.png");
}
.loginLink {
	position: absolute;
	top: 200px;
	left: 500px;
}
</style>
<body>
<div>
	<span class="messagePanel">
		<c:out value="${message01}"/><br/>
		<c:out value="${message02}"/>
	</span>
	<span class="loginLink">
		<c:url var="loginUrl" value="/login"/>
		<a href="${loginUrl}">[<span><tags:message code="error.login"/></span>]</a>
	</span>
	<div class="header">
		<span id="systemTitle">
			<t:message code="systemTitle.title"/>
		</span>
		<span id="loginInfo">
			<span>
				${version}
			</span>
			<br/>
			<br/>
		</span>
	</div>
<%@ include file="/resources/layout/footer.jsp" %>
