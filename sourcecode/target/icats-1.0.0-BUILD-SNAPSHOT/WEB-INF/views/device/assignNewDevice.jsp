<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="assignDevice.title.list"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="assignNewDevice.title.list"/></h1>
        </section>
        <!-- Main content -->
        <section id="content" class="content">
            <p>
                <spring:url value="assignAllDevice" var="assignAllUrl"/>
                <a class="btn btn-primary tooltips" href="${assignAllUrl}"  data-toggle="tooltip" title="<spring:message code="assignDevice.title.assignAllDevice"/>"
                   onclick="return confirm('<spring:message code="assignDevice.title.questionConfirm"/>');">
                    <span class="glyphicon glyphicon-plus"></span>
                    <spring:message code="assignDevice.title.assignAllDevice"/>
                </a>

                <spring:url value="assignDeviceList?id=${assignUserId}" var="assignList"/>
                <a class="btn btn-default tooltips" href="${assignList}"  data-toggle="tooltip" title="<spring:message code="assignDevice.title.cancel"/>">
                    <spring:message code="assignDevice.title.cancel"/>
                </a>
            </p>
            <spring:htmlEscape defaultHtmlEscape="true" />
            <%@ include file="/resources/layout/messageList.jsp" %>
            <%@ include file="/resources/layout/alert.jsp" %>
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <s:form method="GET" action="${ctx}/searchAssignNewDevice" modelAttribute="searchDeviceForm">
                                <div class="panel panel-default">
                                    <table id="table_data" class="table table-bordered table-hover" sortby="members.id" orderby="desc">
                                        <thead>
                                        <tr>
                                            <th id="id_title" class="text-left">
                                                <spring:message code="assignDevice.title.id"/>
                                            </th>
                                            <th id="device_login_id" class="text-left">
                                                <spring:message code="assignDevice.title.deviceLoginid"/>
                                            </th>
                                            <th id="division_name" class="text-left">
                                                <spring:message code="assignDevice.title.divisionName"/>
                                            </th>
                                            <th id="driver_name" class="text-left">
                                                <spring:message code="assignDevice.title.driverName"/>
                                            </th>
                                            <th id="plate_number" class="text-left">
                                                <spring:message code="assignDevice.title.plateNumber"/>
                                            </th>
                                            <th class="text-right td-search-reset" rowspan="2">
                                                <button type="submit" value="" class="btn btn-success tooltips"
                                                        id="apply"
                                                        data-toggle="tooltip" title="<spring:message code="assignDevice.title.Search"/>">
                                                    <span class="glyphicon glyphicon-filter"></span><spring:message code="assignDevice.title.Search"/>
                                                </button>
                                                <a class="btn btn-default tooltips reset_filter"
                                                   data-toggle="tooltip" title="<spring:message code="assignDevice.title.Reset"/>">
                                                    <span class="glyphicon glyphicon-refresh"></span><spring:message code="assignDevice.title.Reset"/>
                                                </a>
                                            </th>
                                        </tr>
                                        <tr class="filter">
                                            <td>
                                                <select name="deviceId" class="form-control input-sm select2">
                                                    <c:choose>
                                                        <c:when  test="${empty searchDeviceForm.deviceId}">
                                                            <option value="" selected><spring:message
                                                                code="assignDevice.title.allDevice"/></option>
                                                            <c:if test="${not empty deviceIdList}">
                                                                <c:forEach var="device" items="${deviceIdList}">
                                                                    <option value="${device}">${device}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                code="assignDevice.title.allDevice"/></option>
                                                            <c:if test="${not empty deviceIdList}">
                                                                <c:forEach var="device" items="${deviceIdList}">
                                                                    <c:choose>
                                                                        <c:when test = "${device eq searchDeviceForm.deviceId}">
                                                                            <option value="${device}" selected>${device}</option>
                                                                        </c:when>
                                                                        <c:otherwise><option value="${device}">${device}</option></c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="deviceLoginId" value="${searchDeviceForm.deviceLoginId}" id="deviceLoginId"
                                                       class="form-control input-sm" title="<spring:message code="assignDevice.title.divisionName"/>">
                                            </td>
                                            <td>
                                                <select name="divisionId" class="form-control input-sm select2">
                                                    <c:choose>
                                                        <c:when  test="${empty searchDeviceForm.divisionId}">
                                                            <option value="" selected><spring:message code="assignDevice.title.allDivision"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <option value="${division.divisionId}">${division.divisionName}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                code="assignDevice.title.allDevice"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <c:choose>
                                                                        <c:when test = "${division.divisionId eq searchDeviceForm.divisionId}">
                                                                            <option value="${division.divisionId}" selected>${division.divisionName}</option>
                                                                        </c:when>
                                                                        <c:otherwise><option value="${division.divisionId}">${division.divisionName}</option></c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>


                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="driverName" value="${searchDeviceForm.driverName}" id="driverName"
                                                       class="form-control input-sm" title="<spring:message code="assignDevice.title.driverName"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="plateNumber" value="${searchDeviceForm.plateNumber}" id="plateNumber"
                                                       class="form-control input-sm" title="<spring:message code="assignDevice.title.plateNumber"/>">
                                            </td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:if test="${not empty deviceList}">
                                            <c:forEach var="device" items="${deviceList}">
                                                <tr>
                                                    <td class="text-center"><c:out value="${device.deviceId}"/></td>
                                                    <td class="text-center"><c:out value="${device.deviceLoginId}"/></td>
                                                    <td class="text-center"><c:out value="${device.divisionName}"/></td>
                                                    <td class="text-center"><c:out value="${device.driverName}"/></td>
                                                    <td class="text-center"><c:out value="${device.plateNumber}"/></td>
                                                    <td class="text-right">
                                         <span class="">
                                        <spring:url value="assignDevice?id=${device.deviceId}" var="assignDevice"/>
                                             <a class="btn btn-primary" href="${assignDevice}" >
                                             <span class="glyphicon glyphicon-plus"></span><spring:message code="assignDevice.title.assign"/></a>
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
                                            <div class="col-md-5 text-left">
                                                <label><spring:message code="assignDevice.title.total"/> ${count} </label>
                                            </div>
                                            <div class="col-md-7 text-right">
                                                <tag:paginate max="10"  offset="${offset}" count="${count}" uri="searchAssignNewDevicePaging" next="&raquo;" previous="&laquo;" steps="10"/>
                                            </div>
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
<%@include file="/resources/layout/footer.jsp" %>
