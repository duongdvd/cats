<!DOCTYPE html>
    <html>
    <head>
        <title><spring:message code="${titleCode}"/></title>
        <c:set value="${pageContext.request.contextPath}" var="ctx"/>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
        <!-- Bootstrap 3.3.7 -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/bootstrap/dist/css/bootstrap.min.css">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/font-awesome/css/font-awesome.min.css">
        <!--  -->

        <!-- Ionicons -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/Ionicons/css/ionicons.min.css">
        <!-- Select2 -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/select2/dist/css/select2.min.css">
        <!-- Theme style -->
        <link rel="stylesheet" href="${ctx}/resources/dist/css/AdminLTE.css">
        <!-- AdminLTE Skins. Choose a skin from the css/skins
        folder instead of downloading all of them to reduce the load. -->
        <link rel="stylesheet" href="${ctx}/resources/dist/css/skins/_all-skins.min.css">
        <!-- jvectormap -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/jvectormap/jquery-jvectormap.css">
        <!-- Date Picker -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/bootstrap-datepicker/dist/css/bootstrap-datepicker.min.css">
        <!-- Daterange picker -->
        <link rel="stylesheet" href="${ctx}/resources/bower_components/bootstrap-daterangepicker/daterangepicker.css">
        <!-- bootstrap wysihtml5 - text editor -->
        <link rel="stylesheet" href="${ctx}/resources/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css">
        <!-- Google Font -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
        <link rel="stylesheet" href="${ctx}/resources/css/style.css">
        <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">--%>
        <!-- jQuery 3 -->
        <script src="${ctx}/resources/bower_components/jquery/dist/jquery.min.js"></script>
        <!-- jQuery UI 1.11.4 -->
        <script src="${ctx}/resources/bower_components/jquery-ui/jquery-ui.min.js"></script>
        <!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
        <script>
        $.widget.bridge('uibutton', $.ui.button);
        </script>
        <!-- Bootstrap 3.3.7 -->
        <script src="${ctx}/resources/bower_components/bootstrap/dist/js/bootstrap.min.3.3.7.js"></script>
        <!-- Sparkline -->
        <script src="${ctx}/resources/bower_components/jquery-sparkline/dist/jquery.sparkline.min.js"></script>
        <!-- jvectormap -->
        <script src="${ctx}/resources/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
        <script src="${ctx}/resources/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
        <!-- jQuery Knob Chart -->
        <script src="${ctx}/resources/bower_components/jquery-knob/dist/jquery.knob.min.js"></script>
        <!-- daterangepicker -->
        <script src="${ctx}/resources/bower_components/moment/min/moment.min.js"></script>
        <script src="${ctx}/resources/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
        <!-- datepicker -->
        <script src="${ctx}/resources/bower_components/bootstrap-datepicker/dist/js/bootstrap-datepicker.min.js"></script>
        <!-- Bootstrap WYSIHTML5 -->
        <script src="${ctx}/resources/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"></script>
        <!-- Slimscroll -->
        <script src="${ctx}/resources/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>
        <!-- FastClick -->
        <script src="${ctx}/resources/bower_components/fastclick/lib/fastclick.js"></script>
        <!-- AdminLTE App -->
        <script src="${ctx}/resources/dist/js/adminlte.min.js"></script>
        <script type="text/javascript" src="${ctx}/resources/js/common.js"></script>
        <script type="text/javascript" src="${ctx}/resources/js/service.js"></script>
        <script type="text/javascript" src="${ctx}/resources/js/alert.js"></script>
        <script type="text/javascript">
        var baseUrl = '<%= request.getContextPath() %>';
        if (baseUrl == null) {
        baseUrl = '';
        }
        window.baseUrl = baseUrl;
        var MESSAGE_SOURCE = {
                notFounData : "<spring:message code="carStatus.js.notFounData"/>",
                notFoundCoordinate : "<spring:message code="carStatus.js.notFoundCoordinate"/>",
                failFoundCoordinate : "<spring:message code="carStatus.js.failFoundCoordinate"/>",
                enterYourAddress : "<spring:message code="carStatus.js.enterYourAddress"/>",
                btnEditDivisionJs : "<spring:message code="divisionEdit.btnEditDivision"/>",
                btnAddDivisionJs : "<spring:message code="divisionEdit.btnAddDivision"/>",
                btnDeleteDivisionJs : "<spring:message code="divisionEdit.btnDeleteDivisionJs"/>",

                editRoute : "<spring:message code="route.js.editRoute"/>",
                alertCustomer : "<spring:message code="route.js.alertCustomer"/>",
                carInformation : "<spring:message code="route.js.carInformation"/>",
                carName : "<spring:message code="route.js.carName"/>",
                driverName : "<spring:message code="route.js.driverName"/>",
                plateNumber : "<spring:message code="route.js.plateNumber"/>",
                currentRouteName : "<spring:message code="route.js.currentRouteName"/>",

                customerInformation : "<spring:message code="route.js.customerInformation"/>",
                customerNameJs : "<spring:message code="route.js.customerNameJs"/>",
                customerAddress : "<spring:message code="route.js.customerAddress"/>",
                descriptionCustomer : "<spring:message code="route.js.descriptionCustomer"/>",
                currentRouteNameCustomer : "<spring:message code="route.js.currentRouteNameCustomer"/>",

                textYouHave : "<spring:message code="TopNavigation.js.textYouHave"/>"
        };

        String.prototype.format = function (args) {
        var str = this;
        return str.replace(String.prototype.format.regex, function(item) {
        var intVal = parseInt(item.substring(1, item.length - 1));
        var replace;
        if (intVal >= 0) {
        replace = args[intVal];
        } else if (intVal === -1) {
        replace = "{";
        } else if (intVal === -2) {
        replace = "}";
        } else {
        replace = "";
        }
        return replace;
        });
        };
        String.prototype.format.regex = new RegExp("{-?[0-9]+}", "g");

        var operatorId = '${userLoginInfo.getId()}';
        var userLoginCallId = '${userLoginInfo.getCallId()}';
        var userLoginCallName = '${userLoginInfo.getCallName()}';
        var userLoginCallPassword = '${userLoginInfo.getCallPassword()}';
        var userLoginRole = '${userLoginInfo.getRoleId()}';
        var configNotification = '${userLoginInfo.getConfigNotification()}';
        var divisionAddress = '${userLoginInfo.divisionAddress}';

        var userRoles = {
            OPERATOR: 4
        }
     </script>
</head>