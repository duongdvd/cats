<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="message.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
<link rel="stylesheet" href="${ctx}/resources/css/viewMessage.css">

    <script type="text/javascript" src="${ctx}/resources/js/mustache.min.js"></script>
    <script type="text/javascript" src="${ctx}/resources/js/message.js"></script>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper" onload="onLoadHtml()">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="message.title"/></h1>
        </section>

        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/>
                <%@ include file="/resources/layout/messageList.jsp" %>
                <%@ include file="/resources/layout/alert.jsp" %>
            </p>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box box-info">
                        <div class="box-header with-border">
                            <div class="col-md-6">
                                <h3 class="box-title"><spring:message code="message.routeInfo"/></h3>
                                <input type="text" id="fromNotification" name="fromNotification" hidden
                                       value="${fromNotification}">
                                <input type="text" id="routeId" name="routeId" hidden value="${routeId}">
                            </div>
                        </div>
                        <div class="box-body filter">
                            <s:form method="GET" action="${ctx}/searchMessage" class="form-horizontal"
                                    modelAttribute="searchRouteForm">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="Date" class="col-sm-3 control-label"><spring:message code="message.date"/></label>
                                        <div class="col-sm-9">
                                            <div class="input-group input-daterange">
                                                <input id="fromDate" type="date"
                                                       class="form-control form-search-to"
                                                       name="fromDate" title="<spring:message code="message.fromDate"/>"
                                                       data-date-format="yyyy-MM-dd HH:mm:ss"
                                                       value="${searchRouteForm.fromDate}">
                                                <span class="input-group-addon form-search-middle"><spring:message code="message.to"/></span>
                                                <input id="toDate" type="date"
                                                       class="form-control form-search-to"
                                                       data-date-format="yyyy-MM-dd HH:mm:ss"
                                                       name="toDate" title="<spring:message code="message.toDate"/>"
                                                       value="${searchRouteForm.toDate}">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="routeName" class="col-sm-3 control-label"><spring:message code="message.routeName"/></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="routesName" value="${searchRouteForm.routesName}"
                                                   class="form-control"
                                                   placeholder="<spring:message code="message.routeName"/>">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="device" class="col-sm-3 control-label"><spring:message code="message.device"/></label>
                                        <div class="col-sm-9">
                                            <select name="devicesId" id="roleId" class="form-control select2" style="width: 100%">
                                                <option><spring:message code="message.AllPlateNumber"/></option>
                                                <c:forEach var="device" items="${devicesList}">
                                                    <c:if test="${device.id == searchRouteForm.devicesId}">
                                                        <option value="${device.id}"
                                                                selected>${device.plateNumber}</option>
                                                    </c:if>
                                                    <c:if test="${device.id != searchRouteForm.devicesId}">
                                                        <option value="${device.id}">${device.plateNumber}</option>
                                                    </c:if>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label"><spring:message code="message.textMessage"/></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="textMessage"
                                                   value="${searchRouteForm.textMessage}"
                                                   class="form-control"
                                                   placeholder="<spring:message code="message.textMessage"/>">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 col-md-offset-6 text-right">
                                    <button type="submit" value="" class="btn btn-success tooltips"
                                            id="apply"
                                            data-toggle="tooltip" title="<spring:message code="message.btn.search"/>">
                                        <span class="glyphicon glyphicon-filter"></span>
                                        <spring:message code="message.btn.search"/>
                                    </button>
                                    <a class="btn btn-default tooltips reset_filter"
                                       data-toggle="tooltip" title="<spring:message code="message.btn.reset"/>">
                                        <span class="glyphicon glyphicon-refresh"></span>
                                        <spring:message code="message.btn.reset"/>
                                    </a>
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

            <div class="row">
                <div class="col-md-8">
                    <div class="box box-info">
                        <div class="box-header with-border">
                            <div class="col-md-6">
                                <h3 class="box-title"><spring:message code="message.list"/></h3>
                            </div>
                        </div>
                        <div class="box-body">
                            <s:form>
                                <table id="table_data" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th id="operator_name" class="text-left">
                                            <spring:message code="message.operatorName"/>
                                        </th>
                                        <th id="date" class="text-left">
                                            <spring:message code="message.date"/>
                                        </th>
                                        <th id="device" class="text-left">
                                            <spring:message code="message.device"/>
                                        </th>
                                        <th id="routeName" class="text-left">
                                            <spring:message code="message.routeName"/>
                                        </th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="route" items="${routesList}">
                                        <tr>
                                            <td>${route.usersName}</td>
                                            <td>${route.date}</td>
                                            <td>${route.devicesName}</td>
                                            <td>${route.routesName}</td>
                                            <td class="text-right">
                                                <a onclick="onclickMessageDetail(${route.id})" class="btn btn-info">
                                                    <spring:message code="message.Detail"/></a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                                <div class="panel-footer">
                                    <div class="row">
                                        <div class="col-sm-5">
                                            <div class="dataTables_info"><spring:message code="message.total"/> ${count}</div>
                                        </div>
                                        <div class="col-sm-7 text-right">
                                            <tag:paginate max="2" offset="${offset}" count="${count}"
                                                          uri="searchMessagePaging"
                                                          next="&raquo;"
                                                          previous="&laquo;" steps="2"/>
                                        </div>
                                    </div>
                                </div>
                            </s:form>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="box box-info direct-chat direct-chat-info" style="
                    overflow-y: auto;">
                        <div class="box-header with-border">
                            <h3 id="routeActualName" class="box-title"></h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <div class="text-aqua">
                                <h4 id="routeActualMemo" class="text-center route_meno"></h4>
                            </div>
                            <!-- Conversations are loaded here -->
                            <%--template--%>
                            <script id="routeDetailTemplate" type="text/template">
                                <div class="text-blue">
                                    <div class="direct-chat-timestamp text-left" style="width: 25%; float: left">
                                        <span>{{timeIn}}</span></div>
                                    <div style="float: left; width: 50%">
                                        <div class="text-center text-blue">
                                            <i class="fa fa-info"></i>
                                            <b>{{title}}</b>
                                        </div>
                                    </div>
                                    <div class="direct-chat-timestamp text-right" style="width: 25%; float: left">
                                        <span>{{timeOut}}</span></div>
                                    <div class="clearfix"></div>
                                    <div class="text-left text-blue">{{content}}</div>
                                </div>
                            </script>
                            <script id="adminMessageTemplate" type="text/template">
                                <div class="direct-chat-msg">
                                    <div class="direct-chat-info clearfix">
                                            <span class="direct-chat-name pull-left">
                                                    {{title}}
                                            </span>
                                        <span class="direct-chat-timestamp pull-right">{{timeIn}}</span>
                                    </div>
                                    <i class="fa fa-user fa-3x"></i>
                                    <div class="direct-chat-text">
                                        {{content}}
                                    </div>
                                </div>
                            </script>
                            <script id="devicesMessage" type="text/template">
                                <div class="direct-chat-msg right">
                                    <div class="direct-chat-info clearfix">
                                            <span class="direct-chat-name pull-right">
                                                    {{title}}
                                            </span>
                                        <span class="direct-chat-timestamp pull-left">{{timeIn}}</span>
                                    </div>
                                    <img class="iconPath" src="${ctx}/{{iconPath}}" onerror="error()">
                                    <div class="direct-chat-text">
                                        {{content}}
                                    </div>
                                </div>
                            </script>
                            <script id="safetyConfirm" type="text/template">
                                <div class="direct-chat-timestamp text-left"><span>{{timeIn}}</span></div>
                                <div class="direct-chat-msg text-left text-yellow" style="text-align: center">
                                    <i class="fa fa-get-pocket"></i>
                                    <b>{{title}} </br> {{content}}</b>
                                </div>
                            </script>
                            <script id="emergencyLogs" type="text/template">
                                <div class="direct-chat-timestamp text-left"><span>{{timeIn}}</span></div>
                                <div class="direct-chat-msg text-left text-red" style="text-align: center">
                                    <i class="fa fa-warning"></i>
                                    <b>{{title}} </br> {{content}}</b>
                                </div>
                            </script>
                            <script id="adminCall" type="text/template">
                                <div class="direct-chat-timestamp text-left"><span>{{timeIn}}</span></div>
                                <div class="direct-chat-msg text-left text-navy">
                                    <i class="fa fa-phone-square"></i>
                                    <b>{{content}}</b>
                                </div>
                            </script>
                            <script id="deviceCall" type="text/template">
                                <div class="direct-chat-timestamp text-right"><span>{{timeIn}}</span></div>
                                <div class="direct-chat-msg text-right text-navy">
                                    <i class="fa fa-phone-square"></i>
                                    <b>{{content}}</b>
                                </div>
                            </script>
                            <script id="adminMissCall" type="text/template">
                                <div class="direct-chat-timestamp text-left"><span>{{timeIn}}</span></div>
                                <div class="direct-chat-msg text-left text-navy">
                                    <i class="fa fa-phone-square"></i>
                                    <b>{{content}}</b>
                                </div>
                            </script>
                            <script id="deviceMissCall" type="text/template">
                                <div class="direct-chat-timestamp text-right"><span>{{timeIn}}</span></div>
                                <div class="direct-chat-msg text-right text-navy">
                                    <i class="fa fa-phone-square"></i>
                                    <b>{{content}}</b>
                                </div>
                            </script>
                            <div id="timeLines" class="direct-chat-messages" style="height: auto">
                            </div>
                        </div>
                    </div>
                </div>
                <%@include file="/resources/layout/footer.jsp" %>
            </div>
        </section>
        <!-- /.content -->

        <script>
            var routeId = "<c:out value='${routeId}'/>";
            if(routeId.length != 0){
                $(".btn-info").click();
            }
        </script>
        <script>
            function error() {
                $('img.iconPath').replaceWith("<i class='fa fa-taxi fa-3x'></i>");
            }
        </script>

    </div>

