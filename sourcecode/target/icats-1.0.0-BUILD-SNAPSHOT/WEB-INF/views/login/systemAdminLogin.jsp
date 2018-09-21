<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="login.systemAdmin.title"></c:set>
<%@ include file="/resources/layout/headerResource.jsp"%>

<body class="hold-transition login-page">
    <div class="login-box">
        <div class="login-logo">
            <t:message code="login.systemAdmin.title"/>
        </div>
        <%@ include file="/resources/layout/messageList.jsp" %>
        <br>
        <!-- /.login-logo -->
        <div class="login-box-body">
            <p class="login-box-msg"><t:message code="login.systemAdmin.box.msg.title"/></p>
            <s:form method="post" action="${ctx}/systemAdminLogin" modelAttribute="loginForm">
                <div class="form-group has-feedback">
                    <input type="text" class="textbox form-control" id="loginId" name="loginId"
                           placeholder="<t:message code="login.systemAdmin.user.loginId"/>" value="${loginForm.loginId}"/>
                    <span class="glyphicon glyphicon-user form-control-feedback"></span>
                </div>
                <div class="form-group has-feedback">
                    <input type="password" class="textbox form-control" id="passwd" name="passwd"
                           placeholder="<t:message code="login.systemAdmin.user.password"/>" value="${loginForm.passwd}"/>
                    <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                </div>
                <div class="row">
                    <div class="col-xs-4 col-xs-offset-8">
                        <button type="submit" class="btn btn-primary btn-block btn-flat"><t:message code="login.systemAdmin.user.btn.login"/></button>
                    </div>
                    <!-- /.col -->
                </div>
            </s:form>

            <a href="${ctx}/systemAdminForgotPassword" class="btn-link"><t:message code="resetPassword.systemAdmin.forgetPassword"/></a>
        </div>
        <!-- /.login-box-body -->
    </div>
    <!-- /.login-box -->
</body>
</html>

