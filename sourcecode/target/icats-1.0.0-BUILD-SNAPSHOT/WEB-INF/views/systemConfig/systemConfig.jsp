<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="systemConfig.title.list"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <script>
        $(document).ready(function () {
            document.forms[0].action = "${ctx}/updateSystemConfig";
            document.forms[0].method = "POST";
            document.forms[0].modelAttribute = "systemConfigForm";

            $("table  tr  td input[type=checkbox]").click(function () {
                $(this).attr('value', !this.checked);
            });

            $("input[type='checkbox']").on('change', function(){
                $(this).val(this.checked ? "FALSE" : "TRUE");
            });

            var mobileIconPath = '${systemConfigForm.mobileIcon}';
            if (mobileIconPath.trim() !== '') {
                $('#mobileIconDisplay').show();
            }

            $("#mobileIconFile").change(function(){
                displayChosenIcon(this, $('#mobileIconDisplay'));
            });

            var customerIconPath = '${systemConfigForm.customerIcon}';
            if (customerIconPath.trim() !== '') {
                $('#customerIconDisplay').show();
            }

            $("#customerIconFile").change(function(){
                displayChosenIcon(this, $('#customerIconDisplay'));
            });
        });

        function reload() {
            document.forms[0].action = "${ctx}/systemConfig?divisionId=" + $("#divisionId > option:selected").val();
            document.forms[0].method = "GET";
            document.forms[0].modelAttribute = "";
            document.forms[0].submit();
        }
    </script>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="systemConfig.title.list"/></h1>
        </section>
        <!-- Main content -->
        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/>
                <%@ include file="/resources/layout/messageList.jsp" %>
            </p>
            <s:form method="POST" action="${ctx}/updateSystemConfig" enctype="multipart/form-data"
                    modelAttribute="systemConfigForm" class="form-horizontal">
            <c:if test="${not empty divisionList}">
                <input type="text" hidden name="divisionName" value="${systemConfigForm.divisionName}"/>
                <input type="text" hidden name="parentDivisionName" value="${systemConfigForm.parentDivisionName}"/>
                <input type="text" hidden name="customerIcon" value="${systemConfigForm.customerIcon}"/>
                <input type="text" hidden name="mobileIcon" value="${systemConfigForm.mobileIcon}"/>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title"><b><spring:message code="systemConfig.title.list"/></b></h3>
                </div>
                <div class="box-body">
                    <div class="col-md-10">
                        <div class="form-group">
                            <label for="divisionId" class="col-sm-3 control-label"><spring:message
                                code="systemConfig.select.division"/></label>
                            <div class="col-sm-6">
                                <select name="divisionId" id="divisionId" class="form-control select2" onchange="reload()" style="width: 100%">
                                    <c:choose>
                                        <c:when test="${empty systemConfigForm.divisionId}">
                                            <option value="${systemConfigForm.divisionId}"
                                                    selected>${systemConfigForm.divisionId}</option>
                                            <c:forEach var="division" items="${divisionList}">
                                                <option value="${division.id}">${division.divisionName}</option>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${systemConfigForm.divisionId}"
                                                    selected>${systemConfigForm.divisionName}</option>
                                            <c:forEach var="division" items="${divisionList}">
                                                <option value="${division.id}">${division.divisionName}</option>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="parentDivisionName" class="col-sm-3 control-label"><spring:message
                                code="systemConfig.parent.divisionName"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="parentDivisionName"
                                       value="<c:out value="${systemConfigForm.parentDivisionName}"/>"
                                       readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="currentDivisionName" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.currentDivisionName"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="currentDivisionName"
                                       name="currentDivisionName" value="<c:out value="${systemConfigForm.divisionName}"/>" readonly>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="box-header with-border">
                    <h3 class="box-title"><b><spring:message code="systemConfig.configuration"/></b></h3>
                </div>
                <div class="box-header with-border">
                    <h3 class="box-title"><spring:message code="systemConfig.safetyConfirm"/></h3>
                </div>
                <div class="box-body">
                    <div class="col-md-10">
                        <div class="form-group">
                            <label for="notificationTime" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.notificationTime"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="notificationTime"
                                       name="notificationTime" value="<c:out value="${systemConfigForm.notificationTime}"/>">
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setNotificationTime"
                                                           id="setNotificationTime" value="${systemConfigForm.setNotificationTime}"
                                                           <c:if test="${systemConfigForm.setNotificationTime eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                        <div class="form-group">
                            <label for="notificationEmail" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.notificationEmail"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="notificationEmail"
                                       name="notificationEmail" value="<c:out value="${systemConfigForm.notificationEmail}"/>">
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setNotificationEmail"
                                                           id="setNotificationEmail" value="${systemConfigForm.setNotificationEmail}"
                                                           <c:if test="${systemConfigForm.setNotificationEmail eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                    </div>
                </div>

                <div class="box-header with-border">
                    <h3 class="box-title"><spring:message code="systemConfig.iconSetting"/></h3>
                </div>
                <div class="box-body">
                    <div class="col-md-10">
                        <div class="form-group">
                            <label for="mobileIconFile" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.mobileIcon"/></label>
                            <div class="col-sm-6">
                                <input type="file" class="form-control hidden" id="mobileIconFile"
                                       name="mobileIconFile" value="${systemConfigForm.mobileIcon}">
                                <img id="mobileIconDisplay" class="img-rounded" style="display: none;"
                                     src="${ctx}/${systemConfigForm.mobileIcon}" width="24" height="24"/>

                                <i class="fa fa-edit control-label" style="cursor: pointer; float: right;"
                                   onclick="document.getElementById('mobileIconFile').click();"></i>
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setMobileIconFile"
                                                           id="setMobileIconFile" value="${systemConfigForm.setMobileIconFile}"
                                                           <c:if test="${systemConfigForm.setMobileIconFile eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                        <div class="form-group">
                            <label for="customerIconFile" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.customerIcon"/></label>
                            <div class="col-sm-6">
                                <input type="file" class="form-control hidden" id="customerIconFile"
                                       name="customerIconFile" value="${systemConfigForm.customerIcon}">
                                <img id="customerIconDisplay" class="img-rounded" style="display: none;"
                                     src="${ctx}/${systemConfigForm.customerIcon}" width="24" height="24"/>

                                <i class="fa fa-edit control-label" style="cursor: pointer; float: right;"
                                   onclick="document.getElementById('customerIconFile').click();"></i>
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setCustomerIconFile"
                                                           id="setCustomerIconFile" value="${systemConfigForm.setCustomerIconFile}"
                                                           <c:if test="${systemConfigForm.setCustomerIconFile eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                    </div>
                </div>

                <div class="box-header with-border">
                    <h3 class="box-title"><spring:message code="systemConfig.defaultAppSetting"/></h3>
                </div>
                <div class="box-body">
                    <div class="col-md-10">
                        <div class="form-group">
                            <label for="messageQueryFrequency" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.timeMessage"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="messageQueryFrequency"
                                       name="timeMessage" value="<c:out value="${systemConfigForm.timeMessage}"/>">
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setTimeMessage"
                                                           id="setTimeMessage" value="${systemConfigForm.setTimeMessage}"
                                                           <c:if test="${systemConfigForm.setTimeMessage eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                        <div class="form-group">
                            <label for="travelledTimeToShowAlert" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.travelTimeAlert"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="travelledTimeToShowAlert"
                                       name="travelTimeAlert" value="<c:out value="${systemConfigForm.travelTimeAlert}"/>">
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setTravelTimeAlert"
                                                           id="setTravelTimeAlert" value="${systemConfigForm.setTravelTimeAlert}"
                                                           <c:if test="${systemConfigForm.setTravelTimeAlert eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                        <div class="form-group">
                            <label for="finishVisitPlaceRadius" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.distanceFinished"/></label>
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="finishVisitPlaceRadius"
                                       name="distanceFinished" value="<c:out value="${systemConfigForm.distanceFinished}"/>">
                            </div>
                            <label class="col-sm-3"><input type="checkbox" class="no-border" name="setDistanceFinished"
                                                           id="setDistanceFinished" value="${systemConfigForm.setDistanceFinished}"
                                                           <c:if test="${systemConfigForm.setDistanceFinished eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                    </div>
                </div>

                <div class="box-header with-border">
                    <h3 class="box-title"><spring:message code="systemConfig.makerColor"/></h3>
                </div>
                <div class="box-body">
                    <div class="col-md-10">
                        <div class="form-group">
                            <label for="startEndPoint" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.startEndPointColor"/></label>
                            <div class="col-sm-3">
                                <input type="color" class="form-control" id="startEndPoint"
                                       name="startEndPointColor" value="${systemConfigForm.startEndPointColor}">
                            </div>
                            <label class="col-sm-3 col-sm-offset-3"><input type="checkbox" class="no-border" name="setStartEndPointColor"
                                                                           id="setStartEndPointColor" value="${systemConfigForm.setStartEndPointColor}"
                                                                           <c:if test="${systemConfigForm.setStartEndPointColor eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                        <div class="form-group">
                            <label for="arrivedPoint" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.arrivedPoint"/></label>
                            <div class="col-sm-3">
                                <input type="color" class="form-control" id="arrivedPoint"
                                       name="arrivedPoint" value="${systemConfigForm.arrivedPoint}">
                            </div>
                            <label class="col-sm-3 col-sm-offset-3"><input type="checkbox" class="no-border" name="setArrivedPointColor"
                                                                           id="setArrivedPointColor" value="${systemConfigForm.setArrivedPointColor}"
                                                                           <c:if test="${systemConfigForm.setArrivedPointColor eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                        <div class="form-group">
                            <label for="notArrivedPoint" class="col-sm-3 control-label">
                                <spring:message code="systemConfig.notArrivedPoint"/></label>
                            <div class="col-sm-3">
                                <input type="color" class="form-control" id="notArrivedPoint"
                                       name="notArrivedPoint" value="${systemConfigForm.notArrivedPoint}">
                            </div>
                            <label class="col-sm-3 col-sm-offset-3"><input type="checkbox" class="no-border" name="setNotArrivedPointColor"
                                                                           id="setNotArrivedPointColor" value="${systemConfigForm.setNotArrivedPointColor}"
                                                                           <c:if test="${systemConfigForm.setNotArrivedPointColor eq false}"> checked</c:if>>
                                <spring:message code="systemConfig.notSet"/></label>
                        </div>
                    </div>
                </div>
            </div>

            <div class="text-right">
                <button class="btn btn-primary btn-submit" type="submit"><spring:message code="profiles.btn.save"/></button>
            </div>
            </c:if>
    </s:form>
    </section>
    <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>
