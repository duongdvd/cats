<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="devices.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
<script>
    var SELECTED_NOTHING = "<spring:message code="exportDaily.warning.select.nothing"/>";
</script>
<script src="${ctx}/resources/js/exportDaily.js"></script>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="exportDaily.title"/></h1>
        </section>

        <section id="content" class="content">
            <p>
                <spring:url value="downloadDailyReport" var="downloadUrl"/>
                <a id="btnExportDailyReport" class="btn btn-warning"
                   title="<spring:message code="exportDaily.Download"/>" data-href="${downloadUrl}">
                    <span class="glyphicon glyphicon-export"></span>
                    <spring:message code="exportDaily.Download"/>
                </a>
            </p>

            <spring:htmlEscape defaultHtmlEscape="true"/>
            <%@ include file="/resources/layout/messageList.jsp" %>
            <%@ include file="/resources/layout/alert.jsp" %>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <s:form method="GET" action="${ctx}/searchExportDaily" modelAttribute="SearchExportDailyForm">
                                <div class="panel panel-default">
                                    <table id="table_data" class="table table-bordered table-hover" sortby="members.id" orderby="desc">
                                        <thead>
                                        <tr>
                                            <th class="text-left">
                                                <input id="check_all_route"  type="checkbox" class="table-select-all" />
                                            </th>
                                            <th id="division_od" class="text-left">
                                                <spring:message code="exportDaily.division"/>
                                            </th>
                                            <th id="actual_date" class="text-left">
                                                <spring:message code="exportDaily.actualDate"/>
                                            </th>
                                            <th id="driver_name" class="text-left">
                                                <spring:message code="exportDaily.DriverName"/>
                                            </th>
                                            <th id="login_Id" class="text-left">
                                                <spring:message code="exportDaily.DeviceLoginID"/>
                                            </th>
                                            <th id="plate_number" class="text-left">
                                                <spring:message code="exportDaily.PlateNumber"/>
                                            </th>
                                            <th id="start_point" class="text-left">
                                                <spring:message code="exportDaily.StartPointName"/>
                                            </th>
                                            <th id="end_point" class="text-left">
                                                <spring:message code="exportDaily.EndPointName"/>
                                            </th>
                                            <th class="text-right td-search-reset" rowspan="2">
                                                <button type="submit" value="" class="btn btn-success tooltips"
                                                        id="apply"
                                                        data-toggle="tooltip"
                                                        title="<spring:message code="exportDaily.Search"/>">
                                                    <span class="glyphicon glyphicon-filter"></span>
                                                    <spring:message code="exportDaily.Search"/>
                                                </button>
                                                <a class="btn btn-default tooltips reset_filter"
                                                   data-toggle="tooltip" title="<spring:message code="exportDaily.Reset"/>">
                                                    <span class="glyphicon glyphicon-refresh"></span>
                                                    <spring:message code="exportDaily.Reset"/>
                                                </a>
                                            </th>
                                        </tr>
                                        <tr class="filter">
                                            <td></td>
                                            <td>
                                                <select name="divisionId" class="form-control input-sm select2" style="width: 100%">
                                                    <c:choose>
                                                        <c:when test="${empty SearchExportDailyForm.divisionId}">
                                                            <option value="" selected><spring:message code="exportDaily.AllDivision"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <option value="${division.divisionId}">${division.divisionName}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message code="exportDaily.AllDivision"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <c:choose>
                                                                        <c:when test="${division.divisionId eq SearchExportDailyForm.divisionId}">
                                                                            <option value="${division.divisionId}"
                                                                                    selected>${division.divisionName}</option>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <option value="${division.divisionId}">${division.divisionName}</option>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </select>
                                            </td>
                                            <td>
                                                <input id="actualDate" type="date"
                                                       class="form-control input-sm form-search-from" data-date-format="yyyy-MM-dd"
                                                       name="actualDate"   value="${SearchExportDailyForm.actualDate}"
                                                       title="<spring:message code="exportDaily.actualDate"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="driverName" value="${SearchExportDailyForm.driverName}"
                                                       id="driverName"
                                                       class="form-control input-sm"
                                                       title="<spring:message code="exportDaily.DriverName"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="loginId" value="${SearchExportDailyForm.loginId}"
                                                       id="loginId"
                                                       class="form-control input-sm"
                                                       title="<spring:message code="exportDaily.DeviceLoginID"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="plateNumber"
                                                       value="${SearchExportDailyForm.plateNumber}"
                                                       id="plateNumber"
                                                       class="form-control input-sm"
                                                       title="<spring:message code="exportDaily.PlateNumber"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="startPoint"
                                                       value="${SearchExportDailyForm.startPoint}"
                                                       id="startPoint"
                                                       class="form-control input-sm"
                                                       title="<spring:message code="exportDaily.StartPointName"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="endPoint"
                                                       value="${SearchExportDailyForm.endPoint}"
                                                       id="endPoint"
                                                       class="form-control input-sm"
                                                       title="<spring:message code="exportDaily.EndPointName"/>">
                                            </td>
                                        </tr>
                                        </thead>

                                        <tbody>
                                            <c:forEach items="${exportList}" var="item">
                                             <tr>
                                                 <td><input type="checkbox" class="table-item-select" data-routeid="${item.routeId}"></td>
                                                 <td>${item.divisionName}</td>
                                             <td><fmt:formatDate value="${item.actualDate}" pattern="yyyy-MM-dd" /></td>
                                                 <td>${item.driverName}</td>
                                                 <td>${item.loginId}</td>
                                                 <td>${item.plateNumber}</td>
                                                 <td>${item.customerName}</td>
                                                 <td>${item.endName}</td>
                                             </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                    <div class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-5">
                                                <div class="dataTables_info"><spring:message code="employee.total"/>: ${count}</div>
                                            </div>
                                            <div class="col-sm-7 text-right">
                                                <tag:paginate max="10" offset="${offset}" count="${count}"
                                                              uri="searchExportDailyPaging"
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
