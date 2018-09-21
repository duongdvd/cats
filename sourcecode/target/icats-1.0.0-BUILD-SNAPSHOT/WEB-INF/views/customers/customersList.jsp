<%@ include file="/resources/layout/taglib.jsp" %>
<%@ page import="jp.co.willwave.aca.model.enums.CustomerType" %>
<c:set value="${customerType}" var="isCustomer"/>
<c:choose>
	<c:when test="${isCustomer}">
		<c:set var="titleCode" value="customer.title.list"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titleCode" value="garage.title.list"></c:set>
	</c:otherwise>
</c:choose>
<%@ include file="/resources/layout/pageHeader.jsp"%>

	<script>
        function performClick(elemId) {
            var elem = document.getElementById(elemId);
            if (elem && document.createEvent) {
                var evt = document.createEvent("MouseEvents");
                evt.initEvent("click", true, false);
                elem.dispatchEvent(evt);
            }
        }

        $( document ).ready(function() {
            document.getElementById('fileImport').onchange = function () {
                $(".filePath").text(this.value);
                $(".uploadForm").show();
            };
        });
	</script>

	<div class="content-wrapper">
		<section class="content-header">
			<h1>${pageHeaderTitle}</h1>
		</section>

		<section id="content" class="content">
			<p>
				<c:choose>
					<c:when test="${isCustomer}">
						<a href="${ctx}/addCustomerView"
							class="btn btn-primary tooltips" id="create" title="<spring:message code="customer.btn.addNew"/>">
							<span class="glyphicon glyphicon-plus"></span>
							<spring:message code="customer.btn.addNew"/>
						</a>
					</c:when>
					<c:otherwise>
						<a href="${ctx}/addGarageView"
							class="btn btn-primary tooltips" id="create" title="<spring:message code="customer.btn.addNew"/>">
							<span class="glyphicon glyphicon-plus"></span>
							<spring:message code="customer.btn.addNew"/>
						</a>
					</c:otherwise>
				</c:choose>
				<a href="${ctx}/exportCustomer" id="export" name="export" class="btn btn-warning">
					<span class="glyphicon glyphicon-export"></span>
					<spring:message code="customer.btn.export"/>
				</a>
				<a href="#" id="import" name="import" class="btn btn-info" onclick="performClick('fileImport');">
					<span class="glyphicon glyphicon-import"></span>
					<spring:message code="customer.btn.import"/>
				</a>
			</p>
			<div class="row">
				<div class="uploadForm col-lg-3" style="display: none">
					<form action="${ctx}/importCustomer" method="post" enctype="multipart/form-data">
						<input type="file" id="fileImport" name="fileInput" style="display: none" />
						<input type="hidden" name="type" value="${isCustomer? CustomerType.CUSTOMER.getType() : CustomerType.GARAGE.getType()}" />
						<span class="filePath"></span>
						<input type="submit" value="<spring:message code="customer.btn.upload"/>" class="btn btn-primary glyphicon glyphicon-upload"/>
					</form>
				</div>
				<c:if test="${not empty importResult}">
					<table>
						<tr><td><b><c:out value="${importResult}"/></b></td></tr>
					</table>
				</c:if>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
						<div class="box-body">
							<s:form method="GET" action="${ctx}/searchCustomer" modelAttribute="searchCustomerForm">
                                <input type="text" name="isCustomer" value="${isCustomer}" hidden>
								<div class="panel panel-default">
									<table id="table_data" class="table table-bordered table-hover" sortby="customer.id" orderby="desc">
										<thead>
										<tr>
											<th class="text-left">
												<spring:message code="customer.title.divisionName"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.cusName"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.address"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.buildingName"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.long"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.lat"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.description"/>
											</th>
											<th class="text-left">
												<spring:message code="customer.title.status"/>
											</th>
											<th class="text-right td-search-reset" rowspan="2">
												<button type="submit" value="" class="btn btn-success tooltips" id="apply"
														data-toggle="tooltip" title="<spring:message code="customer.search"/>">
													<span class="glyphicon glyphicon-filter"></span>
													<spring:message code="customer.search"/>
												</button>
												<a class="btn btn-default tooltips reset_filter"
													data-toggle="tooltip" title="<spring:message code="customer.reset"/>">
													<span class="glyphicon glyphicon-refresh"></span>
													<spring:message code="customer.reset"/>
												</a>
											</th>
										</tr>
										<tr class="filter">
											<td>
												<select name="divisionsId" class="form-control input-sm select2" style="width: 100%;">
													<option value=""><spring:message code="customer.allDivision"/></option>
													<c:forEach var="division" items="${divisionsList}">
														<c:if test="${division.id == searchCustomerForm.divisionsId}">
															<option value="${division.id}" selected>${division.name}</option>
														</c:if>
														<c:if test="${empty searchCustomerForm.divisionsId or division.id != searchCustomerForm.divisionsId}">
															<option value="${division.id}">${division.name}</option>
														</c:if>
													</c:forEach>
												</select>
											</td>
											<td>
												<input type="text" name="name" value="${searchCustomerForm.name}"
													   id="name"
													   class="form-control input-sm" title="<spring:message code="customer.title.cusName"/>">
											</td>
											<td>
												<input type="text" name="address" value="${searchCustomerForm.address}"
													   id="address"
													   class="form-control input-sm" title="<spring:message code="customer.title.address"/>">
											</td>
											<td>
												<input type="text" name="buildingName" value="${searchCustomerForm.buildingName}"
													   id="buildingName"
													   class="form-control input-sm" title="<spring:message code="customer.title.buildingName"/>">
											</td>
											<td></td>
											<td></td>
											<td>
												<input type="text" name="description" value="${searchCustomerForm.description}"
													   class="form-control input-sm" title="<spring:message code="customer.title.description"/>">
											</td>
											<td>
												<select name="status" class="form-control input-sm">
													<c:if test="${empty searchCustomerForm.status}">
														<option selected><spring:message code="customer.allStatus"/></option>
														<option value="1"><spring:message code="customer.active"/></option>
														<option value="0"><spring:message code="customer.inactive"/></option>
													</c:if>
													<c:if test="${searchCustomerForm.status}">
														<option><spring:message code="customer.allStatus"/></option>
														<option value="1" selected><spring:message code="customer.active"/></option>
														<option value="0"><spring:message code="customer.inactive"/></option>
													</c:if>
													<c:if test="${not empty searchCustomerForm.status and not searchCustomerForm.status}">
														<option><spring:message code="customer.allStatus"/></option>
														<option value="1"><spring:message code="customer.active"/></option>
														<option value="0" selected><spring:message code="customer.inactive"/></option>
													</c:if>
												</select>
											</td>
										</tr>
										</thead>
										<tbody>
										<c:if test="${not empty customersList}">
											<c:forEach var="customer" items="${customersList}">
												<tr>
													<td class="text-center"><c:out value="${customer.divisionName}"/></td>
													<td class="text-center"><c:out value="${customer.name}"/></td>
													<td class="text-center"><c:out value="${customer.address}"/></td>
													<td class="text-center"><c:out value="${customer.buildingName}"/></td>
													<td class="text-center"><c:out value="${customer.longitude}"/></td>
													<td class="text-center"><c:out value="${customer.latitude}"/></td>
													<td class="text-center"><c:out value="${customer.description}"/></td>
													<td class="text-center">
														<c:if test="${customer.status}">
															<a href="${ctx}/changeCustome/status/${customer.id}"
															   class="btn btn-success<c:if test="${customer.editPermission eq false || customer.deleteAndChangeStatusPermission eq false}"> disabled</c:if>">
																<spring:message code="customer.active"/>
															</a>
														</c:if>
														<c:if test="${not customer.status}">
															<a href="${ctx}/changeCustome/status/${customer.id}"
															   class="btn btn-danger<c:if test="${customer.editPermission eq false || customer.deleteAndChangeStatusPermission eq false}"> disabled</c:if>">
																<spring:message code="customer.inactive"/>
															</a>
														</c:if>
													</td>
													<td class="text-right">
														<c:choose>
															<c:when test="${isCustomer}">
																<span class="">
																	<a id="edit-customer" class="btn btn-warning submit regist"
																	   href="${ctx}/editCustomer/${customer.id}">
																		<span class="glyphicon glyphicon-pencil"></span>
																		<spring:message code="customer.btn.edit"/>
																	</a>
																</span>
																<span class="">
																	<a class="btn bg-red-active delete<c:if test="${customer.editPermission eq false || customer.deleteAndChangeStatusPermission eq false}"> disabled</c:if>" id="delete"
																	   href="${ctx}/deleteCustomer/${customer.id}">
																		<span class="glyphicon glyphicon-trash"></span>
																		<spring:message code="customer.btn.delete"/>
																	</a>
																</span>
															</c:when>
															<c:otherwise>
																<span class="">
																	<a id="edit-garage" class="btn btn-warning submit regist"
																	   href="${ctx}/editGarage/${customer.id}">
																		<span class="glyphicon glyphicon-pencil"></span>
																		<spring:message code="customer.btn.edit"/>
																	</a>
																</span>
																<span class="">
																	<a class="btn bg-red-active delete<c:if test="${customer.editPermission eq false || customer.deleteAndChangeStatusPermission eq false}"> disabled</c:if>" id="delete"
																	   href="${ctx}/deleteGarage/${customer.id}">
																		<span class="glyphicon glyphicon-trash"></span>
																		<spring:message code="customer.btn.delete"/>
																	</a>
																</span>
															</c:otherwise>
														</c:choose>
													</td>
												</tr>
											</c:forEach>
										</c:if>
										</tbody>
									</table>
									<div class="panel-footer">
										<div class="row">
											<div class="col-sm-5">
												<div class="dataTables_info"><spring:message code="customer.total"/>: ${count}</div>
											</div>
											<div class="col-sm-7 text-right">
												<tag:paginate offset="${offset}" count="${count}" uri="searchCustomerPaging" next="&raquo;" previous="&laquo;"/>
											</div>
										</div>
									</div>
								</div>
							</s:form>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
				<!-- /.col -->
			</div>
			<!-- /.row -->
		</section>
		<!-- /.content -->
	</div>
<%@include file="/resources/layout/footer.jsp" %>
