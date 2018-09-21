<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="profiles.title"></c:set>
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
			<s:form method="POST" action="${ctx}/editSystemAdminProfile" modelAttribute="systemAdminProfileForm" class="form-horizontal">
				<div class="box box-info">
					<div class="box-header with-border">
						<div class="col-md-6">
							<h3 class="box-title"></h3>
						</div>
					</div>
					<div class="box-body">
						<input type="hidden" class="form-control" name="id" value="${systemAdminProfileForm.id}">
						<div class="col-md-6">
							<div class="form-group">
								<label for="loginId" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.loginId"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="loginId" name="loginId" readonly
										   placeholder="<spring:message code="profiles.systemAdmin.title.loginId"/>"
										   value="${systemAdminProfileForm.loginId}">
								</div>
							</div>
							<div class="form-group">
								<label for="firstName" class="col-sm-4 control-label required-field"><spring:message code="profiles.systemAdmin.title.firstName"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="firstName" name="firstName"
										   placeholder="<spring:message code="profiles.systemAdmin.title.firstName"/>"
										   value="${systemAdminProfileForm.firstName}">
								</div>
							</div>
							<div class="form-group">
								<label for="lastName" class="col-sm-4 control-label required-field"><spring:message code="profiles.systemAdmin.title.lastName"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="lastName" name="lastName"
										   placeholder="<spring:message code="profiles.systemAdmin.title.lastName"/>"
										   value="${systemAdminProfileForm.lastName}">
								</div>
							</div>
							<div class="form-group">
								<label for="passwd" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.Password"/></label>
								<div class="col-sm-8">
									<input type="password" class="form-control" id="passwd" name="passwd"
										   placeholder="<spring:message code="profiles.systemAdmin.title.Password"/>"/>
								</div>
							</div>
							<div class="form-group">
								<label for="newPassword" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.NewPassword"/></label>
								<div class="col-sm-8">
									<input type="password" class="form-control" id="newPassword" name="newPassword"
										placeholder="<spring:message code="profiles.systemAdmin.title.NewPassword"/>"
										value="${systemAdminProfileForm.newPassword}"/>
								</div>
							</div>
							<div class="form-group">
								<label for="confirmPassword" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.ConfirmPassword"/></label>
								<div class="col-sm-8">
									<input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
										   placeholder="<spring:message code="profiles.systemAdmin.title.ConfirmPassword"/>"
										   value="${systemAdminProfileForm.confirmPassword}"/>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label for="userAddress" class="col-sm-4 control-label required-field"><spring:message code="profiles.systemAdmin.title.Address"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="userAddress" name="userAddress"
										   placeholder="<spring:message code="profiles.systemAdmin.title.Address"/>"
										   value="${systemAdminProfileForm.userAddress}">
								</div>
							</div>
							<div class="form-group">
								<label for="userEmail" class="col-sm-4 control-label required-field"><spring:message code="profiles.systemAdmin.title.mail"/></label>
								<div class="col-sm-8">
									<input type="email" class="form-control" id="userEmail" name="userEmail"
										   placeholder="<spring:message code="profiles.systemAdmin.title.mail"/>"
										   value="${systemAdminProfileForm.userEmail}">
								</div>
							</div>
							<div class="form-group">
								<label for="fixedPhone" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.fixPhone"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="fixedPhone" name="fixedPhone"
										   placeholder="<spring:message code="profiles.systemAdmin.title.fixPhone"/>"
										   value="${systemAdminProfileForm.fixedPhone}">
								</div>
							</div>
							<div class="form-group">
								<label for="mobilePhone" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.mobilePhone"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="mobilePhone" name="mobilePhone"
										   placeholder="<spring:message code="profiles.systemAdmin.title.mobilePhone"/>"
										   value="${systemAdminProfileForm.mobilePhone}">
								</div>
							</div>
							<div class="form-group">
								<label for="faxNumber" class="col-sm-4 control-label"><spring:message code="profiles.systemAdmin.title.faxPhone"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="faxNumber" name="faxNumber"
										   placeholder="<spring:message code="profiles.systemAdmin.title.faxPhone"/>"
										   value="${systemAdminProfileForm.faxNumber}">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="text-right">
					<button class="btn btn-primary btn-submit" type="submit"><spring:message code="profiles.systemAdmin.btn.save"/></button>
					<a class="btn btn-default" href="" data-toggle="tooltip" title="Cancel">
						<spring:message code="profiles.systemAdmin.btn.cancel"/>
					</a>
				</div>

			</s:form>
		</section>
	</div>
<%@include file="/resources/layout/footer.jsp" %>
