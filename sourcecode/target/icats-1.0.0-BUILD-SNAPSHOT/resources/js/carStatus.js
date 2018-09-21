var carMap = new CatsMap();
var carStatus = {};
$(document).ready(function () {
    createCarMarkers();
    initCarStatusMap();

    $("#carFilterCondition").keyup(function() {
        filterCars();
    });

    $("#btnSearchCars").click(function () {
        var divisionId = $("#divisionsId").val().trim();
        var url = getUrl("ws/getCarList?divisionId=" + divisionId);
        getWs(url, function (carList, result) {
            console.log(carList);
            resetCarList(carList.carMapDTOList);
            carMap.removeMarkers();
            carMap = new CatsMap();
            createCarMarkers();
            initCarStatusMap();
            filterCars();
            if ( result === "error") {
                console.log('Error:' + MESSAGE_SOURCE.notFounData);
            }
        });
    });

    $("#carList table").find(".car-row").click(function () {
        carRowClick($(this));
    });

    function initCarStatusMap() {
        ZDC.Search.getByZmaps('address/word', {
            word: divisionAddress
        }, function (status, res) {
            if (status.code == '200' && res.item.length > 0 && carMap.getMarkers().length === 0) {
                var results = res.item;
                var latlng = results[0].point;
                var lat = floatFormat(latlng.lat, 7);
                var lon = floatFormat(latlng.lon, 7);
                var tkyLatLon = new ZDC.LatLon(lat, lon);
                carMap.initCarStatusMap("carStatusMap", tkyLatLon);
            } else {
                carMap.initCarStatusMap("carStatusMap");
                console.log(MESSAGE_SOURCE.failFoundCoordinate);
            }
        });
    }

    function carRowClick($row) {
        var deviceId = $row.find("td.car-device-id").text().trim();
        var markers = carMap.getMarkers();
        for (var i = 0; i < markers.length; i++) {
            var mk = markers[i];
            console.log('marker device:' + deviceId);
            if (Number(mk.deviceId) === Number(deviceId)) {
                carMap.focusOnLatLon(mk.getLatLon(), 12);
            }
        }

        $("#carList table").find(".car-row").removeClass("highlight");
        $row.addClass("highlight");
    }
    function resetCarList(carDtos) {
        $("#carList table").find("tr.car-row").remove();
        if (carDtos !== null && carDtos !== undefined) {
            for (var i = 0; i < carDtos.length; i++) {
                var carInfo = carDtos[i];
                $("#carList table tbody")
                    .append('<tr class="car-row">'
                        + '<td class="car-device-id hidden">' + carInfo.deviceId
                        + '</td>'
                        + '<td class="car-speed-id hidden">' + carInfo.speed
                        + '</td>'
                        + '<td class="car-icon-path hidden">' + carInfo.iconPath
                        + '</td>'
                        + '<td class="car-name"><i class="fa fa-fw fa-car text-red"></i><span>' + carInfo.carName + '</span>'
                        + '</td>'
                        + '<td class="car-plate-number">' + carInfo.plateNumber
                        + '</td>'
                        + '<td class="car-longitude hidden">' + carInfo.longitude
                        + '</td>'
                        + '<td class="car-latitude hidden">' + carInfo.latitude
                        + '</td>'
                        + '</tr>');
            }
            $("#carList table").find(".car-row").click(function () {
                carRowClick($(this));
            });
        }
    }

    var call = {
        callTime: 0,
        callTimer: null,
        updTimer: function() {
            this.callTime += 1000;

            $('#timer').show()
                .text( new Date(this.callTime).toUTCString().split(/ /)[4] );
        }
    };

    // Call management.
    connectCallSession(function (success) {
        if (success) {
            QB.webrtc.onCallListener = app.onCallListener;
            QB.webrtc.onCallStatsReport = app.onCallStatsReport;
            QB.webrtc.onUpdateCallListener = app.onUpdateCallListener;
            QB.webrtc.onAcceptCallListener = app.onAcceptCallListener;
            QB.webrtc.onRejectCallListener = app.onRejectCallListener;
            QB.webrtc.onStopCallListener = app.onStopCallListener;

            QB.webrtc.onUserNotAnswerListener = app.onUserNotAnswerListener;
            QB.webrtc.onRemoteStreamListener = app.onRemoteStreamListener;

            QB.webrtc.onSessionCloseListener = app.onSessionCloseListener;
            QB.webrtc.onSessionConnectionStateChangedListener = app.onSessionConnectionStateChangedListener;
            QB.webrtc.onDisconnectedListener = app.onDisconnectedListener;
        }
    });

    function connectCallSession(callback) {
        app.caller = {
            id: Number(userLoginCallId)
            ,name: userLoginCallName
            ,password: userLoginCallPassword
        };


        /** Check internet connection */
        if (!window.navigator.onLine) {
            alert(CONFIG.MESSAGES['no_internet']);
            return false;
        }

        QB.chat.connect({
            userId: app.caller.id,
            password: app.caller.password
        }, function (err, res) {
            if (err) {
                if (!_.isEmpty(app.currentSession)) {
                    app.currentSession.stop({});
                    app.currentSession = {};
                }

                app.helpers.changeFilter('#localVideo', 'no');
                app.helpers.changeFilter('#main_video', 'no');
                app.mainVideo = 0;

                $(ui.filterSelect).val('no');
                app.calleesAnwered = [];
                app.calleesRejected = [];
                if (call.callTimer) {
                    $('#timer').addClass('invisible');
                    clearInterval(call.callTimer);
                    call.callTimer = null;
                    call.callTime = 0;
                    app.helpers.network = {};
                }
                alert(res);
                return false;
            } else {
                if (typeof callback === "function") {
                    return callback(true);
                }
            }
        });
    }
});

