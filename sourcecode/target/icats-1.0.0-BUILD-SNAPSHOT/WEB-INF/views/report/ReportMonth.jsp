<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="Report.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
<script>
    var SELECTED_NOTHING = "<spring:message code="exportDaily.warning.select.nothing"/>";
</script>
<script src="${ctx}/resources/js/exportMonthly.js"></script>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="Report.title"/></h1>
        </section>

        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/>
                <%@ include file="/resources/layout/messageList.jsp" %>
                <%@ include file="/resources/layout/alert.jsp" %>
            </p>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box box-info">
                        <div class="box-header with-border">
                            <div class="col-md-6">
                                <h3 class="box-title"><spring:message code="Report.infor"/></h3>
                            </div>
                        </div>
                        <div class="box-body filter">
                            <s:form method="GET" action="${ctx}/searchReportMonth" class="form-horizontal"
                                    modelAttribute="searchReportForm">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="month" class="col-sm-3 control-label"><spring:message code="Report.date"/></label>
                                        <div class="col-sm-9">
                                            <input id="month" type="month"
                                                   class="form-control form-search-to"
                                                   name="month"
                                                   value="${searchReportForm.month}">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="division" class="col-sm-3 control-label"><spring:message code="Report.divisionCompany"/></label>
                                        <div class="col-sm-9">
                                            <select name="divisionId" id="division" class="form-control select2" style="width: 100%">
                                                <c:forEach var="division" items="${divisionList}">
                                                    <c:if test="${division.id == searchReportForm.divisionId}">
                                                        <option value="${division.id}"
                                                                selected>${division.divisionName}</option>
                                                    </c:if>
                                                    <c:if test="${division.id != searchReportForm.divisionId}">
                                                        <option value="${division.id}">${division.divisionName}</option>
                                                    </c:if>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label"><spring:message code="Report.driverName"/></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="driverName" value="${searchReportForm.driverName}"
                                                   placeholder="<spring:message code="Report.driverName"/>"
                                                   class="form-control">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label"><spring:message code="Report.plateCar"/></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="plateNumber"
                                                   value="${searchReportForm.plateNumber}"
                                                   class="form-control"
                                                   placeholder="<spring:message code="Report.plateCar"/>">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 col-md-offset-6 text-right">
                                    <button type="submit" value="" class="btn btn-success tooltips"
                                            id="apply"
                                            data-toggle="tooltip" title="<spring:message code="Report.Search"/>">
                                        <span class="glyphicon glyphicon-filter"></span>
                                        <spring:message code="Report.Search"/>
                                    </button>
                                    <a class="btn btn-default tooltips reset_filter"
                                       data-toggle="tooltip" title="<spring:message code="Report.Reset"/>">
                                        <span class="glyphicon glyphicon-refresh"></span>
                                        <spring:message code="Report.Reset"/>
                                    </a>
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

            <div class="row">
                <div class="col-md-12">
                    <div class="box box-info">
                        <div class="box-header with-border">
                            <div class="col-md-9">
                                <h3 class="box-title"><spring:message code="Report.listRouteActual"/></h3>
                            </div>
                            <div class="text-right">
                                <a id="btnExportMonthReport" class="btn btn-warning tooltips"
                                   data-href="${ctx}/downloadMonthReport"
                                   data-toggle="tooltip" title="<spring:message code="Report.export"/>">
                                    <span class="glyphicon glyphicon-export"></span>
                                    <spring:message code="Report.export"/>
                                </a>
                            </div>
                        </div>
                        <div class="box-body">
                            <s:form>
                                <table id="table_data" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th class="text-left">
                                            <input id="check_all_route"  type="checkbox" class="table-select-all" />
                                        </th>
                                        <th id="carName" class="text-left">
                                            <spring:message code="Report.driverName"/>
                                        </th>
                                        <th id="plateNumber" class="text-left">
                                            <spring:message code="Report.plateName"/>
                                        </th>
                                        <th id="loginId" class="text-left">
                                            <spring:message code="Report.loginId"/>
                                        </th>
                                        <th id="distance" class="text-left">
                                            <spring:message code="Report.totalKm"/>
                                        </th>
                                        <th id="detail" class="text-left">
                                            <spring:message code="Report.detail"/>
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="route" items="${routesReport}">
                                        <tr>
                                            <td><input type="checkbox" class="table-item-select" data-routeid="${route.routeId}"></td>
                                            <td>${route.driverName}</td>
                                            <td>${route.plateNumber}</td>
                                            <td>${route.loginId}</td>
                                            <td>${route.distance} (km)</td>
                                            <td>
                                                    ${route.actualRouteName} <br/>
                                                    ${route.formatVisitedPlaces()}
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                                <div class="panel-footer">
                                    <div class="row">
                                        <div class="col-sm-5">
                                            <div class="dataTables_info"><spring:message code="Report.Total"/>: ${count}</div>
                                        </div>
                                        <div class="col-sm-7 text-right">
                                            <tag:paginate max="10" offset="${offset}" count="${count}"
                                                          uri="searchReportMonthPaging"
                                                          next="&raquo;"
                                                          previous="&laquo;" steps="10"/>
                                        </div>
                                    </div>
                                </div>
                            </s:form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
<script>
    $('input[type=month]').datepicker(
        {
            format: "yyyy-mm",
            viewMode: "months",
            minViewMode: "months"
        }
    );
</script>
<%@include file="/resources/layout/footer.jsp" %>
