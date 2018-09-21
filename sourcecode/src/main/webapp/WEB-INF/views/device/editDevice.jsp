<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="devices.deviceEdit"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>

    <script>
        $(document).ready(function(){
            var iconPath = '${deviceForm.currentImage}';
            if (iconPath.trim() === '') {
                $('#iconPathDisplay').hide();
            }

            $("#iconPath").change(function(){
                displayChosenIcon(this, $('#iconPathDisplay'));
            });

        });
    </script>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="devices.deviceEdit"/></h1>
        </section>
        <!-- Main content -->
        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/><%@ include file="/resources/layout/messageList.jsp" %></p>
            <%--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>--%>
            <s:form method="POST" action="${ctx}/editDetail" modelAttribute="deviceForm" class="form-horizontal" enctype="multipart/form-data">
                <input type="hidden" name="id" value="${deviceForm.id}" id="id">
                <div class="box box-info">
                    <div class="box-header with-border">
                        <div class="col-md-6">
                            <h3 class="box-title"><spring:message code="devices.deviceInformation"/></h3>
                        </div>
                        <div class="col-md-6">
                            <h3 class="box-title"><spring:message code="devices.carInformation"/></h3>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="loginId" class="col-sm-3 control-label required-field"><spring:message
                                        code="devices.loginIdedit"/></label>

                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="loginId" name="loginId"
                                           placeholder="<spring:message code="devices.loginIdedit"/>"
                                           value="<c:out value="${deviceForm.loginId}"/>">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label required-field"><spring:message
                                        code="devices.statusEdit"/></label>

                                <div class="col-sm-9">
                                    <label class="radio-inline">
                                        <input type="radio" value="1"
                                        <c:if test="${deviceForm.status == 1}">
                                               checked
                                        </c:if>
                                               name="status"><spring:message
                                            code="route.add.active"/>
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" value="1"
                                        <c:if test="${deviceForm.status == 0}">
                                               checked
                                        </c:if>
                                               name="status"><spring:message code="route.add.Inactive"/>
                                    </label>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="iconPath" class="col-sm-3 control-label"><spring:message
                                        code="devices.iconPath"/></label>

                                <div class="col-sm-9">
                                    <input type="file" class="form-control hidden"
                                           accept="<spring:message code="upload.image.type"/>"
                                           id="iconPath" name="iconPath">

                                    <img id="iconPathDisplay" class="img-rounded"
                                         src="${ctx}/${deviceForm.currentImage}" width="24" height="24"/>

                                    <i class="fa fa-edit control-label" style="cursor: pointer; float: right;"
                                       onclick="document.getElementById('iconPath').click();"></i>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="carMaker" class="col-sm-3 control-label required-field"><spring:message
                                        code="devices.carMaker"/></label>

                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="carMaker" name="carMaker"
                                           placeholder="<spring:message code="devices.carMaker"/>"
                                           value="${deviceForm.carMaker}">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="carType" class="col-sm-3 control-label required-field"><spring:message
                                        code="devices.carType"/></label>

                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="carType" name="carType"
                                           placeholder="<spring:message code="devices.carType"/>"
                                           value="<c:out value="${deviceForm.carType}"/>">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="driverName" class="col-sm-3 control-label required-field"><spring:message
                                        code="devices.driverNameEdit"/></label>

                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="driverName" name="driverName"
                                           placeholder="<spring:message code="devices.driverNameEdit"/>"
                                           value="<c:out value="${deviceForm.driverName}"/>">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="plateNumber" class="col-sm-3 control-label required-field"><spring:message code="devices.plateNameEdit"/></label>

                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="plateNumber" name="plateNumber"
                                           placeholder="<spring:message code="devices.plateNameEdit"/>"
                                           value="<c:out value="${deviceForm.plateNumber}"/>">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="text-right">
                    <spring:url value="deviceList" var="deviceList"/>
                    <button class="btn btn-primary btn-submit" type="submit"><spring:message code="devices.create"/></button>
                    <a class="btn btn-default" href="${deviceList}" data-toggle="tooltip" title="<spring:message code="customer.btn.cancel"/>"><spring:message code="customer.btn.cancel"/></a>
                </div>
            </s:form>
        </section>
        <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>