var startLatLon = null;
function callDevice() {
    var clickedMarker = carMap.getClickedCarMarker();
    var userCallee = {
        id: Number(clickedMarker.callId)
        ,name:clickedMarker.callUserName
    };

    startLatLon = convertTkyToWgs(clickedMarker.getLatLon());
    app.startCall(userCallee);
    $('.drive-name').text(clickedMarker.driverName);
    $('.plate-number').text(clickedMarker.plateNumber);
    $('#operatorCallModal .out-call').show();
    $('#operatorCallModal .in-call').hide();
    $('#callStatus').text("Calling...");
}

function saveCallLogInfo(callLogInfo) {
    // In case call from operator to device.
    if (callLogInfo.isFromDevice !== true) {
        var carMarker = carMap.getClickedCarMarker();
        callLogInfo.isFromDevice = false;
        callLogInfo.latitude = startLatLon.lat;
        callLogInfo.longitude = startLatLon.lon;
        callLogInfo.deviceId = carMarker.deviceId;
        callLogInfo.toCallName = carMarker.toCallName;
        callLogInfo.userTags = carMarker.userTags;
        callLogInfo.routeDetailId = carMarker.routeDetailId;
    }

    callLogInfo.operatorId = operatorId;

    var url = getUrl("quickblox/call/end");
    postWs(url, callLogInfo, function (response) {});
}

function getCarCall(fromCallId) {
    // In case call from device to operator
    var allMarkers = carMap.getMarkers();
    for (var i = 0; i < allMarkers.length; i++) {
        if (allMarkers[i].callId === fromCallId) {
            return allMarkers[i];
        }
    }
}

function filterCars() {
    var carList = $("#carList table").find(".car-row");
    var filterValue = $("#carFilterCondition").val().toUpperCase();
    if (filterValue !== "" && filterValue !== null) {
        if (carList.length > 0) {
            $.each( carList, function( index  , element ) {
                var deviceLoginId = $(this).find("td.car-name").text().trim().toUpperCase();
                var plateNumber = $(this).find("td.car-plate-number").text().trim().toUpperCase();
                if (deviceLoginId.indexOf(filterValue) >= 0 || plateNumber.indexOf(filterValue) >= 0) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
    } else {
        carList.show();
    }
}

function createCarMarkers() {
    var carList = $("#carList table").find(".car-row");
    if (carList.length > 0) {
        $.each( carList, function( index  , element ) {
            console.log("Ca information:" + $(this).find("td.car-name").text());
            var carInfo = {};
            carInfo.deviceId = $(this).find("td.car-device-id").text().trim();
            carInfo.routeId = $(this).find("td.car-route-id").text().trim();
            carInfo.carName = $(this).find("td.car-name").text().trim();
            carInfo.iconPath = $(this).find("td.car-icon-path").text().trim();
            carInfo.lon = Number($(this).find("td.car-longitude").text().trim());
            carInfo.lat = Number($(this).find("td.car-latitude").text().trim());
            carInfo.deviceName = $(this).find("td.car-device-name").text().trim();
            carInfo.loginId = "loading...";
            carInfo.driverName = "loading...";
            carInfo.plateNumber = "loading...";
            carInfo.currentRouteName = "loading...";
            var marker = carMap.createCarMarker(carInfo);
            carMap.pushMarker(marker);
        });
    }
}

