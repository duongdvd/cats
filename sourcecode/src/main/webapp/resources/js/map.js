var routeProperties = {
    '高速道路': {strokeColor: '#3000ff', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '都市高速道路': {strokeColor: '#008E00', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '国道': {strokeColor: '#007777', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '主要地方道': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '都道府県道': {strokeColor: '#008800', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '一般道路(幹線)': {strokeColor: '#000088', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '一般道路(その他)': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '導入路': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    '細街路(主要)': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    'フェリー航路': {},
    '道路外': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'}
};

var planActualRouteProperties = {
    NOT_ARRIVED_YET: {strokeColor: '#808080', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
    ALREADY_ARRIVED: {strokeColor: '#3000ff', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'}
};

LAT_DEFAULT = 35.6778614;
LON_DEFAULT = 139.7703167;
ZOOM_DEFAULT = 10;
var map = null;

var tokyoLatLon = new ZDC.LatLon(LAT_DEFAULT, LON_DEFAULT);
var okayamaLatLon = new ZDC.LatLon(34.6582417, 133.9369278);

var initMarker, initLatLon, difLat, difLon;
var CatsMap = function() {
    var clickedCarMarker = null;

    // To define arrived visit order.
    var maxVisitedOrder = 0;
    var startPoint = null;
    var endPoint = null;
    var visitOrderMakers = [];
    var allLatLons = [];

    var cnt = 1;
    var mapDetails = [];
    var markers = [];
    var clickedCustomerMarker = null;

    var $latInputItem;
    var $lonInputItem;
    var arrivedVisitOrderMax = 0;

    // Save Polyline object on all routes
    var plRoutes = [];

    this.initRouteMap = initRouteMap;
    this.initSearchMap = initSearchMap;
    this.initCarStatusMap = initCarStatusMap;

    this.displayMarkers = displayMarkers;
    this.displayRoutes = displayRoutes;
    this.moveLatLon = moveLatLon;
    this.setZoom = setZoom;
    this.removeMarkers = removeMarkers;
    this.createMarker = createMarker;
    this.createCarMarker = createCarMarker;
    this.pushMarker = pushMarker;
    this.createStartPoint = createStartPoint;
    this.createEndPoint = createEndPoint;
    this.getLatLngByAddress = getLatLngByAddress;
    this.autoAdjustMap = autoAdjustMap;
    this.showCarInfo = showCarInfo;
    this.getRouteOfCarFromWebService = getRouteOfCarFromWebService;
    this.displayCarRoute = displayCarRoute;

    this.setMaxVisitedOrder = setMaxVisitedOrder;
    this.getMaxVisitedOrder = getMaxVisitedOrder;

    this.getVisitOrderMakers = getVisitOrderMakers;

    this.getClickedCarMarker = getClickedCarMarker;
    this.getStartPoint = getStartPoint;
    this.getEndPoint = getEndPoint;
    this.getMarkers = getMarkers;
    this.createMarkerVisitOrders = createMarkerVisitOrders;
    this.clearAllRoutes = clearAllRoutes;
    this.focusOnLatLon = focusOnLatLon;

    function initRouteMap($containerId) {
        cnt = maxVisitedOrder;
        visitOrderMakers = [];
        allLatLons = [];
        mapDetails = [];

        map = new ZDC.Map(
            document.getElementById($containerId), {
                latlon: tokyoLatLon,
                zoom: 9,
                mapType: ZDC.MAPTYPE_HIGHRES_LV18
            }
        );

        map.addWidget(new ZDC.ScaleBar());
        map.addWidget(new ZDC.Control());
        map.pointerPositionOn();
    }

    function initSearchMap($containerId, $latInput, $lonInput) {
        $latInputItem = $("#" + $latInput);
        $lonInputItem = $("#" + $lonInput);

        initLatLon = okayamaLatLon;
        if ($latInputItem.val() !== '' && $lonInputItem.val() !== '') {
            initLatLon = convertWgsToTky(new ZDC.LatLon($latInputItem.val(), $lonInputItem.val()));
        }

        map = new ZDC.Map(
            document.getElementById($containerId), {
                latlon: initLatLon,
                zoom: 9,
                mapType: ZDC.MAPTYPE_HIGHRES_LV18
            }
        );

        map.addWidget(new ZDC.ScaleBar());
        map.addWidget(new ZDC.Control());
        map.pointerPositionOn();

        initMarker = new ZDC.Marker(initLatLon);
        map.addWidget(initMarker);

        ZDC.addListener(map, ZDC.MAP_MOUSEMOVE, onMouseMove);
        ZDC.addListener(map, ZDC.MAP_MOUSEUP, onMouseUp);

        ZDC.addListener(initMarker, ZDC.MARKER_MOUSEMOVE, onMouseMove);
        ZDC.addListener(initMarker, ZDC.MARKER_MOUSEDOWN, onMouseDown);
        ZDC.addListener(initMarker, ZDC.MARKER_MOUSEUP, onMouseUp);
    }

    function initCarStatusMap($containerId, defaultLonLon) {
        if (defaultLonLon === undefined) {
            map = new ZDC.Map(
                document.getElementById($containerId), {
                    latlon: okayamaLatLon,
                    zoom: 9,
                    mapType: ZDC.MAPTYPE_HIGHRES_LV18
                }
            );
        } else {
            map = new ZDC.Map(
                document.getElementById($containerId), {
                    latlon: defaultLonLon,
                    zoom: 9,
                    mapType: ZDC.MAPTYPE_HIGHRES_LV18
                }
            );
        }


        map.addWidget(new ZDC.ScaleBar());
        map.addWidget(new ZDC.Control());
        map.pointerPositionOn();

        if (markers.length > 0) {
            displayMarkers();
        }

    }

    function createStartPoint(id, lat, lon, color, number) {
        startPoint = createMarker(id, lat, lon, color, number);
    }
    
    function createEndPoint(id, lat, lon, color, number) {
        endPoint = createMarker(id, lat, lon, color, number);
    }

    function displayMarkers() {
        if (startPoint !== null) {
            map.addWidget(startPoint);
            allLatLons.push(startPoint.getLatLon());
        }

        if (endPoint !== null) {
            map.addWidget(endPoint);
            allLatLons.push(endPoint.getLatLon());
        }

        for (var i = 0; i < markers.length; i++) {
            var mk = markers[i];

            map.addWidget(mk);
            if (mk.visitOrder === undefined || mk.visitOrder > maxVisitedOrder) {
                addMarkerClickListener(mk);
            }

            allLatLons.push(mk.getLatLon());
            startLoadCarInfo(mk);
        }

        autoAdjustMap(allLatLons);
    }

    function addMarkerClickListener(marker) {
        console.log("click marker");
        ZDC.addListener(
            marker
            ,ZDC.MARKER_CLICK
            ,function() {
                if (marker.isCar) {
                    showCarInfo(marker);
                    getRouteOfCarFromWebService(marker.deviceId, function (data) {
                        console.log(data);
                        displayCarRoute(data.allRouteDTO)
                    });
                } else {
                    cnt = cnt + 1;
                    marker.visitOrder = cnt;
                    makeMarkerVisitOrder(marker);
                }
            }
        );
    }

    function getRouteOfCarFromWebService(deviceId, callback) {
        var url = getUrl("ws/getRouteDetail?deviceId=" + deviceId);
        console.log("getRouteOfCarFromWebService-url:" + url);
        getWs(url, function (response, result) {
            console.log(response);
            if (response.success) {
                if (typeof callback === "function") {
                    callback(response);
                }

            } else {
                alert(response.msg);
            }
        });
    }

    function showCarInfo(marker) {
        if (marker !== null) {
            // Delete msg info of old clicked marker.
            if (clickedCarMarker === null || clickedCarMarker.id !== marker.id) {
                if (clickedCarMarker !== null) {
                    clickedCarMarker.msg.close();
                    map.removeWidget(clickedCarMarker.msg);
                }

                clickedCarMarker = marker;
                createMarkerMsgInfo(clickedCarMarker);
                map.addWidget(clickedCarMarker.msg);
                clickedCarMarker.msg.open();
            } else {
                clickedCarMarker.msg.setHtml(createHtmlCarInfo(marker));
            }

            autoAjustMsgInfo("car-status-info-content");

            var $btnMessage = $('#mapContainer .car-status-info-content a#btnSendMessage');
            $btnMessage.click(function (e) {
                checkValidDevice(clickedCarMarker.deviceId);
                e.preventDefault();
                $('#sendMessageModal').modal().find('.modal-body').load(getUrl("message/thread/" + clickedCarMarker.deviceId));
            });

            var $btnEditRoute = $('#mapContainer .car-status-info-content a#btnEditRoute');
            if (clickedCarMarker.routeId === undefined || clickedCarMarker.routeId === null) {
                $btnEditRoute.hide();
            } else {
                $btnEditRoute.show();
                var url = getUrl("editRouteView?fromCarStatus=true&&routeId=" + clickedCarMarker.routePlanId);
                $btnEditRoute.click(function () {
                    openNewWindow(url, MESSAGE_SOURCE.editRoute, screen.availWidth, screen.availHeight);
                });
            }

            var $btnCall = $('#mapContainer .car-status-info-content a#btnCall');
            $btnCall.click(function (e) {
                e.preventDefault();
                $('#operatorCallModal').modal({backdrop: 'static', keyboard: false});
                callDevice();
            });
        } else {

        }
    }

    function checkValidDevice(deviceId) {
        if (clickedCarMarker.deviceId === undefined || clickedCarMarker.deviceId === null || clickedCarMarker.deviceId === "") {
            alert("Device Id is invalid");
            return false;
        }
    }
    function openNewWindow(url, title, width, height) {
        var center_left = (screen.width / 2) - (width / 2);
        var center_top = (screen.height / 2) - (height / 2);
        var my_window = window.open(url, title, "scrollbars=1, width=" + width + ", height=" + height + ", left=" + center_left + ", top=" + center_top);
        my_window.focus();
    }
    function showCustomerInfo(marker) {
        if (clickedCustomerMarker !== null) {
            clickedCustomerMarker.msg.close();
            // Delete msg info of marker.
            map.removeWidget(clickedCustomerMarker.msg);
        }

        clickedCustomerMarker = marker;
        clickedCustomerMarker.msg = new ZDC.MsgInfo(
            marker.getLatLon(),
            {
                html: createHtmlCustomerInfo(marker)
                ,offset: ZDC.Pixel(0, 0)
            }
        );

        map.addWidget(clickedCustomerMarker.msg);
        clickedCustomerMarker.msg.open();

        autoAjustMsgInfo("car-status-customer-content", clickedCustomerMarker);
    }

    function autoAjustMsgInfo(divClass) {
        var parent =  $("." + divClass).parent().parent().parent()
            .find("div:eq(1)").find("span").width(42);
        var $btnClose = $('#mapContainer span[title="閉じる"]');
        $btnClose.css("width", 13);
        $btnClose.css("top", -18);
        $btnClose.find("img").click(function () {
            clickedCarMarker = null;
        });
    }

    function startLoadCarInfo(marker) {
        setTimeout(function () {
            if (marker.deviceId === undefined) {
                return;
            }
            console.log("load car info from webserive:deviceId=" + marker.deviceId);
            var url = getUrl("ws/getCarDetail?deviceId=" + marker.deviceId);
            console.log("getCarDetail-url:" + url);
            getWs(url, function (car, result) {
                console.log(car);
                if (car !== "" && result === "success") {
                    var carInfo = car.carDetailDTO;
                    var tkyLatLon = convertWgsToTky(new ZDC.LatLon(Number(carInfo.latitude), Number(carInfo.longitude)));

                    marker.moveLatLon(tkyLatLon);
                    setCarMarkerInfo(marker, carInfo);

                    showCarInfo(clickedCarMarker);
                    $(".btn-car-status-control").show();
                }

                startLoadCarInfo(marker);
            });
        }, 5000);
    }

    function displayCarRoute(allRoute, callback) {
        clearAllRoutes();
        removeMarkers(true);
        var routeDetailPlan = allRoute.routePlanDetail;
        var routeDetailActual = allRoute.routeActualDetail;

        arrivedVisitOrderMax = routeDetailActual.length - 1;

        for (var i = 0; i < routeDetailPlan.length; i++) {
            var routePlan = routeDetailPlan[i];
            var tkyLatLon = convertWgsToTky(new ZDC.LatLon(Number(routePlan.latitude), Number(routePlan.longitude)));

            var color, number;
            if (i === 0) {
                color = ZDC.MARKER_COLOR_ID_GREEN_S;
                number = ZDC.MARKER_NUMBER_ID_STAR_S;
            } else  if (i === routePlan.length - 1) {
                color = ZDC.MARKER_COLOR_ID_RED_S;
                number = ZDC.MARKER_NUMBER_ID_STAR_S;
            } else {
                color =ZDC.MARKER_COLOR_ID_BLUE_S;
                number = ZDC['MARKER_NUMBER_ID_' + Number(routePlan.visitOrder) + '_S'];
            }

            var imageMarker = routePlan.iconMaker;
            var marker;
            if (imageMarker === undefined || imageMarker === null || imageMarker === ""  || imageMarker === "null") {
                marker = new ZDC.Marker(tkyLatLon, {
                    color: color,
                    number: number
                });
            } else {
                imageMarker = getUrl(imageMarker);
                marker = new ZDC.Marker(tkyLatLon, {
                    number: number,
                    custom: {
                        base: {
                            src: imageMarker
                        }
                    }
                });
            }

            marker.visitOrder = Number(routePlan.visitOrder);
            marker.arrivalTime = routePlan.arrivalTime;
            marker.arrivalNote = routePlan.arrivalNote;
            marker.routeDetailId = Number(routePlan.routeDetailId);
            marker.routeName = routePlan.name;
            marker.customerName = routePlan.customerName;
            marker.customerAddress= routePlan.address;
            marker.customerDescription= routePlan.description;

            addCustomerClickListener(marker);

            map.addWidget(marker);
            visitOrderMakers.push(marker);

        }

        mapDetails = [];
        if (visitOrderMakers.length > 0) {
            var visitOrderMarkersClone = [];
            for (var k = 0; k < visitOrderMakers.length; k++) {
                visitOrderMarkersClone.push(visitOrderMakers[k]);
            }

            searchRoutes(visitOrderMarkersClone, function (mapDetails) {
                if (typeof callback === "function") {
                    callback(mapDetails);
                }
            });
        }
    }

    function displayRoutes(callback) {
        if (checkExistsMarkerNotHaveVisitOrder()) {
            alert(MESSAGE_SOURCE.alertCustomer);
            return;
        }

        mapDetails = [];
        var allPoints = [];
        allPoints.push(startPoint);
        if (visitOrderMakers.length > 0) {
            allPoints = allPoints.concat(visitOrderMakers);
        }

        allPoints.push(endPoint);
        searchRoutes(allPoints, function (mapDetails) {
            if (typeof callback === "function") {
                callback(mapDetails);
            }
        });
    }

    function checkExistsMarkerNotHaveVisitOrder() {
        for (var i = 0; i < markers.length; i++) {
            var visitOrder = markers[i].visitOrder;
            if (visitOrder === undefined || Number(visitOrder) === 0) {
                return true;
            }
        }

        return false;
    }

    function searchRoutes(allPoints, callback) {
        var s = allPoints[0];
        var e = allPoints[1];

        drawRoute(s, e, function (rd) {
            rd.startVisitOrder = s.visitOrder;
            rd.endVisitOrder = e.visitOrder;
            mapDetails.push(rd);
            allPoints.splice(0, 1);
            if (allPoints.length > 1) {
                searchRoutes(allPoints, callback);
            } else {
                if (typeof callback === "function") {
                    callback(mapDetails);
                }
            }
        });
    }

    function drawRoute(s, e, callback) {
        ZDC.Search.getByZmaps('route3/drive', {
            from: s.getLatLon().lat + ',' + s.getLatLon().lon,
            to: e.getLatLon().lat + ',' + e.getLatLon().lon,
            searchType: 0
        }, function (status, res) {
            if (status.code == '200' && res.status.code == '0000') {
                var routeColor = (
                            e.visitOrder <= arrivedVisitOrderMax ? planActualRouteProperties.ALREADY_ARRIVED : planActualRouteProperties.NOT_ARRIVED_YET);

                var routeDetail = writeRoute(status, res, routeColor);
                routeDetail.distance = res.route.distance;
                if (typeof callback === "function") {
                    callback(routeDetail);
                }
            } else {
                alert(status.text);
            }
        });
    }

    function makeMarkerVisitOrder(mk) {
        var vo = mk.visitOrder;
        if (vo !== null && vo !== undefined && vo > 0) {
            var marker = new ZDC.Marker(mk.getLatLon(),{
                color: ZDC.MARKER_COLOR_ID_BLUE_S,
                number: ZDC['MARKER_NUMBER_ID_' + (mk.visitOrder) + '_S']
            });
            marker.visitOrder = Number(vo);
            marker.id = mk.id;

            map.addWidget(marker);
            visitOrderMakers.push(marker);
        }

    }

    function moveLatLon(latlon) {
        map.moveLatLon(latlon);
    }

    function setZoom(zoom) {
        map.setZoom(zoom);
    }

    function createMarker(id, lat, lon, color, number) {
        var marker = new ZDC.Marker(convertWgsToTky(new ZDC.LatLon(Number(lat), Number(lon))), {
            color: color,
            number: number
        });
        marker.id = id;
        return marker;
    }

    function createCarMarker(carInfo) {
        var latlon = new ZDC.LatLon(carInfo.lat, carInfo.lon);

        var imageMarker = carInfo.iconPath;
        if (imageMarker === undefined || imageMarker === null || imageMarker === "" || imageMarker === "null") {
            imageMarker = "resources/img/icon/car64.png";
        } else {
            imageMarker = getUrl(imageMarker);
        }

        var mk = new ZDC.Marker(convertWgsToTky(latlon), {
            custom: {
                base: {
                    src: imageMarker
                }
            }
        });

        setCarMarkerInfo(mk, carInfo);

        return mk;
    }

    function setCarMarkerInfo(mk, carInfo) {
        mk.id = carInfo.deviceId;
        mk.isCar = true;
        mk.deviceId = carInfo.deviceId;
        mk.carName = carInfo.carName;
        mk.iconPath = carInfo.iconPath;
        mk.loginId = carInfo.loginId;
        mk.driverName = carInfo.driverName;
        mk.plateNumber = carInfo.plateNumber;
        mk.currentRouteName = carInfo.currentRouteName;
        mk.routeId = carInfo.routeId;
        mk.routePlanId = carInfo.planRouteId;
        mk.callId = carInfo.callId;
        mk.callUserName = carInfo.callUserName;
        mk.userTags = carInfo.userTags;
        mk.callPassword = carInfo.callPassword;
    }

    function createMarkerMsgInfo(marker) {
        marker.msg = new ZDC.MsgInfo(
            marker.getLatLon(),
            {
                html: createHtmlCarInfo(marker)
                ,offset: ZDC.Pixel(0, 0)
            }
        );
    }

    function addCustomerClickListener(marker) {
        console.log("click marker");
        ZDC.addListener(
            marker
            ,ZDC.MARKER_CLICK
            ,function() {
                showCustomerInfo(marker);
            }
        );
    }

    function createHtmlCarInfo(marker) {
        var htmlContent =  '<div class="car-status-info-content col-md-12">'
                    + '<span class="item-border-bottom col-md-12">' + '<b>'+ MESSAGE_SOURCE.carInformation + '</b></span>'
                    + '<div style="padding: 10px 0;">'
                    + '<br/><span>' + MESSAGE_SOURCE.carName + marker.loginId + '</span> '
                    + '<br/><span>' + MESSAGE_SOURCE.driverName + marker.driverName + '</span> '
                    + '<br/><span>' + MESSAGE_SOURCE.plateNumber + marker.plateNumber + '</span> '
                    + '<br/><span>' + MESSAGE_SOURCE.currentRouteName + marker.currentRouteName + '</span> '
                    + '</div>';
        if (Number(userLoginRole) === Number(userRoles.OPERATOR)) {
            htmlContent +=
                '<div class="btn-car-status-control item-border-bottom item-border-top col-md-12 text-center">'
                + '<a href="#" id="btnSendMessage"><i class="fa fa-envelope fa-2x" style="margin: 10px;"></i></a>'
                + '<a href="#" id="btnCall"><i class="fa fa-phone fa-2x" style="margin: 10px;"></i></a>'
                + '<a href="#" id="btnEditRoute"><i class="fa fa-edit fa-2x" style="margin: 10px;"></i></a>'
                + '</div>'

        }

        htmlContent += '</div>';
        return htmlContent;
    }

    function createHtmlCustomerInfo(marker) {
        return '<div class="car-status-customer-content">'
            + '<span class="item-border-bottom">' + '<b>'+ MESSAGE_SOURCE.customerInformation + '</b></span>'
                + '<div style="padding: 5px 0;">'
                    + '<br/><span>' + MESSAGE_SOURCE.customerNameJs + marker.customerName + '</span> '
                    + '<br/><span>' + MESSAGE_SOURCE.customerAddress + marker.customerAddress + '</span> '
                    + '<br/><span>' + MESSAGE_SOURCE.descriptionCustomer + marker.customerDescription + '</span> '
                    + '<br/><span>' + MESSAGE_SOURCE.currentRouteNameCustomer + marker.routeName + '</span> '
                + '</div>'
            + '</div>';
    }

    function pushMarker(mkr) {
       markers.push(mkr);
    }

    function removeMarkers(ignoreCarMarker) {
        if (map != null) {
            // To save car marker.
            var carMarkers = [];
            for (var i = 0;  i < markers.length; i++) {
                var marker = markers[i];

                if (marker.isCar) {
                    if (ignoreCarMarker !== true) {
                        map.removeWidget(marker);
                        if (marker.msg !== undefined) {
                            map.removeWidget(markers.msg);
                        }
                    } else {
                        carMarkers.push(marker);
                    }
                } else {
                    map.removeWidget(marker);
                }
            }

            markers=[];

            if (carMarkers.length > 0) {
                markers = markers.concat(carMarkers);
            }


            if (startPoint !== undefined && startPoint !== null) {
                map.removeWidget(startPoint);
                startPoint = null;
            }

            if (endPoint !== undefined && endPoint !== null) {
                map.removeWidget(endPoint);
                endPoint = null;
            }

            if (visitOrderMakers.length > 0) {
                for (var j = 0;  j < visitOrderMakers.length; j++) {
                    var vMarker = visitOrderMakers[j];
                    map.removeWidget(vMarker);
                    if (vMarker.isCar && vMarker.msg !== undefined) {
                        map.removeWidget(vMarker.msg);
                    }
                }
                visitOrderMakers=[];
            }
        }
    }

    function markerClick() {
        var url = this.link.guidance.imageurl[0].url;
        var road_name  = this.link.guidance.routeName;
        var cross_name = this.link.guidance.pointName;

        if (road_name == null) {
            road_name = 'なし';
        }
        if (cross_name == null) {
            cross_name = 'なし';
        }

        var html = '<div style = "width:200px; height:170px; overflow-y:scroll;">';
        html += '<img src="' + url + '" width="100%"></src>';
        html += '<table border="1" style="width:180px;">';
        html += '<tr>';
        html += '<td width="35%" style="font-size:10pt;">道路名</td>';
        html += '<td style="font-size:10pt;">'+ road_name +'</td>';
        html += '</tr>';
        html += '<tr>';
        html += '<td style="font-size:10pt;">交差点名</td>';
        html += '<td style="font-size:10pt;">'+ cross_name +'</td>';
        html += '</tr></table></div>';
        msg_info.setHtml(html);
        msg_info.moveLatLon(new ZDC.LatLon(this.link.line.latlon[0], this.link.line.latlon[1]));
        msg_info.open();
    }

    function writeRoute(status, res, opt) {
        console.log("Respone:" + res);
        var routeDetail = {};
        routeDetail.line = [];

        var links = res.route.link;
        var latlons = [];

        for (var i = 0; i < links.length; i++) {
            var link = links[i];

            if (opt === undefined || opt === null || opt === "") {
                opt = routeProperties[link.roadType];
            }

            var pllatlons = [];

            for (var k = 0; k < link.line.latlon.length; k += 2) {
                var lat = link.line.latlon[k];
                var lon = link.line.latlon[k + 1];

                var tkyLatLon = new ZDC.LatLon(lat, lon);
                var wgsLatLon = convertTkyToWgs(tkyLatLon);
                pllatlons.push(tkyLatLon);
                latlons.push(tkyLatLon);

                // Set route details with world map.
                routeDetail.line.push({"latitude" :wgsLatLon.lat, "longtitude" : wgsLatLon.lon})
            }

            var pl = new ZDC.Polyline(pllatlons, opt);
            map.addWidget(pl);
            plRoutes.push(pl);

            if (link.guidance != null) {
                var url = link.guidance.imageurl;
                if (url.length !== 0) {
                    var mk = new ZDC.Marker(new ZDC.LatLon(link.line.latlon[0], link.line.latlon[1]));
                    ZDC.bind(mk, ZDC.MARKER_CLICK, {link: link}, markerClick);
                }
            }
        }

        return routeDetail;
    }

    function autoAdjustMap(latlons, zoom) {
        if (latlons.length > 0) {
            var adjust = map.getAdjustZoom(latlons);
            map.moveLatLon(adjust.latlon);
            if (zoom === undefined) {
                map.setZoom(adjust.zoom);
            } else {
                map.setZoom(zoom);
            }
        } else {
            if (zoom === undefined) {
                map.setZoom(ZOOM_DEFAULT);
            }
        }
    }

    function focusOnLatLon(latlon, zoom) {
        map.moveLatLon(latlon);
        if (zoom === undefined) {
            map.setZoom(12);
        } else {
            map.setZoom(zoom);
        }
    }

    function setMaxVisitedOrder(v) {
        maxVisitedOrder = v;
    }

    function getMaxVisitedOrder() {
        return maxVisitedOrder;
    }

    function getClickedCarMarker() {
        return clickedCarMarker;
    }

    function getStartPoint() {
        return startPoint;
    }

    function getEndPoint() {
        return endPoint;
    }

    function getMarkers() {
        return markers;
    }

    function getVisitOrderMakers() {
        return visitOrderMakers;
    }

    function createMarkerVisitOrders() {
        for (var i = 0; i < markers.length; i++) {
            if (markers[i].visitOrder > 0) {
                makeMarkerVisitOrder(markers[i]);
            }
        }
    }


    /**
     * Get lat and lng from address input.
     */
    function getLatLngByAddress(address) {
        var $address = $("#" + address);
        if ($address.val() !== '') {
            ZDC.Search.getByZmaps('address/word', {
                word: $address.val()
            }, function (status, res) {
                if (status.code == '200') {
                    if (res.item.length === 0) {
                        console.log(MESSAGE_SOURCE.notFoundCoordinate);
                    } else {
                        var results = res.item;
                        var latlng = results[0].point;
                        var lat = floatFormat(latlng.lat, 7);
                        var lon = floatFormat(latlng.lon, 7);
                        var tkyLatLon = new ZDC.LatLon(lat, lon);
                        var wgsLatLon = convertTkyToWgs(tkyLatLon);
                        initLatLon = new ZDC.LatLon(lat, lon);
                        $latInputItem.val(wgsLatLon.lat);
                        $lonInputItem.val(wgsLatLon.lon);
                        map.moveLatLon(tkyLatLon);
                        initMarker.moveLatLon(tkyLatLon);
                        map.setZoom(12);
                    }
                } else {
                    alert(MESSAGE_SOURCE.failFoundCoordinate);
                }
            });
        } else {
            if (!bulk) {
                alert(MESSAGE_SOURCE.enterYourAddress);
            }
        }
    }

    function onMouseDown() {
        console.log('onMouseDown');
        console.log('initLatLon=(' + initLatLon.lat + "," + initLatLon.lon + ")");
        var cLatLon = map.getPointerPosition();
        difLat = cLatLon.lat - initLatLon.lat;
        difLon = cLatLon.lon - initLatLon.lon;
        dragging = true;
    }

    function onMouseMove() {
        console.log('onMouseMove');
        console.log('dragging = ' + dragging);
        if (dragging) {
            var latlon = map.getPointerPosition();

            /* マーカ表示緯度経度を取得 */
            var mkLat = latlon.lat - difLat;
            var mkLon = latlon.lon - difLon;
            console.log("Marker Lat Lon is:(" + mkLat + "," + mkLon + ")");
            var tkyLatLon = new ZDC.LatLon(mkLat, mkLon);
            var wgsLatLon = convertTkyToWgs(tkyLatLon);
            initLatLon = tkyLatLon;
            initMarker.moveLatLon(initLatLon);
            $latInputItem.val(wgsLatLon.lat);
            $lonInputItem.val(wgsLatLon.lon);
        }
    }

    function clearAllRoutes() {
        for (var i = 0; i < plRoutes.length; i++) {
            map.removeWidget(plRoutes[i]);
        }
        plRoutes = [];
    }

    function onMouseUp() {
        console.log('onMouseUp');
        if (dragging) {
            dragging = false;
        }
    }
};

function convertTkyToWgs(tkyLatLon) {
    return ZDC.tkyTowgs(tkyLatLon);
}

function convertWgsToTky(wgsLatLon) {
    return ZDC.wgsTotky(wgsLatLon);
}

function floatFormat(value, num) {
    var result = value;
    if ($.isNumeric(value)) {
        value = Number(value);
        var _pow = Math.pow(10, num);
        result = Math.round(value * _pow) / _pow;
    }

    return result;
}