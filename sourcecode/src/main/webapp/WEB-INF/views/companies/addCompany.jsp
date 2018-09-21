<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="company.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <c:choose>
                <c:when test="${modeEdit == false}"><h1><spring:message code="company.title.addCompany"/></h1></c:when>
                <c:otherwise><h1><spring:message code="company.title.edit"/></h1></c:otherwise>
            </c:choose>
        </section>
        <!-- Main content -->
        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/><%@ include file="/resources/layout/messageList.jsp" %></p>
            <s:form method="POST" action="${ctx}/addCompany" modelAttribute="CompanyForm" class="form-horizontal">
                <div class="box box-info">
                    <div class="box-header with-border">
                        <div class="col-md-6">
                            <h3 class="box-title"><spring:message code="company.title.userInformation"/></h3>
                        </div>
                        <div class="col-md-6">
                            <h3 class="box-title"><spring:message code="company.title.companyInformation"/></h3>
                        </div>
                    </div>
                    <div class="box-body">
                        <c:choose>
                            <c:when test="${modeEdit == false}"></c:when>
                            <c:otherwise></c:otherwise>
                        </c:choose>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="firstName" class="col-sm-4 control-label required-field"><spring:message code="company.label.firstname"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="firstName" name="firstName" value="<c:out value="${CompanyForm.firstName}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="lastName" class="col-sm-4 control-label required-field"><spring:message code="company.label.lastName"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="lastName" name="lastName" value="<c:out value="${CompanyForm.lastName}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="loginId" class="col-sm-4 control-label required-field"><spring:message code="company.label.loginId"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="loginId" name="loginId" value="<c:out value="${CompanyForm.loginId}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="passwd" class="col-sm-4 control-label required-field"><spring:message code="company.label.password"/></label>
                                <div class="col-sm-8">
                                    <input type="password" class="form-control" id="passwd" name="passwd" value="<c:out value="${CompanyForm.passwd}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="confirmPasswd" class="col-sm-4 control-label required-field"><spring:message code="company.label.confirmPassword"/></label>
                                <div class="col-sm-8">
                                    <input type="password" class="form-control" id="confirmPasswd" name="confirmPasswd" value="<c:out value="${CompanyForm.confirmPasswd}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="userEmail" class="col-sm-4 control-label required-field"><spring:message code="company.label.userMail"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="userEmail" name="userEmail" value="<c:out value="${CompanyForm.userEmail}"/>">
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="divisionName" class="col-sm-4 control-label required-field"><spring:message code="company.label.companyName"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="divisionName" name="divisionName" value="<c:out value="${CompanyForm.divisionName}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="description" class="col-sm-4 control-label"><spring:message code="company.label.description"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="description" name="description" value="<c:out value="${CompanyForm.description}"/>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="divisionAddress" class="col-sm-4 control-label required-field"><spring:message code="company.label.companyAddress"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="divisionAddress" name="divisionAddress" value="<c:out value="${CompanyForm.divisionAddress}"/>">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-4 control-label required-field"><spring:message code="company.label.status"/></label>
                                <div class="col-sm-8">
                                    <label class="radio-inline">
                                        <input type="radio" value="1"
                                        <c:if test="${CompanyForm.status == 1}">
                                               checked
                                        </c:if>
                                         name="status"><spring:message code="company.active"/>
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" value="0"
                                        <c:if test="${CompanyForm.status == 0}">
                                               checked
                                        </c:if>
                                        name="status"><spring:message code="company.inActive"/>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="text-right">
                    <button class="btn btn-primary btn-submit" type="submit"><spring:message code="company.create"/></button>
                    <a class="btn btn-default" href="${ctx}/companyList" data-toggle="tooltip"
                       title="<spring:message code="customer.btn.cancel"/>">
                        <spring:message code="customer.btn.cancel"/></a>
                </div>
            </s:form>
        </section>
        <!-- /.content -->
    </div>

<%@include file="/resources/layout/footer.jsp" %>
