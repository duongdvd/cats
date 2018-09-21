<header class="main-header">
    <script type="text/javascript" src="${ctx}/resources/js/topNavigation.js"></script>
    <!-- Logo -->
        <c:choose>
            <c:when test="${userLoginInfo.isSystemAdmin()}">
                <a href="${ctx}/usageStatus" class="logo">
            </c:when>
            <c:otherwise>
                <a href="${ctx}/carStatusMapView" class="logo">
            </c:otherwise>
        </c:choose>
        <!-- mini logo for sidebar mini 50x50 pixels -->
    <span class="logo-mini"><b><spring:message code="TopNavigation.title.mini"/></b></span>
        <!-- logo for regular state and mobile devices -->
    <span class="logo-lg"><b><spring:message code="TopNavigation.title.mini"/></b></span>
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
        <!-- Sidebar toggle button-->
        <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
            <span class="sr-only"><spring:message code="TopNavigation.title.Togglenavigation"/></span>
        </a>
        <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
                <!-- Messages: style can be found in dropdown.less-->
                <c:choose>
                    <c:when test="${userLoginInfo.isOperator()}">
                        <li class="dropdown messages-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="fa fa-envelope-o"></i>
                                <span id="countMessage" class="label label-success"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li id="titleMessage" class="header"></li>
                                <li>
                                    <!-- inner menu: contains the actual data -->
                                    <ul id="listMessage" class="menu">

                                    </ul>
                                </li>
                                <li class="footer"><a href="${ctx}/viewMessage"><spring:message code="TopNavigation.title.seeAllMessages"/></a></li>
                            </ul>
                        </li>
                        <!-- Notifications: style can be found in dropdown.less -->
                        <li class="dropdown notifications-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="fa fa-bell-o"></i><span id="countNotification" class="label label-warning"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li id="titleNotification" class="header"></li>
                                <li>
                                    <!-- inner menu: contains the actual data -->
                                    <ul class="menu" id="listNotification">
                                    </ul>
                                </li>
                                <li class="footer"><a href="${ctx}/viewMessage"><spring:message code="TopNavigation.title.viewAll"/></a></li>
                            </ul>
                        </li>
                    </c:when>
                </c:choose>
                <li>
                    <c:choose>
                        <c:when test="${userLoginInfo.isSystemAdmin()}">
                            <a href="${ctx}/editSystemAdminProfileView">
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/editProfilesUsersView">
                        </c:otherwise>
                    </c:choose>
                    <i class="fa fa-user-o"></i> ${userLoginInfo.getFirstName()} ${userLoginInfo.getLastName()}</a>
                </li>
                <li><a href="${ctx}/logout"><i class="fa fa-sign-out"></i> <spring:message code="admin.title.signOut"/></a></li>
            </ul>
        </div>
    </nav>
</header>