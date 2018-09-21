  <aside class="main-sidebar">
    <section class="sidebar">
      <ul class="sidebar-menu tree" data-widget="tree">
        <!-- Car Status -->
        <li><a href="${ctx}/carStatusMapView"><i class="fa fa-car"></i> <span><spring:message code="operatoeAdmin.title.carStatus"/></span></a></li>
        <!-- Route -->
        <li class="treeview">
          <a href="#">
            <i class="fa fa-map-signs"></i> <span><spring:message code="operatoeAdmin.title.routeManage"/></span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="${ctx}/routeList"><i class="fa fa-list-alt"></i><spring:message code="operatoeAdmin.title.routeList"/></a></li>
            <li><a href="${ctx}/addRouteView"><i class="fa fa-plus"></i><spring:message code="operatoeAdmin.title.routeAdd"/></a></li>
          </ul>
        </li>

        <!-- Customer Management -->
        <li class="treeview">
          <a href="#">
            <i class="fa fa-map-marker"></i> <span><spring:message code="operatoeAdmin.title.customeManage"/></span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="${ctx}/customersList"><i class="fa fa-list-alt"></i><spring:message code="operatoeAdmin.title.customeList"/></a></li>
            <li><a href="${ctx}/addCustomerView"><i class="fa fa-plus"></i><spring:message code="operatoeAdmin.title.customeAdd"/></a></li>
          </ul>
        </li>

        <!-- Message List -->
        <li><a href="${ctx}/viewMessage?routeActualId"><i class="fa fa-commenting-o"></i> <span><spring:message
          code="operatoeAdmin.title.messageList"/></span></a></li>

        <!-- Report -->
        <li class="treeview">
          <a href="#">
            <i class="fa fa-bar-chart-o"></i> <span><spring:message code="operatoeAdmin.title.report"/></span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="${ctx}/exportDailyList"><i class="fa fa-circle-o"></i><spring:message code="operatoeAdmin.title.reportDaily"/></a></li>
            <li><a href="${ctx}/reportMonth"><i class="fa fa-circle-o"></i><spring:message code="operatoeAdmin.title.reportMonthly"/></a></li>
          </ul>
        </li>

        <!-- Logout -->
        <li><a href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out"></i> <span><spring:message code="btn.logout"/></span></a></li>

      </ul>
    </section>
    <div class="padingMessage"></div>
    <!-- /.sidebar -->
  </aside>
