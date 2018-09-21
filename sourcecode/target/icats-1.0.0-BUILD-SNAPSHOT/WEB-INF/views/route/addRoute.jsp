<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="route.title.addRoute"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>

    <link rel="stylesheet" href="${ctx}/resources/css/addRoute.css">
    <script src="//api.its-mo.com/v3/loader?key=JSZ0fb74a744a15%7CMDoAa&api=zdcmap.js,control.js,search.js,shape.js&enc=UTF8&force=1"
            type="text/javascript"></script>
    <script type="text/javascript" src="${ctx}/resources/js/service.js"></script>
    <script type="text/javascript" src="${ctx}/resources/js/map.js"></script>
    <script type="text/javascript" src="${ctx}/resources/js/route.js"></script>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="route.title.addRoute"/></h1>
        </section>
        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/>
                <%@ include file="/resources/layout/messageList.jsp" %>
                <%@ include file="/resources/layout/alert.jsp" %>
            </p>
            <s:form method="POST" action="${ctx}/addRoute" modelAttribute="addEditRouteForm" class="form-horizontal" name="addEditRouteForm">
                <input type="hidden" id="mapDetails" name="mapDetails" value="">
                <input type="hidden" id="routeDetails" name="routeDetails" value="">
                <input type="hidden" id="mode" name="mode" value="${mode}">
                <input type="hidden" id="maxVisitedOrder" name="maxVisitedOrder" value="${maxVisitedOrder}">
                <input type="hidden" id="id" value="${addEditRouteForm.id}" name="id">
                <input id="devicesId" type="hidden" name="devicesId" value="${addEditRouteForm.devicesId}">
                <input type="hidden" id="updateCarStatusRoute" value="${addEditRouteForm.updateCarStatusRoute}">
                <input type="hidden" id="fromCarStatus" name="fromCarStatus"
                       value="<%= request.getParameter("fromCarStatus") == null ? false : request.getParameter("fromCarStatus")%>">
                <div class="row">
                    <div class="col-xs-6">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <div class="col-xs-6">
                                    <h3 class="box-title"><spring:message code="route.title.inforRoute"/></h3>
                                </div>
                            </div>
                            <div class="box-body">
                                <c:set value="${managedDevices}" var="managedDevices"/>
                                <div id="startGarage" class="form-group">
                                    <label class="col-xs-4 control-label required-field"><spring:message
                                            code="route.add.plateNumber"/></label>
                                    <div class="col-xs-8">
                                        <select class="form-control select2" id="devicesTest" style="width: 100%;">
                                            <option value><spring:message code="route.add.plateNumber" /></option>
                                            <c:if test="${not empty managedDevices}">
                                                <c:forEach var="device" items="${managedDevices}">
                                                    <c:choose>
                                                        <c:when test="${device.deviceId == addEditRouteForm.devicesId}">
                                                            <option value="${device.deviceId}" selected>${device.plateNumber}</option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value="${device.deviceId}">${device.plateNumber}</option>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="routeName" class="col-xs-4 control-label required-field"><spring:message code="route.add.routeName"/></label>
                                    <div class="col-xs-8">
                                        <input id="routeName" type="text" class="form-control" name="name" value="<c:out value="${addEditRouteForm.name}"/>">
                                    </div>
                                </div>
                                <c:set value="${garages}" var="garages"/>
                                <div id="endGarage" class="form-group">
                                    <input id="startGarageId" type="hidden" name="startGarageId" value="${addEditRouteForm.startGarageId}">
                                    <label class="col-xs-4 control-label required-field"><spring:message code="route.startGarage"/></label>
                                    <div class="col-xs-8">
                                        <select class="form-control select2" id="startGarageOption" style="width: 100%;">
                                            <c:if test="${not empty garages}">
                                                <c:forEach var="garage" items="${garages}">
                                                    <c:choose>
                                                        <c:when test="${garage.id eq addEditRouteForm.startGarageId}">
                                                            <option value="${garage.id}/${garage.latitude}/${garage.longitude}" selected>
                                                                ${garage.name}
                                                            </option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value="${garage.id}/${garage.latitude}/${garage.longitude}">
                                                                ${garage.name}
                                                            </option>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-4 control-label required-field"><spring:message code="route.endGarage"/></label>
                                    <div class="col-xs-8">
                                        <input id="endGarageId" type="hidden" name="endGarageId" value="${addEditRouteForm.endGarageId}">
                                        <select class="form-control select2" id="endGarageOption" style="width: 100%;">
                                            <c:if test="${not empty garages}">
                                                <c:forEach var="garage" items="${garages}">
                                                    <c:choose>
                                                        <c:when test="${garage.id eq addEditRouteForm.endGarageId}">
                                                            <option value="${garage.id}/${garage.latitude}/${garage.longitude}" selected>
                                                                    ${garage.name}
                                                            </option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value="${garage.id}/${garage.latitude}/${garage.longitude}">
                                                                    ${garage.name}
                                                            </option>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="routeMemo" class="col-xs-4 control-label"><spring:message
                                            code="route.add.routeMemo"/></label>
                                    <div class="col-xs-8">
                                        <textarea class="form-control" name="routeMemo" id="routeMemo" rows="3">${addEditRouteForm.routeMemo}</textarea>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-xs-4 control-label required-field"><spring:message code="route.add.startDate"/></label>
                                    <div class="col-xs-8">
                                        <input type="date" name="startDate" class="form-control"
                                               value="<fmt:formatDate value="${addEditRouteForm.startDate}" pattern="yyyy-MM-dd" />"
                                               <c:if test="${addEditRouteForm.fromCarStatus == true}">readonly</c:if>>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-xs-4 control-label required-field"><spring:message
                                            code="route.add.endDate"/></label>
                                    <div class="col-xs-8">
                                        <input type="date" class="form-control" name="endDate"
                                               value="<fmt:formatDate value="${addEditRouteForm.endDate}" pattern="yyyy-MM-dd" />">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-xs-4 control-label required-field"><spring:message
                                            code="route.add.status"/></label>
                                    <div class="col-xs-8">
                                        <label class="radio-inline">
                                            <input type="radio" value="true"
                                            <c:if test="${addEditRouteForm.active == true}">
                                                   checked
                                            </c:if>
                                                   name="active"><spring:message
                                                code="route.active"/>
                                        </label>
                                        <label class="radio-inline">
                                            <input type="radio" value="false"
                                            <c:if test="${addEditRouteForm.active == false}">
                                                   checked
                                            </c:if>
                                                   name="active"><spring:message code="route.inActive"/>
                                        </label>
                                    </div>
                                </div>

                            </div>
                            <!-- /.box-body -->
                        </div>
                        <!-- /.box -->
                    </div>
                    <div id="customerList" class="col-xs-6">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title col-xs-9"><spring:message code="route.add.customerList"/></h3>
                                <div class="text-right">
                                    <a class="btn btn-default tooltips reset_filter"
                                        id="btnResetCustomer"
                                        data-filter_form="#customerFilterForm"
                                        data-toggle="tooltip" title="<spring:message code="route.reset"/>">
                                        <span class="glyphicon glyphicon-refresh"></span>
                                        <spring:message code="route.reset"/>
                                    </a>
                                </div>
                            </div>
                            <div class="box-body" style="height: 405px">
                                <table id="tableCustomer" class="table table-bordered table-hover table-striped customer_all_list table-responsive"
                                       sortby="customer.id" orderby="desc">
                                    <thead>
                                    <tr>
                                        <th class="customer-name text-left word-wrap">
                                            <spring:message code="route.add.customerName"/>
                                        </th>
                                        <th class="customer-address text-left word-wrap">
                                            <spring:message code="route.add.customerAddress"/>
                                        </th>
                                        <th class="customer-long text-left word-wrap">
                                            <spring:message code="route.add.Longitude"/>
                                        </th>
                                        <th class="customer-lat text-left word-wrap">
                                            <spring:message code="route.add.Latitude"/>
                                        </th>
                                        <th style="width: 50px"></th>
                                    </tr>
                                    <tr id="customerFilterForm" class="filter">
                                        <td class="customer-name">
                                            <input type="text" name="customerName"
                                                   value=""
                                                   class="form-control" placeholder="<spring:message code="route.add.customerName"/>">
                                        </td>
                                        <td class="customer-address">
                                            <input type="text" name="customerAddress"
                                                   value=""
                                                   class="form-control" placeholder="<spring:message code="route.add.customerAddress"/>">
                                        </td>
                                        <td class="customer-long">
                                        </td>
                                        <td class="customer-lat">
                                        </td>
                                        <td style="width: 50px"></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:if test="${not empty customers}">
                                        <c:forEach var="customer" items="${customers}">
                                            <tr class="customer-content customer_${customer.id}" data-customer_id="${customer.id}">
                                                <td class="word-wrap customer-id hidden">${customer.id}</td>
                                                <td class="word-wrap customer-name">${customer.name}</td>
                                                <td class="word-wrap customer-address">${customer.address}</td>
                                                <td class="word-wrap customer-long">${customer.longitude}</td>
                                                <td class="word-wrap customer-lat">${customer.latitude}</td>
                                                <td class="word-wrap customer-building-name">${customer.buildingName}</td>
                                                <td class="word-wrap customer-checkbox text-center" style="width: 44px">
                                                    <label>
                                                        <input type="checkbox">
                                                    </label>
                                                </td>
                                                <td class="customer-visitorder text-center"></td>
                                                <td class="customer-delete text-center">
                                                    <a class="btn-delete-selected-customer btn bg-red-active delete tooltips">
                                                        <span class="glyphicon glyphicon-trash"></span>
                                                        <spring:message code="company.btn.delete"/>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:if>
                                    </tbody>
                                </table>
                            </div>
                            <div class="box-footer col-xs-12 text-center">
                                <a href="#" id="selectCustomer" class="btn btn-primary"><span
                                    class="fa fa-fw fa-long-arrow-down"></span></a>
                            </div>
                            <!-- /.box-body -->
                        </div>
                        <!-- /.box -->
                    </div>
                </div>
                <!-- /.row -->

                <div class="row">
                    <div class="col-xs-6">
                        <div class="box box-info">
                            <div id="mapContainer" style="min-height: 435px">
                                <div style="z-index: 1000; float: right; padding: 5px; position: relative;" class="ext-center">
                                    <button id="btnDisplayRoute" type="button" class="btn btn-warning">
                                        <i class="fa fa-eye fa-lg"></i></button>
                                    <button id="btnReloadMap" type="button" class="btn btn-default" style="margin: 0 2px 0 0;">
                                        <i class="fa fa-refresh fa-lg"></i></button>

                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="selectCustomerList" class="col-xs-6">
                        <div class="box box-info">
                            <div class="box-header with-border">
                                <h3 class="box-title col-xs-6"><spring:message code="route.add.selectCustomer"/></h3>
                                <div class="text-right">
                                    <a class="btn btn-default tooltips reset_filter"
                                       id="btnResetSelectCustomer"
                                       data-filter_form="#routeDetailFilterForm"
                                       data-toggle="tooltip" title="<spring:message code="route.reset"/>">
                                        <span class="glyphicon glyphicon-refresh"></span>
                                        <spring:message code="route.reset"/>
                                    </a>
                                </div>
                            </div>
                            <div class="box-body" style="height: 380px">
                                <table id="tableRouteDetail" class="table table-bordered table-hover customer_all_list" sortby="customer.id" orderby="desc">
                                    <thead>
                                    <tr>
                                        <th class="text-left">
                                            <spring:message code="route.add.customerName"/>
                                        </th>
                                        <th class="text-left">
                                            <spring:message code="route.add.customerAddress"/>
                                        </th>
                                        <th class="text-left">
                                            <spring:message code="route.add.BuildingAddress"/>
                                        </th>
                                        <th class="text-left">
                                            <spring:message code="route.add.visitOrder"/>
                                        </th>
                                        <th class="title-customer-delete"></th>
                                    </tr>
                                    <tr id="routeDetailFilterForm" class="filter">
                                        <td>
                                            <input type="text" name="customerName"
                                                   value=""
                                                   id="customerName"
                                                   class="form-control" title="<spring:message code="route.add.customerName"/>">
                                        </td>
                                        <td>
                                            <input type="text" name="customerAddress"
                                                   value=""
                                                   id="customerAddress"
                                                   class="form-control" title="<spring:message code="route.add.customerAddress"/>">
                                        </td>
                                        <td>
                                            <input type="text" name="customerBuildingName"
                                                   value=""
                                                   id="customerBuildingName"
                                                   class="form-control" title="<spring:message code="route.add.BuildingAddress"/>">
                                        </td>
                                        <td>
                                        </td>
                                        <td style="width: 87px"></td>
                                    </tr>
                                    </thead>
                                    <tbody style="height: 230px">
                                    <c:if test="${not empty routeDetails}">
                                        <c:forEach var="customer" items="${routeDetails}">
                                            <tr class="customer-content customer_${customer.customersId}" data-customer_id="${customer.customersId}">
                                                <td class="word-wrap customer-id hidden">${customer.customersId}</td>
                                                <td class="word-wrap customer-name">${customer.name}</td>
                                                <td class="word-wrap customer-address">${customer.address}</td>
                                                <td class="word-wrap customer-long">${customer.longtitude}</td>
                                                <td class="word-wrap customer-lat">${customer.latitude}</td>
                                                <td class="word-wrap customer-building-name">${customer.buildingName}</td>
                                                <td class="word-wrap customer-checkbox text-center">
                                                    <label><input type="checkbox"></label>
                                                </td>
                                                <td class="customer-visitorder text-center">${customer.visitOrder}</td>
                                                <td class="customer-delete text-center">
                                                    <a class="btn-delete-selected-customer btn bg-red-active delete tooltips">
                                                        <span class="glyphicon glyphicon-trash"></span>
                                                        <spring:message code="company.btn.delete"/>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:if>
                                    </tbody>
                                </table>
                            </div>
                            <!-- /.box-body -->
                        </div>
                        <!-- /.box -->
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->

                <div class="text-right">
                    <spring:url value="routeList" var="routeList"/>
                    <button id="btnAddEditRouteConfirm" class="btn btn-primary" <c:if test="${addEditRouteForm.editPermission eq false}">disabled</c:if> ><spring:message code="devices.create"/></button>
                    <a class="btn btn-default" href="${routeList}" data-toggle="tooltip" title="<spring:message code="customer.btn.cancel"/>">
                        <spring:message code="customer.btn.cancel"/></a>
                </div>
            </s:form>
        </section>
        <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>
