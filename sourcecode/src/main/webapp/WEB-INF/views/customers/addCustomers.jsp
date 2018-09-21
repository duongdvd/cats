<%@ include file="/resources/layout/taglib.jsp" %>
<%@ page import="jp.co.willwave.aca.model.enums.CustomerType" %>
<c:set value="${sessionScope.customerType == null || sessionScope.customerType == '' || sessionScope.customerType == CustomerType.CUSTOMER.getType()}" var="isCustomer"/>
<c:choose>
	<c:when test="${isCustomer}">
		<c:set var="titleCode" value="customer.add.title"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titleCode" value="garage.add.title"></c:set>
	</c:otherwise>
</c:choose>
<%@ include file="/resources/layout/pageHeader.jsp"%>

	<script src="//api.its-mo.com/v3/loader?key=JSZ0fb74a744a15%7CMDoAa&api=zdcmap.js,control.js,search.js,shape.js&enc=UTF8&force=1"
			type="text/javascript"></script>
	<script type="text/javascript" src="${ctx}/resources/js/map.js"></script>
	<script>
		$(document).ready(function(){
			var map = new CatsMap();
			map.initSearchMap("mapContainer", "latitude", "longitude");

            map.getLatLngByAddress(divisionAddress);
			$("#searchLatLonByAddress").click(function () {
				map.getLatLngByAddress("address");
			});

			var iconMarker = '${customerForm.iconMarker}';
			if (iconMarker.trim() === '') {
                $('#iconMarkerDisplay').hide();
            }

            $("#iconMarker").change(function(){
                displayChosenIcon(this, $('#iconMarkerDisplay'));
            });
		});
	</script>

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>${pageHeaderTitle}</h1>
		</section>
		<!-- Main content -->
		<section id="content" class="content">
			<p><spring:htmlEscape defaultHtmlEscape="true"/>
				<%@ include file="/resources/layout/messageList.jsp" %></p>
			<s:form method="POST" action="${ctx}/addCustomer" enctype="multipart/form-data" modelAttribute="customerForm" class="form-horizontal">
				<div class="box box-info">
					<div class="box-header with-border">
						<div class="col-sm-6">
							<c:choose>
								<c:when test="${isCustomer}">
									<h3 class="box-title"><spring:message code="customer.title.CustomerDetail"/></h3>
								</c:when>
								<c:otherwise>
									<h3 class="box-title"><spring:message code="customer.title.GarageDetail"/></h3>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div class="box-body">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="col-sm-4 control-label">
									<spring:message code="customer.title.divisionName"/></label>
								<div class="col-sm-8">
									<input type="hidden" name="divisionsId" value="${customerForm.divisionsId}" >
									<input type="text" name="divisionsName" value="<c:out value="${customerForm.divisionName}"/>"  class="form-control" readonly>
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-label required-field">
									<spring:message code="customer.title.cusName"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="name" name="name"
										value="<c:out value="${customerForm.name}" escapeXml="true"/>"
										placeholder="<spring:message code="customer.title.cusName"/>">
								</div>
							</div>
							<div class="form-group">
								<label for="address" class="col-sm-4 control-label required-field">
									<spring:message code="customer.title.address"/></label>
								<div class="col-sm-8">
									<div class="input-group">
										<input type="text" class="form-control" id="address" name="address"
											value="<c:out value="${customerForm.address}" escapeXml="true"/>"
											placeholder="<spring:message code="customer.title.address"/>">
										<span class="input-group-addon" id="searchLatLonByAddress">
											<i class="glyphicon glyphicon glyphicon-search"></i>
										</span>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label for="latitude" class="col-sm-4 control-label required-field">
									<spring:message code="customer.title.lat"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="latitude" name="latitude"
										value="<c:out value="${customerForm.latitude}"/>"
										placeholder="<spring:message code="customer.title.lat"/>">
								</div>
							</div>
							<div class="form-group">
								<label for="longitude" class="col-sm-4 control-label required-field">
									<spring:message code="customer.title.long"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="longitude" name="longitude"
										value="<c:out value="${customerForm.longitude}"/>"
										placeholder="<spring:message code="customer.title.long"/>">
								</div>
							</div>
                            <div class="form-group">
                                <label for="buildingName" class="col-sm-4 control-label">
                                    <spring:message code="customer.title.buildingName"/></label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="buildingName" name="buildingName"
                                           value="<c:out value="${customerForm.buildingName}" escapeXml="true"/>"
                                           placeholder="<spring:message code="customer.title.buildingName"/>">
                                </div>
                            </div>
							<div class="form-group">
								<label for="description" class="col-sm-4 control-label">
									<spring:message code="customer.title.description"/></label>
								<div class="col-sm-8">
									<input type="text" class="form-control" id="description" name="description"
										value="<c:out value="${customerForm.description}" escapeXml="true"/>"
										placeholder="<spring:message code="customer.title.description"/>">
								</div>
							</div>
                            <div class="form-group">
                                <label for="iconMarker" class="col-sm-4 control-label">
                                    <spring:message code="customer.title.fileUpload"/></label>
                                <div class="col-sm-8">
                                    <input type="file" class="form-control hidden" accept="<spring:message code="upload.image.type"/>"
                                           id="iconMarker" name="iconMarkerFile" value="${customerForm.iconMarker}">
                                    <img id="iconMarkerDisplay"
                                         src="${ctx}/${customerForm.iconMarker}" width="24" height="24" />
                                    <i class="fa fa-edit control-label" style="cursor: pointer; float: right;"
                                       onclick="document.getElementById('iconMarker').click();"></i>
                                </div>
                            </div>
						</div>
						<div id="mapContainer" class="col-sm-6" style="min-height: 430px">

						</div>
					</div>
				</div>
				<div class="text-right">
                    <button class="btn btn-primary btn-submit" type="submit"><spring:message code="customer.btn.save"/></button>
					<c:choose>
						<c:when test="${isCustomer}">
							<a class="btn btn-default" href="${ctx}/customersList" data-toggle="tooltip" title="<spring:message code="customer.btn.cancel"/>">
								<spring:message code="customer.btn.cancel"/></a>
						</c:when>
						<c:otherwise>
							<a class="btn btn-default" href="${ctx}/garagesList" data-toggle="tooltip" title="<spring:message code="customer.btn.cancel"/>">
								<spring:message code="customer.btn.cancel"/></a>
						</c:otherwise>
					</c:choose>
				</div>
			</s:form>
		</section>
		<!-- /.content -->
	</div>

<%@include file="/resources/layout/footer.jsp" %>
