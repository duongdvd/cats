<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="employee.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="employee.title"/></h1>
        </section>

        <section id="content" class="content">
            <c:url var="addUrl" value="/addEmployeeView"/>
            <p>
                <a href="${ctx}/addEmployeeView" class="btn btn-primary tooltips"
                   id="create" title="<spring:message code="employee.addNew"/>"
                   data-toggle="tooltip">
                    <span class="glyphicon glyphicon-plus"></span>
                    <spring:message code="employee.addNew"/>
                </a>
            </p>

            <spring:htmlEscape defaultHtmlEscape="true"/>
            <%@ include file="/resources/layout/messageList.jsp" %>
            <%@ include file="/resources/layout/alert.jsp" %>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <s:form method="GET" action="${ctx}/searchEmployee" modelAttribute="searchEmployeeForm">
                                <div class="panel panel-default">
                                    <table id="table_data" class="table table-bordered table-hover" sortby="members.id" orderby="desc">
                                        <thead>
                                        <tr>
                                            <th id="division_name_title" class="text-left">
                                                <spring:message code="employee.Division"/>
                                            </th>
                                            <th id="id_title" class="text-left" style="width: 50px">
                                                <spring:message code="employee.Id"/>
                                            </th>
                                            <th id="login_title" class="text-left">
                                                <spring:message code="employee.LoginId"/>
                                            </th>
                                            <th id="name_title" class="text-left">
                                                <spring:message code="employee.name"/>
                                            </th>
                                            <th id="role_title" class="text-left">
                                                <spring:message code="employee.role"/>
                                            </th>
                                            <th id="email_title" class="text-left">
                                                <spring:message code="employee.email"/>
                                            </th>
                                            <th id="fixed_phone_title" class="text-left">
                                                <spring:message code="employee.fixedPhone"/>
                                            </th>
                                            <th id="mobile_phone_title" class="text-left">
                                                <spring:message code="employee.mobilePhone"/>
                                            </th>
                                            <th id="status_title" class="text-left">
                                                <spring:message code="employee.status"/>
                                            </th>
                                            <th class="text-right td-search-reset" rowspan="2">
                                                <button type="submit" value="" class="btn btn-success tooltips" id="apply"
                                                        data-toggle="tooltip" title="<spring:message code="employee.search"/>">
                                                    <span class="glyphicon glyphicon-filter"></span>
                                                    <spring:message code="employee.search"/>
                                                </button>
                                                <button type="submit" class="btn btn-default btn-submit tooltips reset_filter"
                                                        data-toggle="tooltip" title="<spring:message code="employee.reset"/>">
                                                    <span class="glyphicon glyphicon-refresh"></span>
                                                    <spring:message code="employee.reset"/>
                                                </button>
                                            </th>
                                        </tr>
                                        <tr class="filter">
                                            <td>
                                                <select name="divisionsId" class="form-control input-sm select2">
                                                    <option value=""><spring:message code="employee.allDivision"/></option>
                                                    <c:forEach var="division" items="${divisionList}">
                                                        <c:if test="${division.id ==
                                            searchEmployeeForm.divisionsId}">
                                                            <option value="${division.id}" selected>${division.name}</option>
                                                        </c:if>
                                                        <c:if test="${empty searchEmployeeForm.divisionsId or division.id !=
                                            searchEmployeeForm.divisionsId}">
                                                            <option value="${division.id}">${division.name}</option>
                                                        </c:if>
                                                    </c:forEach>
                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="id" value="${searchEmployeeForm.id}" id="id"
                                                       class="form-control input-sm" title="<spring:message code="employee.Id"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="loginId" value="${searchEmployeeForm.loginId}" id="loginId"
                                                       class="form-control input-sm" title="<spring:message code="employee.LoginId"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="name" value="${searchEmployeeForm.name}"
                                                       id="name"
                                                       class="form-control input-sm" title="<spring:message code="employee.name"/>">
                                            </td>
                                            <td>
                                                <select name="roleId" class="form-control input-sm">
                                                    <option value=""><spring:message code="employee.allRoles"/></option>
                                                    <c:forEach var="role" items="${roleList}">
                                                        <c:if test="${role.id == searchEmployeeForm.roleId}">
                                                            <option value="${role.id}" selected>${role.name}</option>
                                                        </c:if>
                                                        <c:if test="${empty searchEmployeeForm.roleId or role.id != searchEmployeeForm.roleId}">
                                                            <option value="${role.id}">${role.name}</option>
                                                        </c:if>
                                                    </c:forEach>
                                                </select>
                                            </td>
                                            <td>
                                                <input type="text" name="email" value="${searchEmployeeForm.email}"
                                                       id="email"
                                                       class="form-control input-sm" title="<spring:message code="employee.email"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="fixedPhone" value="${searchEmployeeForm.fixedPhone}"
                                                       id="fixedPhone"
                                                       class="form-control input-sm" title="<spring:message code="employee.fixedPhone"/>">
                                            </td>
                                            <td>
                                                <input type="text" name="mobilePhone" value="${searchEmployeeForm.mobilePhone}"
                                                       id="mobilePhone"
                                                       class="form-control input-sm" title="<spring:message code="employee.mobilePhone"/>">
                                            </td>
                                            <td>
                                                <select name="status" class="form-control input-sm">
                                                    <c:if test="${empty searchEmployeeForm.status}">
                                                        <option selected value=""><spring:message code="employee.allStatus"/></option>
                                                        <option value="1"><spring:message code="employee.active"/></option>
                                                        <option value="0"><spring:message code="employee.inActive"/></option>
                                                    </c:if>
                                                    <c:if test="${searchEmployeeForm.status}">
                                                        <option value=""><spring:message code="employee.allStatus"/></option>
                                                        <option value="1" selected><spring:message code="employee.active"/></option>
                                                        <option value="0"><spring:message code="employee.inActive"/></option>
                                                    </c:if>
                                                    <c:if test="${not empty searchEmployeeForm.status and not searchEmployeeForm.status}">
                                                        <option value=""><spring:message code="employee.allStatus"/></option>
                                                        <option value="1"><spring:message code="employee.active"/></option>
                                                        <option value="0" selected><spring:message code="employee.inActive"/></option>
                                                    </c:if>
                                                </select>
                                            </td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:if test="${not empty employeeList}">
                                            <c:forEach var="employee" items="${employeeList}">
                                                <tr>
                                                    <td class="word-wrap"><c:out value="${employee.divisionName}"/></td>
                                                    <td class="word-wrap"><c:out value="${employee.id}"/></td>
                                                    <td class="word-wrap"><c:out value="${employee.loginId}"/></td>
                                                    <td class="word-wrap"><c:out
                                                            value="${employee.firstName} ${employee.lastName}"/></td>
                                                    <td class="word-wrap"><c:out value="${employee.roleName}"/></td>
                                                    <td class="word-wrap"><c:out value="${employee.userEmail}"/></td>
                                                    <td class="word-wrap"><c:out value="${employee.fixedPhone}"/></td>
                                                    <td class="word-wrap"><c:out value="${employee.mobilePhone}"/></td>
                                                    <td class="word-wrap">
                                                        <c:if test="${employee.status}">
                                                            <a href="${ctx}/change/status/${employee.id}"
                                                                    <c:if test="${employee.roleId == 3}">
                                                                        disabled
                                                                    </c:if>
                                                               class="btn btn-success">
                                                                <spring:message code="employee.active"/>
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${not employee.status}">
                                                            <a href="${ctx}/change/status/${employee.id}"
                                                                    <c:if test="${employee.roleId == 3}">
                                                                        disabled
                                                                    </c:if>
                                                               class="btn btn-danger">
                                                                <spring:message code="employee.inActive"/>
                                                            </a>
                                                        </c:if>
                                                    </td>
                                                    <td class="text-right">
                                                        <c:if test="${employee.roleId == 4}">
                                                            <span class="">
                                                            <a id="assignDeviceList" class="btn btn-info submit regist"
                                                               href="${ctx}/assignDeviceList?id=${employee.id}">
                                                             <span class="glyphicon glyphicon-pushpin"></span>
                                                             <spring:message code="employee.btn.assign"/>
                                                            </a>
                                                        </span>
                                                        </c:if>
                                                        <span class="">
                                                             <a id="edit-employee" class="btn btn-warning submit regist"
                                                                href="${ctx}/editEmployee/${employee.id}">
                                                                 <span class="glyphicon glyphicon-pencil"></span>
                                                                 <spring:message code="employee.btn.edit"/>
                                                             </a>
                                                        </span>
                                                        <span class="">
                                                            <a class="btn bg-red-active delete" id="delete"
                                                                    <c:if test="${employee.roleId == 3}">
                                                                        disabled
                                                                    </c:if>
                                                               href="${ctx}/deleteEmployee/${employee.id}">
                                                                <span class="glyphicon glyphicon-trash"></span>
                                                                <spring:message code="employee.delete"/>
                                                            </a>
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
                                                <div class="dataTables_info"><spring:message code="employee.total"/>: ${count}</div>
                                            </div>
                                            <div class="col-sm-7 text-right">
                                                <tag:paginate max="10" offset="${offset}" count="${count}"
                                                              uri="searchEmployeePaging"
                                                              next="&raquo;" previous="&laquo;"/>
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
