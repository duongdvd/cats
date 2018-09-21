<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="route.routeList"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <script src="${ctx}/resources/js/routeList.js"></script>
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="route.routeList"/></h1>
            <div id="alertRoute" class="bootstrap-alert"></div>
        </section>

        <section id="content" class="content">
            <p>
                <a class="btn btn-primary tooltips" href="${ctx}/addRouteView" data-toggle="tooltip"
                   title="<spring:message code="route.addNew"/>"><span class="glyphicon glyphicon-plus">
                </span><spring:message code="route.addNew"/></a>
            </p>

            <spring:htmlEscape defaultHtmlEscape="true"/>
            <%@ include file="/resources/layout/messageList.jsp" %>
            <%@ include file="/resources/layout/alert.jsp" %>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <s:form method="GET" action="${ctx}/searchRoute" modelAttribute="SearchRouteForm">
                                <div class="panel panel-default">
                                    <table id="table_data" class="table table-bordered table-hover" sortby="route.id" orderby="desc">
                                        <thead>
                                        <tr>
                                            <th id="id_title" class="text-left">
                                                <spring:message code="route.id"/>
                                            </th>
                                            <th id="device_login_id" class="text-left">
                                                <spring:message code="route.routeName"/>
                                            </th>
                                            <th id="plate_number" class="text-left">
                                                <spring:message code="route.plateNumber"/>
                                            </th>
                                            <th id="driver_name" class="text-left">
                                                <spring:message code="route.startDate"/>
                                            </th>
                                            <th id="end_date" class="text-left">
                                                <spring:message code="route.endDate"/>
                                            </th>
                                            <th id="status" class="text-left">
                                                <spring:message code="route.status"/>
                                            </th>
                                            <th id="route_memo" class="text-left">
                                                <spring:message code="route.memo"/>
                                            </th>
                                            <th id="specific_date" class="text-left">
                                                <spring:message code="route.specificDate"/>
                                            </th>

                                            <th class="text-right td-search-reset" rowspan="2">
                                                <button type="submit" value="" class="btn btn-success tooltips"
                                                        id="apply"
                                                        data-toggle="tooltip" title="<spring:message code="route.search"/>">
                                                    <span class="glyphicon glyphicon-filter"></span>
                                                    <spring:message code="route.search"/>
                                                </button>
                                                <a class="btn btn-default tooltips reset_filter"
                                                   data-toggle="tooltip" title="<spring:message code="route.reset"/>">
                                                    <span class="glyphicon glyphicon-refresh"></span>
                                                    <spring:message code="route.reset"/>
                                                </a>
                                            </th>
                                        </tr>
                                        <tr class="filter">
                                            <td>
                                                <select name="routeId" class="form-control input-sm select2">
                                                    <c:choose>
                                                        <c:when test="${empty SearchRouteForm.routeId}">
                                                            <option value="" selected><spring:message
                                                                code="route.allRoute"/></option>
                                                            <c:if test="${not empty routeIdList}">
                                                                <c:forEach var="routeId" items="${routeIdList}">
                                                                    <option value="${routeId}">${routeId}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                code="route.allRoute"/></option>
                                                            <c:if test="${not empty routeIdList}">
                                                                <c:forEach var="routeId" items="${routeIdList}">
                                                                    <c:choose>
                                                                        <c:when test="${routeId eq SearchRouteForm.routeId}">
                                                                            <option value="${routeId}"
                                                                                    selected>${routeId}</option>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <option value="${routeId}">${routeId}</option>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="routeName"
                                                       value="${SearchRouteForm.routeName}"
                                                       id="routeName"
                                                       class="form-control input-sm" title="<spring:message code="route.routeName"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="plateNumber"
                                                       value="${SearchRouteForm.plateNumber}"
                                                       id="plateNumber"
                                                       class="form-control input-sm"
                                                       title="<spring:message code="route.plateNumber"/>">
                                            </td>
                                            <td>
                                                <div class="input-group input-daterange">
                                                    <input id="fromStartDate" type="date"
                                                           class="form-control input-sm form-search-from" data-date-format="yyyy-MM-dd"
                                                           name="fromStartDate"   value="${SearchRouteForm.fromStartDate}"
                                                           title="<spring:message code="route.fromStartDate"/>">
                                                    <span class="input-group-addon form-search-middle">~</span>
                                                    <input id="toStartDate" type="date"
                                                           class="form-control input-sm form-search-from" data-date-format="yyyy-MM-dd"
                                                           name="toStartDate"   value="${SearchRouteForm.toStartDate}"
                                                           title="<spring:message code="route.toStartDate"/>">
                                                </div>
                                            </td>
                                            <td>
                                                <div class="input-group input-daterange">
                                                    <input id="fromEndDate" type="date"
                                                           class="form-control input-sm form-search-to"
                                                           name="fromEndDate" title="<spring:message code="route.fromEndDate"/>"
                                                           value="${SearchRouteForm.fromEndDate}">
                                                    <span class="input-group-addon form-search-middle">~</span>
                                                    <input id="toEndDate" type="date"
                                                           class="form-control input-sm form-search-to"
                                                           name="toEndDate" title="<spring:message code="route.toEndDate"/>"
                                                           value="${SearchRouteForm.toEndDate}">
                                                </div>
                                            </td>
                                            <td>
                                                <select name="status" class="form-control input-sm">
                                                    <c:choose>
                                                        <c:when test="${empty SearchRouteForm.status}">
                                                            <option value="" selected><spring:message
                                                                code="route.allStatus"/></option>
                                                            <option value="1"><spring:message
                                                                code="route.active"/></option>
                                                            <option value="0"><spring:message
                                                                code="route.inActive"/></option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                code="route.allStatus"/></option>
                                                            <c:choose>
                                                                <c:when test="${SearchRouteForm.status == 1}">
                                                                    <option value="1" selected><spring:message
                                                                        code="route.active"/></option>
                                                                    <option value="0"><spring:message
                                                                        code="route.inActive"/></option>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <option value="1"><spring:message
                                                                        code="route.active"/></option>
                                                                    <option value="0" selected><spring:message
                                                                        code="route.inActive"/></option>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="routeMemo"
                                                       value="${SearchRouteForm.routeMemo}"
                                                       id="routeMemo"
                                                       class="form-control input-sm" title="<spring:message code="route.memo"/>">
                                            </td>
                                            <td>
                                                <input id="specificDate" type="date"
                                                       class="form-control input-sm"
                                                       name="specificDate" title="<spring:message code="route.specificDate"/>" data-date-format="yyyy-MM-dd"
                                                       value="${SearchRouteForm.specificDate}" />
                                            </td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:if test="${not empty routeDTOList}">
                                            <c:forEach var="route" items="${routeDTOList}">
                                                <tr>
                                                    <td class="word-wrap"><c:out value="${route.routeId}"/></td>
                                                    <td class="word-wrap"><c:out value="${route.routeName}"/></td>
                                                    <td class="word-wrap"><c:out value="${route.plateNumber}"/></td>
                                                    <td class="word-wrap"><fmt:formatDate value="${route.startDate}" pattern="yyyy-MM-dd" /></td>
                                                    <td class="word-wrap"><fmt:formatDate value="${route.endDate}" pattern="yyyy-MM-dd" /></td>
                                                    <td class="word-wrap">
                                                        <c:choose>
                                                            <c:when test="${route.status == 1}">
                                                                <button type="button" class="btn btn-success" <c:if test="${route.changeStatusPermission eq false}" >disabled</c:if>
                                                                        onclick="changeStatusRoute(${route.routeId});">
                                                                    <spring:message code="route.active"/>
                                                                </button>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="button" class="btn btn-danger" <c:if test="${route.changeStatusPermission eq false}" >disabled</c:if>
                                                                        onclick="changeStatusRoute(${route.routeId});">
                                                                    <spring:message code="route.inActive"/>
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="word-wrap"><c:out value="${route.routeMemo}"/></td>
                                                    <td class="word-wrap"></td>
                                                    <td class="text-right">

                                                        <span class="">
                                                                    <spring:url value="cloneRouteView?routeId=${route.routeId}"
                                                                                var="cloneUrl"/>
                                                                     <a class="btn btn-info submit regist"
                                                                        href="${cloneUrl}">
                                                                            <span class="fa fa-clone"></span>
                                                                         <spring:message code="route.cloneUrl"/>
                                                                     </a>
                                                                </span>
                                                        <span class="">
                                                                    <spring:url value="editRouteView?routeId=${route.routeId}"
                                                                                var="editUrl"/>
                                                                     <a class="btn btn-warning submit regist"
                                                                        href="${editUrl}">
                                                                            <span class="glyphicon glyphicon-pencil"></span>
                                                                         <spring:message code="route.edit"/>
                                                                     </a>
                                                                </span>
                                                        <span class="">
                                                                    <spring:url value="deleteRoute?id=${route.routeId}"
                                                                                var="deleteUrl"/>
                                                                    <a class="btn bg-red-active delete <c:if test="${route.deletePermission eq false}">disabled</c:if>"
                                                                       href="${deleteUrl}">
                                                                            <span class="glyphicon glyphicon-trash"></span>
                                                                        <spring:message code="route.delete"/>
                                                                    </a>
                                                                    <%--<button type="submit" id="delete" name="delete"--%>
                                                                        <%--class="btn btn-info btn-sm delete"--%>
                                                                        <%--value="${device.deviceId}" data-action="${deleteUrl}">Delete</button>--%>
                                                        </span>


                                                    </td>
                                                </tr>
                                            </c:forEach>

                                        </c:if>
                                        </tbody>
                                    </table>

                                    <div class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-5">
                                                <div class="dataTables_info"><spring:message code="route.total"/>: ${count}</div>
                                            </div>
                                            <div class="col-sm-7 text-right">
                                                <tag:paginate max="10" offset="${offset}" count="${count}"
                                                              uri="searchRoutePaging"
                                                              next="&raquo;"
                                                              previous="&laquo;" steps="10"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </s:form>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
        </section>
        <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>
