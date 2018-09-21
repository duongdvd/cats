<%@ page import="jp.co.willwave.aca.utilities.SessionUtil" %>
<c:set var="userLoginInfo" value="<%=SessionUtil.getLoginUser()%>" ></c:set>
<sec:authentication var="principal" property="principal"/>
<%@include file="/resources/layout/headerResource.jsp" %>
<body class="hold-transition skin-blue sidebar-mini">
    <div class="wrapper">
    <%@include file="/resources/layout/topNavigation.jsp" %>
    <c:choose>
        <c:when test="${userLoginInfo.isSystemAdmin()}">
            <%@include file="/resources/layout/navigation/systemAdminNav.jsp" %>
        </c:when>
        <c:when test="${userLoginInfo.isCompanyDirector() || userLoginInfo.isDivisionDirector()}">
            <%@include file="/resources/layout/navigation/companyAdminNav.jsp" %>
        </c:when>
        <c:when test="${userLoginInfo.isOperator()}">
            <%@include file="/resources/layout/navigation/operatorAdminNav.jsp" %>
        </c:when>
        <c:otherwise>
            <%@include file="/resources/layout/navigation/viewerNav.jsp" %>
        </c:otherwise>
    </c:choose>