<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="message.title"></c:set>
<%@ include file="/resources/layout/headerResource.jsp"%>
<body>
    <div>
        <section id="content" class="content">
            <input type="text" id="routeId" hidden value="${routeId}">
            <script type="text/javascript" src="${ctx}/resources/js/mustache.min.js"></script>
            <script type="text/javascript" src="${ctx}/resources/js/thread.message.js"></script>
            <div id="threadMessage">
                <div class="row">
                    <div class="col-md-12">
                        <div class="box box-info direct-chat direct-chat-info">
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
                                        <div class="direct-chat-timestamp text-left"
                                             style="width: 25%; float: left">
                                            <span>{{timeIn}}</span>
                                        </div>
                                        <div style="float: left; width: 50%">
                                            <div class="text-center text-blue">
                                                <i class="fa fa-info"></i>
                                                <b>{{title}}</b>
                                            </div>
                                        </div>
                                        <div class="direct-chat-timestamp text-right"
                                             style="width: 25%; float: left">
                                            <span>{{timeOut}}</span>
                                        </div>
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
                                        <i class="fa fa-taxi fa-3x"></i>
                                        <div class="direct-chat-text">
                                            {{content}}
                                        </div>
                                    </div>
                                </script>
                                <script id="safetyConfirm" type="text/template">
                                    <div class="direct-chat-timestamp text-left">
                                        <span>{{timeIn}}</span>
                                    </div>
                                    <div class="direct-chat-msg text-left text-yellow"
                                         style="text-align: center">
                                        <i class="fa fa-get-pocket"></i>
                                        <b>{{title}} </br> {{content}}</b>
                                    </div>
                                </script>
                                <script id="emergencyLogs" type="text/template">
                                    <div class="direct-chat-timestamp text-left">
                                        <span>{{timeIn}}</span>
                                    </div>
                                    <div class="direct-chat-msg text-left text-red"
                                         style="text-align: center">
                                        <i class="fa fa-warning"></i>
                                        <b>{{title}} </br> {{content}}</b>
                                    </div>
                                </script>
                                <script id="adminCall" type="text/template">
                                    <div class="direct-chat-timestamp text-left">
                                        <span>{{timeIn}}</span>
                                    </div>
                                    <div class="direct-chat-msg text-left text-navy">
                                        <i class="fa fa-phone-square"></i>
                                        <b>{{content}}</b>
                                    </div>
                                </script>
                                <script id="deviceCall" type="text/template">
                                    <div class="direct-chat-timestamp text-right">
                                        <span>{{timeIn}}</span>
                                    </div>
                                    <div class="direct-chat-msg text-right text-navy">
                                        <i class="fa fa-phone-square"></i>
                                        <b>{{content}}</b>
                                    </div>
                                </script>
                                <script id="adminMissCall" type="text/template">
                                    <div class="direct-chat-timestamp text-left">
                                        <span>{{timeIn}}</span>
                                    </div>
                                    <div class="direct-chat-msg text-left text-navy">
                                        <i class="fa fa-phone-square"></i>
                                        <b>{{content}}</b>
                                    </div>
                                </script>
                                <script id="deviceMissCall" type="text/template">
                                    <div class="direct-chat-timestamp text-right">
                                        <span>{{timeIn}}</span>
                                    </div>
                                    <div class="direct-chat-msg text-right text-navy">
                                        <i class="fa fa-phone-square"></i>
                                        <b>{{content}}</b>
                                    </div>
                                </script>
                                <div class="direct-chat-messages" style="height: auto">
                                    <div id="timeLines"></div>
                                    <div class="col-md-12 form-group">
                                        <textarea id="message" class="form-control rounded-0" rows="1"></textarea>
                                    </div>
                                    <div class="col-md-12 form-group text-right">
                                        <input id="devicesId" value="${devicesId}" hidden/>
                                        <button id="butSendMessage" class="btn btn-success">Send message
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
<%@include file="/resources/layout/footer.jsp" %>
