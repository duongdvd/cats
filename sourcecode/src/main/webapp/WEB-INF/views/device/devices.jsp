<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="devices.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <script src="${ctx}/resources/js/device.js"></script>
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="devices.deviceList"/></h1>
            <div id="alertDevice" class="bootstrap-alert"></div>
        </section>

        <section id="content" class="content">
            <p>
                <spring:url value="addDevice" var="addUrl"/>
                <a class="btn btn-primary tooltips" href="${addUrl}" data-toggle="tooltip"
                   title="<spring:message code="employee.addNew"/>"><span class="glyphicon glyphicon-plus"></span>
                    <spring:message code="employee.addNew"/></a>
                <a href="${ctx}/exportDeviceList"  name="export" class="btn btn-warning">
                    <span class="glyphicon glyphicon-export"></span>
                    <spring:message code="customer.btn.export"/>
                </a>
                <a href="#" id="import" name="import" class="btn btn-info" onclick="performClick('fileImport');">
                    <span class="glyphicon glyphicon-import"></span>
                    <spring:message code="customer.btn.import"/>
                </a>
            </p>
            <div class="uploadForm col-lg-3" style="display: none">
                <form action="${ctx}/importDeviceList" method="post" enctype="multipart/form-data">
                    <input type="file" id="fileImport" name="fileInput" style="display: none" />
                    <span class="filePath"></span>
                    <input type="submit" value="<spring:message code="customer.btn.upload"/>" class="btn btn-primary glyphicon glyphicon-upload btn-submit"/>
                </form>
            </div>


            <spring:htmlEscape defaultHtmlEscape="true"/>
            <%@ include file="/resources/layout/messageList.jsp" %>
            <%@ include file="/resources/layout/alert.jsp" %>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <s:form method="GET" action="${ctx}/searchDevices" modelAttribute="searchDeviceForm">
                                <div class="panel panel-default">
                                    <table id="table_data" class="table table-bordered table-hover" sortby="members.id" orderby="desc">
                                        <thead>
                                        <tr>
                                            <th id="id_title" class="text-left">
                                                <spring:message code="devices.id"/>
                                            </th>
                                            <th id="device_login_id" class="text-left">
                                                <spring:message code="devices.loginId"/>
                                            </th>
                                            <th id="division_name" class="text-left">
                                                <spring:message code="devices.divisionName"/>
                                            </th>
                                            <th id="driver_name" class="text-left">
                                                <spring:message code="devices.driverName"/>
                                            </th>
                                            <th id="plate_number" class="text-left">
                                                <spring:message code="devices.plateName"/>
                                            </th>
                                            <th id="status" class="text-left">
                                                <spring:message code="devices.status"/>
                                            </th>
                                            <th class="text-right td-search-reset" rowspan="2">
                                                <button type="submit" value="" class="btn btn-success tooltips"
                                                        id="apply"
                                                        data-toggle="tooltip" title="<spring:message code="devices.search"/>">
                                                    <span class="glyphicon glyphicon-filter"></span>
                                                    <spring:message code="devices.search"/>
                                                </button>
                                                <a class="btn btn-default tooltips reset_filter"
                                                   data-toggle="tooltip" title="<spring:message code="devices.reset"/>">
                                                    <span class="glyphicon glyphicon-refresh"></span>
                                                    <spring:message code="devices.reset"/>
                                                </a>
                                            </th>
                                        </tr>
                                        <tr class="filter">
                                            <td>
                                                <select name="deviceId" class="form-control input-sm select2" style="width: 100%">
                                                    <c:choose>
                                                        <c:when test="${empty searchDeviceForm.deviceId}">
                                                            <option value="" selected><spring:message
                                                                    code="devices.allDevice"/></option>
                                                            <c:if test="${not empty deviceIdList}">
                                                                <c:forEach var="device" items="${deviceIdList}">
                                                                    <option value="${device}">${device}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                    code="devices.allDevice"/></option>
                                                            <c:if test="${not empty deviceIdList}">
                                                                <c:forEach var="device" items="${deviceIdList}">
                                                                    <c:choose>
                                                                        <c:when test="${device eq searchDeviceForm.deviceId}">
                                                                            <option value="${device}"
                                                                                    selected>${device}</option>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <option value="${device}">${device}</option>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="deviceLoginId"
                                                       value="${searchDeviceForm.deviceLoginId}"
                                                       id="deviceLoginId"
                                                       class="form-control input-sm" title="<spring:message code="devices.divisionName"/>">
                                            </td>
                                            <td>
                                                <select name="divisionId" class="form-control input-sm select2" style="width: 100%">
                                                    <c:choose>
                                                        <c:when test="${empty searchDeviceForm.divisionId}">
                                                            <option value="" selected><spring:message
                                                                    code="devices.allDivision"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <option value="${division.divisionId}">${division.divisionName}</option>
                                                                </c:forEach>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                    code="devices.allDevice"/></option>
                                                            <c:if test="${not empty divisionIdList}">
                                                                <c:forEach var="division" items="${divisionIdList}">
                                                                    <c:choose>
                                                                        <c:when test="${division.divisionId eq searchDeviceForm.divisionId}">
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
                                                <input type="text" name="driverName" value="${searchDeviceForm.driverName}"
                                                       id="driverName"
                                                       class="form-control input-sm" title="<spring:message code="devices.driverName"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="plateNumber"
                                                       value="${searchDeviceForm.plateNumber}"
                                                       id="plateNumber"
                                                       class="form-control input-sm" title="<spring:message code="devices.plateName"/>">
                                            </td>
                                            <td>
                                                <select name="status" class="form-control input-sm">
                                                    <c:choose>
                                                        <c:when test="${empty searchDeviceForm.status}">
                                                            <option value="" selected><spring:message
                                                                    code="devices.allStatus"/></option>
                                                            <option value="1"><spring:message
                                                                    code="employee.active"/></option>
                                                            <option value="0"><spring:message
                                                                    code="employee.inActive"/></option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value=""><spring:message
                                                                    code="devices.allStatus"/></option>
                                                            <c:choose>
                                                                <c:when test="${searchDeviceForm.status == 1}">
                                                                    <option value="1" selected><spring:message
                                                                            code="employee.active"/></option>
                                                                    <option value="0"><spring:message
                                                                            code="employee.inActive"/></option>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <option value="1"><spring:message
                                                                            code="employee.active"/></option>
                                                                    <option value="0" selected><spring:message
                                                                            code="employee.inActive"/></option>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </select>
                                            </td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:if test="${not empty deviceList}">
                                            <!-- Modal -->
                                            <div id="dialogResetPassword" class="modal fade text-left"
                                                 role="dialog">
                                                <div class="modal-dialog">

                                                    <!-- Modal content-->
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <button type="button" class="close"
                                                                    data-dismiss="modal">&times;
                                                            </button>
                                                            <h4 class="modal-title"><spring:message code="devices.header.resetPasswordCfm"/></h4>
                                                        </div>
                                                        <div class="modal-body" style="padding: 10px !important;">
                                                            <div id="alertMessage"
                                                                 class="bootstrap-alert"></div>
                                                            <input type="text"
                                                                   placeholder="<spring:message code="devices.header.viewPassword"/>"
                                                                   class="form-control"
                                                                   name="password">
                                                            <input type="text" hidden
                                                                   name="devicesId">
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button"
                                                                    onclick="resetPassword();"
                                                                    class="btn btn-default">
                                                                <spring:message code="devices.header.resetPassword"/>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <c:forEach var="device" items="${deviceList}">
                                                <tr>
                                                    <td><c:out value="${device.deviceId}"/></td>
                                                    <td><c:out value="${device.deviceLoginId}"/></td>
                                                    <td><c:out value="${device.divisionName}"/></td>
                                                    <td><c:out value="${device.driverName}"/></td>
                                                    <td><c:out value="${device.plateNumber}"/></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${device.status == 1}">
                                                                <button type="button" class="btn btn-success"
                                                                        <c:if test="${device.editPermission eq false}">
                                                                            disabled
                                                                        </c:if>
                                                                        onclick="changeStatusDevice(${device.deviceId});">
                                                                    <spring:message code="employee.active"/>
                                                                </button>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="button" class="btn btn-danger"
                                                                        <c:if test="${device.editPermission eq false}">
                                                                            disabled
                                                                        </c:if>
                                                                        onclick="changeStatusDevice(${device.deviceId});">
                                                                    <spring:message code="employee.inActive"/>
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-right">
                                                        <c:choose>
                                                            <c:when test="${device.editPermission eq true}">
                                                                <button type="button" class="btn btn-info"
                                                                        onclick="generatePassword(${device.deviceId});"
                                                                        data-toggle="modal"
                                                                        data-target="#dialogResetPassword">
                                                                    <spring:message code="devices.resetPassword"/>
                                                                </button>
                                                                <span class="">
                                                                    <spring:url value="/editDevice?id=${device.deviceId}" var="editUrl"/>
                                                                     <a class="btn btn-warning submit regist" href="${editUrl}">
                                                                            <span class="glyphicon glyphicon-pencil"></span>
                                                                         <spring:message code="devices.edit"/>
                                                                     </a>
                                                                </span>
                                                                <span class="">
                                                                    <spring:url value="/deleteDevice?id=${device.deviceId}" var="deleteUrl"/>
                                                                    <a class="btn bg-red-active delete" href="${deleteUrl}">
                                                                            <span class="glyphicon glyphicon-trash"></span>
                                                                        <spring:message code="employee.delete"/>
                                                                    </a>
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise><span class=""></span><span class=""></span></c:otherwise>
                                                        </c:choose>

                                                    </td>
                                                </tr>
                                            </c:forEach>

                                        </c:if>
                                        </tbody>
                                    </table>

                                    <div class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-5">
                                                <div class="dataTables_info"><spring:message code="employee.total"/>: ${count}</div>
                                            </div>
                                            <div class="col-sm-7 text-right">
                                                <tag:paginate max="10" offset="${offset}" count="${count}"
                                                              uri="searchPaging"
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
<script>
    function performClick(elemId) {
        var elem = document.getElementById(elemId);
        if (elem && document.createEvent) {
            var evt = document.createEvent("MouseEvents");
            evt.initEvent("click", true, false);
            elem.dispatchEvent(evt);
        }
    }

    $( document ).ready(function() {
        document.getElementById('fileImport').onchange = function () {
            $(".filePath").text(this.value);
            $(".uploadForm").show();
        };
    });
</script>
<%@include file="/resources/layout/footer.jsp" %>