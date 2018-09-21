<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="messagedetail.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1><spring:message code="messagedetail.title"/></h1>
        </section>

        <section id="content" class="content">
            <p><spring:htmlEscape defaultHtmlEscape="true"/>
                <%@ include file="/resources/layout/messageList.jsp" %>
                <%@ include file="/resources/layout/alert.jsp" %>
            </p>
            <div class="row">
                <div class="col-md-12">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="box box-info direct-chat direct-chat-info">
                                <div class="box-header with-border">
                                    <h3 class="box-title">${route.name}</h3>
                                </div>
                                <!-- /.box-header -->
                                <div class="box-body">
                                    <!-- Conversations are loaded here -->
                                    <div class="direct-chat-messages" style="height: auto">
                                        <c:forEach var="log" items="${logs}">
                                            <%--message admin--%>
                                            <c:if test="${log.type eq 'MESSAGE'}">
                                                <div class="direct-chat-msg">
                                                    <div class="direct-chat-info clearfix">
                                                        <span class="direct-chat-name pull-left">
                                                                ${mapMessage.get(log.id).adminName}
                                                        </span>
                                                        <span class="direct-chat-timestamp pull-right">${mapMessage.get(log.id).adminMessage.createDate}</span>
                                                    </div>
                                                    <!-- /.direct-chat-info -->
                                                    <i class="fa fa-user fa-3x"></i>
                                                    <div class="direct-chat-text">
                                                            ${mapMessage.get(log.id).adminMessage.content}
                                                    </div>
                                                    <!-- /.direct-chat-text -->
                                                </div>

                                                <c:if test="${not empty mapMessage.get(log.id).devicesMessage}">
                                                    <%--Devices reply message--%>

                                                    <div class="direct-chat-msg right">
                                                        <div class="direct-chat-info clearfix">
                                                            <span class="direct-chat-name pull-right">
                                                                    ${mapMessage.get(log.id).devicesName}
                                                            </span>
                                                            <span class="direct-chat-timestamp pull-left">${mapMessage.get(log.id).devicesMessage.createDate}</span>
                                                        </div>
                                                        <!-- /.direct-chat-info -->
                                                        <i class="fa fa-taxi fa-3x"></i>
                                                        <div class="direct-chat-text"
                                                             id="${mapMessage.get(log.id).devicesMessage.id}-${log.type}">
                                                                ${mapMessage.get(log.id).devicesMessage.content}
                                                        </div>
                                                        <!-- /.direct-chat-text -->
                                                    </div>

                                                </c:if>
                                            </c:if>
                                            <c:if test="${log.type.name() eq 'ROUTE_DETAIL'}">
                                                <%--route detail--%>
                                                <div class="direct-chat-msg" style="text-align: center">
                                                    <spring:message code="messagedetail.routeDetail"/>
                                                        ${mapRouteDetail.get(log.id).customers.name}
                                                    <spring:message code="messagedetail.routeDetailTo"/>
                                                </div>
                                            </c:if>
                                            <c:if test="${log.type eq 'SAFETY_CONFIRM'}">
                                                <%--safety confirm--%>
                                                <div class="direct-chat-msg" style="text-align: center"
                                                     id="${log.id}-${log.type}">
                                                    <spring:message code="messagedetail.sendMail"/>
                                                        ${mapSafeConfirmLogs.get(log.id).latitude} ,
                                                        ${mapSafeConfirmLogs.get(log.id).longitude} )
                                                    <spring:message code="messagedetail.sendMailTo"/>
                                                </div>
                                            </c:if>
                                            <c:if test="${log.type eq 'EMERGENCY'}">
                                                <%--emergency logs--%>
                                                <div class="direct-chat-msg" style="text-align: center"
                                                     id="${log.id}-${log.type}">
                                                    <spring:message code="messagedetail.emergency"/>
                                                    (${mapEmergencyLogs.get(log.id).latitude} ,
                                                        ${mapEmergencyLogs.get(log.id).longitude})
                                                    <spring:message code="messagedetail.emergencyTo"/>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                    <!--/.direct-chat-messages-->
                                </div>
                                <!-- /.box-body -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
<%@include file="/resources/layout/footer.jsp" %>