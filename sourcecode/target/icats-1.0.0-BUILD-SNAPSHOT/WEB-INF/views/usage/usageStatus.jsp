<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="companyUsergeStatus.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="companyUsergeStatus.title"/></h1>
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
                                <h3 class="box-title"><spring:message code="companyUsergeStatus.title"/></h3>
                            </div>
                        </div>
                        <div class="box-body filter">
                            <s:form method="GET" action="${ctx}/searchUsageStatus" class="form-horizontal" modelAttribute="SearchUsageForm">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="companyId" class="col-sm-4 control-label"><spring:message code="companyUsergeStatus.Id"/></label>
                                        <div class="col-sm-8">
                                            <select name="companyId" id="companyId" class="form-control select2" style="width: 100%">
                                                <c:choose>
                                                    <c:when test="${empty SearchUsageForm.companyId}">
                                                        <option value="" selected><spring:message code="companyUsergeStatus.AllCompany"/></option>
                                                        <c:if test="${not empty companyIdList}">
                                                            <c:forEach var="company" items="${companyIdList}">
                                                                <option value="${company.divisionId}">${company.divisionName}</option>
                                                            </c:forEach>
                                                        </c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value=""><spring:message
                                                                code="devices.allDevice"/></option>
                                                        <c:if test="${not empty companyIdList}">
                                                            <c:forEach var="company" items="${companyIdList}">
                                                                <c:choose>
                                                                    <c:when test="${company.divisionId eq SearchUsageForm.companyId}">
                                                                        <option value="${company.divisionId}"
                                                                                selected>${company.divisionName}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${company.divisionId}">${company.divisionName}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </c:if>
                                                    </c:otherwise>
                                                </c:choose>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label"><spring:message code="companyUsergeStatus.CompanyName"/></label>
                                        <div class="col-sm-8">
                                            <input type="text" name="companyName" value="${SearchUsageForm.companyName}"
                                                   placeholder="<spring:message code="companyUsergeStatus.CompanyName"/>"
                                                   class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label"><spring:message code="companyUsergeStatus.ReportType"/></label>
                                        <div class="col-sm-8">
                                            <select name="reportType" id="reportType" class="form-control">
                                                <c:choose>
                                                    <c:when test="${empty SearchUsageForm.reportType}">
                                                        <option value="REALTIME" selected><spring:message code="companyUsergeStatus.RealTime"/></option>
                                                        <option value="MONTHLY"><spring:message code="companyUsergeStatus.Monthly"/></option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${'REALTIME' eq SearchUsageForm.reportType}">
                                                                <option value="REALTIME" selected><spring:message code="companyUsergeStatus.RealTime"/></option>
                                                                <option value="MONTHLY"><spring:message code="companyUsergeStatus.Monthly"/></option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="REALTIME" ><spring:message code="companyUsergeStatus.RealTime"/></option>
                                                                <option value="MONTHLY" selected><spring:message code="companyUsergeStatus.Monthly"/></option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="reportMonth" class="col-sm-4 control-label"><spring:message code="companyUsergeStatus.ReportMonth"/></label>
                                        <div class="col-sm-8">
                                            <input id="reportMonth" type="month"
                                                   class="form-control form-search-to"
                                                   name="reportMonth"
                                                   value="${SearchUsageForm.reportMonth}">
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

                        <div class="box-body">
                            <s:form>
                                <table id="table_data" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th id="company_id" class="text-left">
                                            <spring:message code="companyUsergeStatus.companyId"/>
                                        </th>
                                        <th id="company_name" class="text-left">
                                            <spring:message code="companyUsergeStatus.CompanyName"/>
                                        </th>
                                        <th id="division_number" class="text-left">
                                            <spring:message code="companyUsergeStatus.DivisionNumber"/>
                                        </th>
                                        <th id="user_number" class="text-left">
                                            <spring:message code="companyUsergeStatus.UserNumber"/>
                                        </th>
                                        <th id="device_number" class="text-left">
                                            <spring:message code="companyUsergeStatus.DeviceNumber"/>
                                        </th>
                                        <th id="month_report" class="text-left">
                                            <spring:message code="companyUsergeStatus.MonthReport"/>
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="company" items="${companyUsageDTOList}">
                                        <tr>
                                            <td>${company.companyId}</td>
                                            <td>${company.companyName}</td>
                                            <td>${company.divisionActiveCount}</td>
                                            <td>${company.userActiveCount}</td>
                                            <td>${company.deviceActiveCount}</td>
                                            <td><fmt:formatDate value="${company.monthReport}" pattern="yyyy-MM" /></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                                <div class="panel-footer">
                                    <div class="row">
                                        <div class="col-sm-5">
                                            <div class="dataTables_info"><spring:message code="companyUsergeStatus.total"/>${count}</div>
                                        </div>
                                        <div class="col-sm-7 text-right">
                                            <tag:paginate max="10" offset="${offset}" count="${count}"
                                                          uri="searchUsageStatusPaging"
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
