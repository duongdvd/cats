<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" uri="http://www.springframework.org/tags" %>
<ul class="tabrow">
	<li class="tab-menu tab-view">
		<a href="<c:url value="/view"/>"><t:message code="view.tabTitle"/></a>
	</li>
	<li class="tab-menu tab-instruction">
		<a href="<c:url value="/instruction"/>"><t:message code="instruction.tabTitle"/></a>
	</li>
	<li class="tab-menu tab-master tab-clients">
		<a href="<c:url value="/clients"/>"><t:message code="clients.tabTitle"/></a>
	</li>
	<li class="tab-menu tab-master tab-cars">
		<a href="<c:url value="/cars"/>"><t:message code="cars.tabTitle"/></a>
	</li>
</ul>
