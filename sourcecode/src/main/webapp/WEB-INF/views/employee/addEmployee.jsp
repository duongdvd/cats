<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="employee.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="employee.title"/></h1>
        </section>
        <!-- Main content -->
        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/><%@ include file="/resources/layout/messageList.jsp" %></p>
            <s:form method="POST" action="${ctx}/addEmployee" modelAttribute="employeeForm" class="form-horizontal">
                <input type="text" hidden name="modeEdit" value="${modeEdit}"/>
                <input type="text" hidden name="id" value="${employeeForm.id}"/>
                <div class="box box-info">
                    <div class="box-header with-border">
                        <div class="col-sm-6">
                            <h3 class="box-title"><spring:message code="employee.employeeInformation"/></h3>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="loginId" class="col-sm-4 control-label required-field">
                                    <spring:message code="employee.LoginId"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" value="<c:out value="${employeeForm.loginId}"/>" class="form-control"
                                           id="loginId" name="loginId" placeholder=" <spring:message code="employee.LoginId"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="passwd" class="col-sm-4 control-label">
                                    <spring:message code="employee.passwd"/>:</label>
                                <div class="col-sm-8">
                                    <input type="password" value="${employeeForm.passwd}"
                                           placeholder="<spring:message code="employee.passwd"/>"
                                           class="form-control" id="passwd" readonly name="passwd">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="passwd" class="col-sm-4 control-label">
                                    <spring:message code="employee.Division"/>:</label>
                                <div class="col-sm-8">
                                    <select id="divisionsId" name="divisionsId"
                                            class="form-control select2"
                                            <c:if test="${modeEdit}">
                                                disabled
                                            </c:if>
                                            style="width: 100%">
                                        <c:forEach var="division" items="${divisionList}">
                                            <option value="${division.id}"
                                                    <c:if test="${division.id == employeeForm.divisionsId}">
                                                        selected
                                                    </c:if>
                                            >${division.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="passwd" class="col-sm-4 control-label required-field">
                                    <spring:message code="employee.email"/>:</label>
                                <div class="col-sm-8">
                                    <input type="email" value="${employeeForm.userEmail}" class="form-control"
                                           id="userEmail" name="userEmail" placeholder="<spring:message code="employee.email"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="roleId" class="col-sm-4 control-label">
                                    <spring:message code="employee.role"/>:</label>
                                <div class="col-sm-8">
                                    <select name="roleId" id="roleId"
                                            <c:if test="${employeeForm.roleId == 3}">
                                                disabled
                                            </c:if>
                                            class="form-control select2" style="width: 100%">
                                        <c:forEach var="role" items="${roleList}">
                                            <option value="${role.id}"
                                                    <c:if test="${employeeForm.roleId == role.id}">
                                                        selected
                                                    </c:if>
                                            >
                                                    ${role.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="userAddress" class="col-sm-4 control-label">
                                    <spring:message code="employee.address"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" value="${employeeForm.userAddress}"
                                           id="userAddress" name="userAddress" placeholder="<spring:message code="employee.address"/>">
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="firstName" class="col-sm-4 control-label required-field">
                                    <spring:message code="employee.firstName"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" value="<c:out value="${employeeForm.firstName}"/>"
                                           id="firstName" name="firstName" placeholder="<spring:message code="employee.firstName"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="lastName" class="col-sm-4 control-label required-field">
                                    <spring:message code="employee.lastName"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" value="<c:out value="${employeeForm.lastName}"/>"
                                           id="lastName" name="lastName" placeholder="<spring:message code="employee.lastName"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="fixedPhone" class="col-sm-4 control-label">
                                    <spring:message code="employee.fixedPhone"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="fixedPhone" value="${employeeForm.fixedPhone}"
                                           name="fixedPhone" placeholder="<spring:message code="employee.fixedPhone"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="mobilePhone" class="col-sm-4 control-label">
                                    <spring:message code="employee.mobilePhone"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" value="${employeeForm.mobilePhone}"
                                           id="mobilePhone" name="mobilePhone" placeholder="<spring:message code="employee.mobilePhone"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="faxNumber" class="col-sm-4 control-label">
                                    <spring:message code="employee.faxNumber"/>:</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" value="${employeeForm.faxNumber}"
                                           id="faxNumber" name="faxNumber" placeholder="<spring:message code="employee.faxNumber"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="faxNumber" class="col-sm-4 control-label required-field">
                                    <spring:message code="employee.status"/>:</label>
                                <div class="col-sm-8">
                                    <label class="radio-inline">
                                        <input type="radio" value="true"
                                        <c:if test="${employeeForm.status == true}">
                                               checked
                                        </c:if>
                                        <c:if test="${employeeForm.roleId == 3}">
                                               disabled
                                        </c:if>
                                               name="status"><spring:message
                                            code="route.add.active"/>
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" value="false"
                                        <c:if test="${employeeForm.status == false}">
                                               checked
                                        </c:if>
                                        <c:if test="${employeeForm.roleId == 3}">
                                               disabled
                                        </c:if>
                                               name="status"><spring:message code="route.add.Inactive"/>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="text-right">
                    <c:if test="${modeEdit}">
                        <button class="btn btn-primary btn-submit" type="submit"><spring:message code="employee.update"/></button>
                    </c:if>
                    <c:if test="${not modeEdit}">
                        <button class="btn btn-primary btn-submit" type="submit"><spring:message code="employee.create"/></button>
                    </c:if>
                    <a class="btn btn-default" href="${ctx}/employeeList" data-toggle="tooltip" title="<spring:message code="employee.cancel"/>">
                        <spring:message code="employee.cancel"/></a>
                </div>
            </s:form>
        </section>
        <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>
