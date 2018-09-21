  <aside class="main-sidebar">
    <section class="sidebar">
      <ul class="sidebar-menu tree" data-widget="tree">
        <!-- Car Status -->
        <li><a href="${ctx}/carStatusMapView"><i class="fa fa-car"></i> <span><spring:message code="operatoeAdmin.title.carStatus"/></span></a></li>

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

        <!-- Garage Management -->
        <li class="treeview">
          <a href="#">
            <i class="fa fa-home"></i> <span><spring:message code="operatoeAdmin.title.garageManage"/></span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="${ctx}/garagesList"><i class="fa fa-list-alt"></i><spring:message code="operatoeAdmin.title.garageList"/></a></li>
            <li><a href="${ctx}/addGarageView"><i class="fa fa-plus"></i><spring:message code="operatoeAdmin.title.garageAdd"/></a></li>
          </ul>
        </li>

        <!-- Device(Truck) Management -->
        <li class="treeview">
          <a href="#">
            <i class="fa fa-tablet"></i> <span><spring:message code="operatoeAdmin.title.deviceManage"/></span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="${ctx}/deviceList"><i class="fa fa-list-alt"></i><spring:message code="operatoeAdmin.title.deviceList"/></a></li>
            <li><a href="${ctx}/addDevice"><i class="fa fa-plus"></i><spring:message code="operatoeAdmin.title.deviceAdd"/></a></li>
          </ul>
        </li>

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

        <!-- Division Management -->
        <li><a href="${ctx}/divisionList"><i class="fa fa-bookmark-o"></i> <span><spring:message code="admin.title.divisionManagement"/></span></a></li>

        <!-- Operator Management -->
        <li class="treeview">
          <a href="#">
            <i class="fa fa-users"></i> <span><spring:message code="admin.title.operatoManagement"/></span>
            <span class="pull-right-container">
              <i class="fa fa-angle-left pull-right"></i>
            </span>
          </a>
          <ul class="treeview-menu">
            <li><a href="${ctx}/employeeList"><i class="fa fa-list-alt"></i> <spring:message code="admin.title.operatorList"/></a></li>
            <li><a href="${ctx}/addEmployeeView"><i class="fa fa-user-plus"></i><spring:message code="admin.title.operatorAdd"/></a></li>
          </ul>
        </li>

        <!-- System Configuration -->
        <li><a href="${ctx}/systemConfig"><i class="fa fa-gears"></i> <span><spring:message code="systemConfig.title.list"/></span></a></li>

        <!-- Logout -->
        <li><a href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out"></i> <span><spring:message code="btn.logout"/></span></a></li>

      </ul>
    </section>
    <!-- /.sidebar -->
  </aside>
