<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="profiles.title.user"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
	<!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="profiles.title.user"/></h1>
        </section>

        <section id="content" class="content">
			<p><spring:htmlEscape defaultHtmlEscape="true"/>
				<%@ include file="/resources/layout/messageList.jsp" %>
				<%@ include file="/resources/layout/alert.jsp" %></p>
            <s:form method="POST" action="${ctx}/editUserProfile" modelAttribute="userProfilesInfo" class="form-horizontal">
				<div class="box box-info">
					<div class="box-header with-border">
						<div class="col-md-6">
							<h3 class="box-title"><spring:message code="profiles.title.infor"/></h3>
						</div>
						<div class="col-md-6">
							<h3 class="box-title"><spring:message code="profiles.title.division"/></h3>
						</div>
					</div>
					<div class="box-body">
						<c:if test="${not empty userProfilesInfo}">
							<div class="col-sm-6">
								<input type="text" hidden name="createDate" value="${userProfilesInfo.createDate}"/>
								<input type="text" hidden name="roleId" value="${userProfilesInfo.roleId}"/>
								<div class="form-group">
									<label for="loginId" class="col-sm-3 control-label"><spring:message code="profiles.title.loginId"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="loginId" name="loginId"
											   placeholder="<spring:message code="profiles.title.loginId"/>"
											   value="${userProfilesInfo.loginId}" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label for="firstName" class="col-sm-3 control-label required-field"><spring:message code="profiles.title.firstName"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="firstName" name="firstName"
											   placeholder="<spring:message code="profiles.title.firstName"/>"
											   value="${userProfilesInfo.firstName}"/>
									</div>
								</div>
								<div class="form-group">
									<label for="lastName" class="col-sm-3 control-label required-field"><spring:message code="profiles.title.lastName"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="lastName" name="lastName"
											   placeholder="<spring:message code="profiles.title.lastName"/>"
											   value="${userProfilesInfo.lastName}"/>
									</div>
								</div>
								<div class="form-group">
									<label for="passwd" class="col-sm-3 control-label"><spring:message code="profiles.title.Password"/></label>

									<div class="col-sm-9">
										<input type="password" class="form-control" id="passwd" name="passwd"
											placeholder="<spring:message code="profiles.title.Password"/>"/>
									</div>
								</div>
								<div class="form-group">
									<label for="newPassword" class="col-sm-3 control-label"><spring:message code="profiles.title.NewPassword"/></label>

									<div class="col-sm-9">
										<input type="password" class="form-control" id="newPassword" name="newPassword" minlength="8"
											placeholder="<spring:message code="profiles.title.NewPassword"/>"
											value="${userProfilesInfo.newPassword}" />
									</div>
								</div>
								<div class="form-group">
									<label for="confirmPassword" class="col-sm-3 control-label"><spring:message code="profiles.title.ConfirmPassword"/></label>

									<div class="col-sm-9">
										<input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="8"
											placeholder="<spring:message code="profiles.title.ConfirmPassword"/>"
											value="${userProfilesInfo.confirmPassword}" />
									</div>
								</div>
								<div class="form-group">
									<label for="userAddress" class="col-sm-3 control-label required-field"><spring:message code="profiles.title.Address"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="userAddress" name="userAddress"
											   placeholder="<spring:message code="profiles.title.Address"/>"
											   value="${userProfilesInfo.userAddress}" />
									</div>
								</div>
								<div class="form-group">
									<label for="userEmail" class="col-sm-3 control-label required-field"><spring:message code="profiles.title.mail"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="userEmail" name="userEmail"
											   placeholder="<spring:message code="profiles.title.mail"/>"
											   value="${userProfilesInfo.userEmail}">
									</div>
								</div>
								<div class="form-group">
									<label for="fixedPhone" class="col-sm-3 control-label"><spring:message code="profiles.title.fixPhone"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="fixedPhone" name="fixedPhone"
											   placeholder="<spring:message code="profiles.title.fixPhone"/>"
											   value="${userProfilesInfo.fixedPhone}">
									</div>
								</div>
								<div class="form-group">
									<label for="mobilePhone" class="col-sm-3 control-label"><spring:message code="profiles.title.mobilePhone"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="mobilePhone" name="mobilePhone"
											   placeholder="<spring:message code="profiles.title.mobilePhone"/>"
											   value="${userProfilesInfo.mobilePhone}">
									</div>
								</div>
								<div class="form-group">
									<label for="faxNumber" class="col-sm-3 control-label"><spring:message code="profiles.title.faxPhone"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="faxNumber" name="faxNumber"
											   placeholder="<spring:message code="profiles.title.faxPhone"/>"
											   value="${userProfilesInfo.faxNumber}">
									</div>
								</div>
							</div>

							<div class="col-md-6">
								<div class="form-group">
									<label for="divisionName" class="col-sm-3 control-label"><spring:message code="profiles.title.divisionName"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="divisionName" name="divisionName"
											   placeholder="<spring:message code="profiles.title.divisionName"/>" readonly
											   value="${userProfilesInfo.divisionName}">
									</div>
								</div>
								<div class="form-group">
									<label for="divisionAddress" class="col-sm-3 control-label"><spring:message code="profiles.title.divisionAddress"/></label>

									<div class="col-sm-9">
										<input type="text" class="form-control" id="divisionAddress" name="divisionAddress"
											   placeholder="<spring:message code="profiles.title.divisionAddress"/>" readonly
											   value="${userProfilesInfo.divisionAddress}">
									</div>
								</div>
							</div>
						</c:if>
					</div>
				</div>
				<div class="text-right">
					<spring:url value="carStatusMapView" var="carStatusMapView"/>
					<button class="btn btn-primary btn-submit" type="submit"><spring:message code="profiles.btn.save"/></button>
					<a class="btn btn-default" href="${carStatusMapView}" data-toggle="tooltip" title="<spring:message code="profiles.btn.cancel"/>">
						<spring:message code="profiles.btn.cancel"/>
					</a>
				</div>
            </s:form>
        </section>
        <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>
