var addOrEditMode = {
    ADD: "0",
    EDIT: "1",
    CLONE: "2"
};

var $btnAddEditRouteConfirm;
var $btnDisplayRoute;
var $btnReloadMap;
var $startGarageOption;
var $endGarageOption;
var $tableCustomer;
var $tableRouteDetail;
var $devicesTest;

var routeMap = new CatsMap();
$(document).ready(function() {
    $btnAddEditRouteConfirm = $("#btnAddEditRouteConfirm");
    $btnDisplayRoute = $("#btnDisplayRoute");
    $btnReloadMap = $("#btnReloadMap");
    $startGarageOption = $("#startGarageOption");
    $endGarageOption = $("#endGarageOption");
    $tableCustomer = $("#tableCustomer");
    $tableRouteDetail = $("#tableRouteDetail");
    $devicesTest = $("#devicesTest");

    var fromCarStatus = JSON.parse($("#fromCarStatus").val().toLowerCase());
    if (fromCarStatus) {
        $startGarageOption.prop("disabled", true);
        $("#devicesTest").prop("disabled", true);
    }

    var updateCarStatusRoute = $("#updateCarStatusRoute").val();
    if (updateCarStatusRoute !== undefined && updateCarStatusRoute !== null && updateCarStatusRoute === "true") {
        var carMap = window.opener.carMap;
        carMap.getRouteOfCarFromWebService(carMap.getClickedCarMarker().deviceId, function (data) {
            console.log(data);
            carMap.removeMarkers(true);
            carMap.displayCarRoute(data.allRouteDTO)
        });
        window.close();
    } else {
        initMap(true);
    }

    $btnAddEditRouteConfirm.prop("disabled", true);
    $btnDisplayRoute.prop("disabled", true);

    $("#selectCustomer").click(function () {
        selectCustomerList();
        resetMapContent();
    });

    initEventDeleteSelectedCustomer();
    $tableRouteDetail.find("tr.customer-content").click(function () {
        clickSelectedCustomer($(this));
    });

    $btnDisplayRoute.click(function () {
        routeMap.clearAllRoutes();
        displayRouteMap(function (data) {
            $("#mapDetails").val(JSON.stringify(data));
            $btnAddEditRouteConfirm.prop("disabled", false);
        });
    });

    $btnReloadMap.click(function () {
        $btnAddEditRouteConfirm.prop("disabled", true);
        $btnDisplayRoute.prop("disabled", false);
        clearSelectedCustomerOrder();
        initMap(false);
    });

    $startGarageOption.change(function () {
        resetMapContent();
        $btnAddEditRouteConfirm.prop("disabled", true);

        var starts= $startGarageOption.find(':selected').val().split("/");
        $('input:hidden[name=startGarageId]').val(starts[0].trim());
    });

    $devicesTest.change(function () {
        resetMapContent();
        $btnAddEditRouteConfirm.prop("disabled", true);

        var devices= $devicesTest.find(':selected').val();
        $('input:hidden[name=devicesId]').val(devices[0].trim());
    });

    $endGarageOption.change(function () {
        resetMapContent();
        $btnAddEditRouteConfirm.prop("disabled", true);
        var ends = $endGarageOption.find(':selected').val().split("/");
        $('input:hidden[name=endGarageId]').val(ends[0].trim());
    });

    function displayRouteMap(callback) {
        var startPoint = routeMap.getStartPoint();
        var endPoint = routeMap.getEndPoint();
        if (startPoint === null || endPoint === null) {
            alert("Not found Start point or End Point!");
            return false;
        }

        routeMap.displayRoutes(function (data) {
            if (typeof callback === 'function') {
                return callback(data);
            }
        });

        var routeDetails = [];
        var mkrs = routeMap.getMarkers();

        startPoint.visitOrder = 0;
        routeDetails.push(startPoint.id + "," + startPoint.visitOrder);

        for (var i = 0; i < mkrs.length; i++) {
            var mk = mkrs[i];
            var clazz = "customer_" + mk.id;
            $("#selectCustomerList table tr." + clazz).find("td.customer-visitorder").val(mk.visitOrder);
            var routeDetail = mk.id + "," + mk.visitOrder;
            routeDetails.push(routeDetail);
        }

        endPoint.visitOrder = mkrs.length + 1;
        routeDetails.push(endPoint.id + "," + endPoint.visitOrder);
        $("#routeDetails").val(routeDetails);
    }

    function initMap(isDisplayRouteMap) {
        routeMap = new CatsMap();
        createStartEndPoint();
        reCreateMarkers();
        routeMap.setMaxVisitedOrder(Number($("#maxVisitedOrder").val().trim()));
        routeMap.initRouteMap("mapContainer");
        routeMap.displayMarkers();

        if (routeMap.getMarkers().length > 0) {
            routeMap.createMarkerVisitOrders();
            if (isDisplayRouteMap) {
                displayRouteMap(function (data) {
                    $("#mapDetails").val(JSON.stringify(data));
                    $btnAddEditRouteConfirm.prop("disabled", false);
                });
            }
        }
    }

    function initEventDeleteSelectedCustomer() {
        $tableRouteDetail.find("tr.customer-content .btn-delete-selected-customer").click(eventDeleteSelectedCustomer);
    }

    function resetMapContent() {
        routeMap.clearAllRoutes();
        routeMap.removeMarkers(routeMap.getMarkers());
        $btnDisplayRoute.disabled();
        $btnAddEditRouteConfirm.disabled();
    }

    function createStartEndPoint() {
        var starts= $startGarageOption.find(':selected').val().split("/");

        var ends = $endGarageOption.find(':selected').val().split("/");

        console.log("Start Point:" + starts[1] + "," + starts[2]);
        console.log("End Point:" + ends[1] + "," + ends[2]);

        routeMap.createStartPoint(
            starts[0], Number(starts[1]), Number(starts[2]), ZDC.MARKER_COLOR_ID_GREEN_S, ZDC.MARKER_NUMBER_ID_STAR_S);

        routeMap.createEndPoint(
            ends[0], Number(ends[1]), Number(ends[2]), ZDC.MARKER_COLOR_ID_RED_S, ZDC.MARKER_NUMBER_ID_STAR_S);
    }

    function selectCustomerList() {
        clearSelectedCustomerOrder();
        $tableCustomer.find("tbody").find("tr.customer-content").each(function () {
            var customerId = $(this).data("customer_id");
            var $elements = $(this).children().find("input:checkbox:first");
            if ($elements !== undefined) {
                if ($elements.is(":checked") && !checkExistsCustomerSelected(customerId)) {
                    var $cloneCustomerRow = $(this).clone();
                    $cloneCustomerRow.find(".btn-delete-selected-customer").click(eventDeleteSelectedCustomer);
                    $tableRouteDetail.find("tbody").append($cloneCustomerRow);
                }
            }
        });

        $("#selectCustomerList table tbody tr").click(function (e) {
            clickSelectedCustomer($(this));
        });
    }

    function clearSelectedCustomerOrder() {
        var $visitOrders = $tableRouteDetail.find("tbody").find("tr.customer-content .customer-visitorder");
        $.each($visitOrders, function( index  , value ) {
            var visitOrder = Number($(this).text());
            if (visitOrder > routeMap.getMaxVisitedOrder()) {
                $(this).text('');
            }
        });
    }

    function checkExistsCustomerSelected(customerId) {
        var existed = false;
        $tableRouteDetail.find("tbody").find("tr.customer-content").each(function () {
            if ($(this).data("customer_id") == customerId) {
                existed = true;
                return false;
            }
        });
        return existed;
    }

    function reCreateMarkers() {
        var customers = $("#selectCustomerList table tr");
        $.each( customers, function( index  , value ) {
            var lat = $(this).find("td.customer-lat").text();
            var lon = $(this).find("td.customer-long").text();
            var id = $(this).find("td.customer-id").text();

            if (lat !== "" && lon !== "") {
                console.log("LAT:" + lat + " LON:" + lon);
                var mkr = routeMap.createMarker(id, Number(lat), Number(lon));
                var visitOrder = Number($(this).find("td.customer-visitorder").text().trim());
                if (visitOrder > 0) {
                    mkr.visitOrder = visitOrder;
                }
                routeMap.pushMarker(mkr);
            }
        });
    }

    function deleteSelectedCustomer(el) {
        var $tr = $(el).closest("tr");
        var visitOrder = Number($tr.find("td.customer-visitorder").text().trim());
        if (!visitOrder || visitOrder > routeMap.getMaxVisitedOrder()) {
            $(el).closest("tr").remove();
            resetMapContent();
        } else {
            alert("You can not delete visited customer!");
        }
    }

    function eventDeleteSelectedCustomer(event) {
        deleteSelectedCustomer(event.target);
        resetMapContent();
    }

    function clickSelectedCustomer($row) {
        var customerId = $row.find("td.customer-id").text().trim();
        var markers = routeMap.getMarkers();
        for (var i = 0; i < markers.length; i++) {
            var mk = markers[i];
            console.log('marker customer Id:' + customerId);
            if (Number(mk.id) === Number(customerId)) {
                routeMap.focusOnLatLon(mk.getLatLon(), 12);
                break;
            }
        }
    }

    /**
     * search 訪問先一覧
     */
    var $customerFilterForm = $("#customerFilterForm");
    $customerFilterForm.find("input").on("keyup", function () {
        var customerNameFilter = $customerFilterForm.find("input[name=customerName]").val();
        var customerAddressFilter = $customerFilterForm.find("input[name=customerAddress]").val();

        $tableCustomer.find("tbody tr").each(function () {
            if (customerNameFilter) {
                if ($(this).find(".customer-name").text().indexOf(customerNameFilter) < 0) {
                    $(this).find(".customer-checkbox input[type=checkbox]").prop('checked', false);
                    $(this).addClass("hidden");
                    return;
                }
            }
            if (customerAddressFilter) {
                if ($(this).find(".customer-address").text().indexOf(customerAddressFilter) < 0) {
                    $(this).find(".customer-checkbox input[type=checkbox]").prop('checked', false);
                    $(this).addClass("hidden");
                    return;
                }
            }
            $(this).removeClass("hidden");
        })
    });

    /**
     * search 選択された訪問先一覧
     */
    var $routeDetailFilterForm = $("#routeDetailFilterForm");
    $routeDetailFilterForm.find("input").on("keyup", function () {
        var customerNameFilter = $routeDetailFilterForm.find("input[name=customerName]").val();
        var customerAddressFilter = $routeDetailFilterForm.find("input[name=customerAddress]").val();
        var customerBuildingNameFilter = $routeDetailFilterForm.find("input[name=customerBuildingName]").val();

        $tableRouteDetail.find("tbody tr").each(function () {
            if (customerNameFilter) {
                if ($(this).find(".customer-name").text().indexOf(customerNameFilter) < 0) {
                    $(this).addClass("hidden");
                    return;
                }
            }
            if (customerAddressFilter) {
                if ($(this).find(".customer-address").text().indexOf(customerAddressFilter) < 0) {
                    $(this).addClass("hidden");
                    return;
                }
            }
            if (customerBuildingNameFilter) {
                if ($(this).find(".customer-building-name").text().indexOf(customerBuildingNameFilter) < 0) {
                    $(this).addClass("hidden");
                    return;
                }
            }
            $(this).removeClass("hidden");
        })
    });

    /**
     * reset filter 訪問先一覧
     */
    $("#btnResetCustomer").click(function () {
        $customerFilterForm.find("input:first").keyup();
    });

    /**
     * reset filter 選択された訪問先一覧
     */
    $("#btnResetSelectCustomer").click(function () {
        $routeDetailFilterForm.find("input:first").keyup();
    });
});
