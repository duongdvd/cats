<!-- Left side column. contains the logo and sidebar -->
	<aside class="main-sidebar">
		<section class="sidebar">
			<ul class="sidebar-menu" data-widget="tree">
				<li class="treeview">
					<a href="#">
						<i class="fa fa-copyright"></i> <span><spring:message code="systemAdmin.title.CooperationManagement"/></span>
						<span class="pull-right-container">
						<i class="fa fa-angle-left pull-right"></i>
						</span>
					</a>
					<ul class="treeview-menu">
						<li class=""><a href="${ctx}/companyList"><i class="fa fa-list-alt"></i><spring:message code="systemAdmin.title.CooperationList"/> </a></li>
						<li class=""><a href="${ctx}/addCompanyView"><i class="fa fa-plus"></i> <spring:message code="systemAdmin.title.AddCooperation"/></a></li>
					</ul>
				</li>
				<li>
					<a href="${ctx}/usageStatus "><i class="fa fa-play-circle-o"></i> <span> <spring:message code="systemAdmin.title.CooperationUsage"/></span></a>
				</li>
				<li>
					<a href="${ctx}/editSystemAdminProfileView"><i class="fa fa-user-o"></i> <span><spring:message code="admin.title.profiles"/></span></a>
				</li>
			</ul>
		</section>
	</aside>