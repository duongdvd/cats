function formatDate(template, date) {
    var specs = 'YYYY:MM:DD:HH:mm:ss'.split(':');
    date = new Date(date || Date.now() - new Date().getTimezoneOffset() * 6e4);
    return date.toISOString().split(/[-:.TZ]/).reduce(function (template, item, i) {
        return template.split(specs[i]).join(item);
    }, template);
}
var onclickMessageDetail = function (routeId) {
    getWs(getUrl("messageDetail/" + routeId), function (res, result) {
        if (result == 'success') {
            console.log(res);
            $("#timeLines").html('');
            res.forEach(function (timeline) {
                timeline.timeIn = formatDate('YYYY-MM-DD HH:mm:ss', new Date(timeline.timeIn));
                timeline.timeOut = formatDate('YYYY-MM-DD HH:mm:ss', new Date(timeline.timeOut));
                var template;
                var html;
                switch (timeline.type) {
                    case 'ROUTE_DETAIL':
                        template = $("#routeDetailTemplate").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'SAFETY_CONFIRM':
                        template = $("#safetyConfirm").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'EMERGENCY':
                        template = $("#emergencyLogs").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'MESSAGE_ADMIN':
                        template = $("#adminMessageTemplate").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'MESSAGE_DEVICE':
                        template = $("#devicesMessage").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'CALL_ADMIN_SUCCESS':
                        template = $("#adminCall").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'CALL_DEVICE_SUCCESS':
                        template = $("#deviceCall").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'MISS_CALL_ADMIN':
                        template = $("#adminMissCall").html();
                        html = Mustache.render(template, timeline);
                        break;
                    case 'MISS_CALL_DEVICE':
                        template = $("#deviceMissCall").html();
                        html = Mustache.render(template, timeline);
                        break;
                    default:
                        break;
                }
                $("#timeLines").append(html);
            });
        } else {
            console.log(res);
        }
    });
    getWs(getUrl("getRouteInfo/" + routeId), function (res, result) {
        if (result == 'success') {
            console.log(res);
            $('#routeActualName').text(res.name);
            $('#routeActualMemo').text(res.routeMemo);
            if (res.routeMemo != null) {
                $('.route_meno').css('border', ' 1px solid');
            } else {
                $('.route_meno').css('border', 'none');
            }
        } else {
            console.log(res);
        }
    })
};
var onLoadHtml = function () {
    var fromNotification = $('#fromNotification').val();
    if (fromNotification == 'true') {
        onclickMessageDetail($('#routeId').val())
    }
};