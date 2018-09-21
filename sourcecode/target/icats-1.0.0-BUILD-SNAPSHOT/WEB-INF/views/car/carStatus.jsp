<%@ include file="/resources/layout/taglib.jsp" %>
<c:set var="titleCode" value="carStatus.title"></c:set>
<%@ include file="/resources/layout/pageHeader.jsp"%>

    <link rel="canonical" href="https://quickblox.github.io/quickblox-javascript-sdk/samples/webrtc"/>
    <!-- use http://una.im/CSSgram/ for filters -->
    <link rel="stylesheet" href="https://cdn.rawgit.com/una/CSSgram/master/source/css/cssgram.css">
    <!-- SCRIPT -->
    <!-- dependencies -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.8.3/underscore-min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/backbone.js/1.2.3/backbone-min.js"></script>

    <!-- Check our qbMediaRecorder https://github.com/QuickBlox/javascript-media-recorder -->
    <script src="https://unpkg.com/media-recorder-js@2.0.0/qbMediaRecorder.js"></script>

    <!-- hack for github Pages -->
    <script>
        var host = "quickblox.github.io";
        if ((host == window.location.host) && (window.location.protocol != "https:"))
            window.location.protocol = "https";
    </script>
    <!-- QB -->
    <script src="${ctx}/resources/js/call/js/quickblox.min.js"></script>

    <!-- app -->
    <script>
        var appId = Number('${quickBloxConfig.appId}');
        var authKey = '${quickBloxConfig.authKey}';
        var authSecret = '${quickBloxConfig.authSecret}';
    </script>
    <script src="${ctx}/resources/js/call/js/config.js"></script>
    <script src="${ctx}/resources/js/call/js/helpers.js"></script>
    <script src="${ctx}/resources/js/call/js/stateBoard.js"></script>
    <script src="${ctx}/resources/js/call/js/app.js"></script>

    <script src="//api.its-mo.com/v3/loader?key=JSZ0fb74a744a15%7CMDoAa&api=zdcmap.js,control.js,search.js,shape.js&enc=UTF8&force=1"
            type="text/javascript"></script>
    <script type="text/javascript" src="${ctx}/resources/js/service.js"></script>
    <script type="text/javascript" src="${ctx}/resources/js/map.js"></script>
    <script type="text/javascript" src="${ctx}/resources/js/carStatus.js"></script>
    <input type="hidden" value="${carList}">

        <div class="content-wrapper">
            <div class="row">
                <div id="mapContainer" class="col-md-12">
                    <div id="carStatusMap" style="width: 100%; height: 95%">
                        <%@include file="/resources/layout/call.jsp" %>

                        <div class="modal fade" id="sendMessageModal" role="dialog">
                            <div class="modal-dialog">
                                <div class="modal-body"></div>
                            </div>
                        </div>
                        <button id="fullMap" type="button" class="map-fa" style="margin-top: 50px"
                                onclick="toggleFullScreen('carStatusMap', '100%', '95%');
                                $('#carStatusMap').toggleClass('fullScreen');">
                            <i class="fa fa-arrows"></i>
                        </button>

                        <button type="button" class="map-fa" style="margin-top: 100px">
                            <a href="#" data-toggle="control-sidebar"><i class="fa fa-car"></i></a>
                        </button>

                        <aside class="control-sidebar control-sidebar-light" style="padding-top: 0;">
                            <div class="box box-warning">
                                <div class="box-header with-border">
                                    <div class="col-md-12">
                                        <h3 class="box-title"><spring:message code="carStatus.title.listCar"/></h3>
                                        <span class="text-right" style="float: right">
                                    <a href="#" data-toggle="control-sidebar"><i class="fa fa-fw fa-close"></i></a>
                                </span>
                                    </div>
                                </div>
                                <div class="box-body">
                                    <div class="form-group">
                                        <label for="divisionsId" class="control-label"><spring:message code="carStatus.title.divisionId"/></label>
                                        <select id="divisionsId" name="divisionsId" class="form-control">
                                            <c:forEach var="division" items="${divisions}">
                                                <c:choose>
                                                    <c:when test="${division.id eq userLoginDevisionId}">
                                                        <option value="${division.id}" selected>${division.divisionName}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${division.id}">${division.divisionName}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="text-right">
                                        <button id="btnSearchCars" type="button" class="btn btn-primary"><spring:message code="carStatus.search"/></button>
                                    </div>
                                    <br>
                                    <hr>
                                    <div id="carList" class="form-group">
                                        <table class="sidebar-menu table" style="font-size: 15px">
                                            <div class="form-group">
                                                <input id="carFilterCondition" type="text" placeholder="Filter Condition" class="form-control">
                                            </div>
                                            <thead>
                                                <tr>
                                                    <th class=""><spring:message code="deviceLoginID"/></th>
                                                    <th><spring:message code="plateNumber"/></th>
                                                </tr>
                                            </thead>
                                            <tbody style="height: 300px; overflow: scroll">
                                                <c:if test="${not empty carList}">
                                                    <c:forEach var="car" items="${carList}">
                                                        <tr class="car-row">
                                                            <td class="car-device-id hidden">
                                                                    ${car.deviceId}
                                                            </td>
                                                            <td class="car-speed-id hidden">
                                                                    ${car.speed}
                                                            </td>
                                                            <td class="car-icon-path hidden">
                                                                    ${car.iconPath}
                                                            </td>
                                                            <td class="car-name">
                                                                <i class="fa fa-fw fa-car text-red"></i>
                                                                <span>${car.carName}</span>
                                                            </td>
                                                            <td class="car-plate-number">${car.plateNumber}</td>
                                                            <td class="car-longitude hidden">${car.longitude}</td>
                                                            <td class="car-latitude hidden">${car.latitude}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:if>
                                            </tbody>

                                        </table>
                                    </div>
                                </div>
                            </div>
                        </aside>
                        <!-- /.control-sidebar -->
                    </div>
                </div>

            </div>
        </div>
<%@include file="/resources/layout/footer.jsp" %>
