<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="resetPassword.user.title"></c:set>
<%@ include file="/resources/layout/headerResource.jsp"%>

<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <t:message code="resetPassword.user"/>
    </div>
    <%@ include file="/resources/layout/messageList.jsp" %>
    <c:if test="${not empty resetResult}">
        <c:if test="${resetResult eq true}">
            <div class="alert alert-success">
                <strong><t:message code="resetPassword.user.successful"/></strong>
            </div>
        </c:if>
        <c:if test="${resetResult eq false}">
            <div class="alert alert-danger">
                <strong><t:message code="resetPassword.user.error"/></strong>
            </div>
        </c:if>
    </c:if>
    <br>
    <!-- /.login-logo -->
    <div class="login-box-body">
        <p class="login-box-msg"><t:message code="resetPassword.user.forgetPassword"/></p>
        <s:form method="post" action="${ctx}/forgotPasswordRequest" modelAttribute="forgotPasswordForm">
            <div class="form-group has-feedback">
                <input type="text" class="textbox form-control" id="email" name="email"
                       placeholder="<t:message code="resetPassword.user.emailUser"/>" value="${forgotPasswordForm.email}"/>
                <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
            </div>
            <div class="row">
                <div class="col-xs-6 col-xs-offset-6">
                    <button type="submit" class="btn btn-primary btn-block btn-flat"><t:message code="resetPassword.user"/></button>
                </div>
                <!-- /.col -->
            </div>
        </s:form>
        <br>
        <a href="${ctx}/userLoginView" class="btn-link"><t:message code="resetPassword.user.backFormLogin"/></a>
    </div>
    <!-- /.login-box-body -->
</div>
<!-- /.login-box -->
</body>
</html>

