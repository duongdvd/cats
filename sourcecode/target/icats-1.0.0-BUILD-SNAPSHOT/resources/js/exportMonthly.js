$(document).ready(function() {
    $("#btnExportMonthReport").click(function () {
        var url = $(this).data("href");
        var isExportAll = $("#check_all_route").is(":checked");
        var listExportRouteIds = [];
        if (!isExportAll) {
            $("#table_data").find("tbody .table-item-select:checked").each(function () {
                listExportRouteIds.push($(this).data("routeid"));
            });

            if (!listExportRouteIds.length) {
                BootstrapAlert.warning({
                    message: SELECTED_NOTHING,
                    target: "#messageList"
                });
                return;
            }
        }
        $.submitForm(url, {"routeIds": listExportRouteIds}, METHOD_POST);
    });
})