$(document).ready(function () {
    function formatDate(template, date) {
        var specs = 'YYYY:MM:DD:HH:mm:ss'.split(':');
        date = new Date(date || Date.now() - new Date().getTimezoneOffset() * 6e4);
        return date.toISOString().split(/[-:.TZ]/).reduce(function (template, item, i) {
            return template.split(specs[i]).join(item);
        }, template);
    }

    var convertTimeline = function (timeline) {
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
        return html;
    };

    $('#butSendMessage').click(function () {
        var data = {
            devicesId: $('#devicesId').val(),
            message: $('#message').val()
        };
        var sendMessage = function (res, result) {
            if (result == 'success') {
                var html = convertTimeline(res);
                $('#timeLines').append(html);
                console.log(res);
                $('#message').val('');
            } else {
                console.log(res);
            }
        };
        postWs(getUrl('message/thread/send'), data, sendMessage);
    });

    var onLoadThreadMessage = function () {
        var routeId = $('#routeId').val();
        console.log(routeId);
        getWs(getUrl("messageDetail/" + routeId), function (res, result) {
            if (result == 'success') {
                console.log(res);
                $("#timeLines").html('');
                res.forEach(function (timeline) {
                    timeline.timeIn = formatDate('YYYY-MM-DD HH:mm:ss', new Date(timeline.timeIn));
                    timeline.timeOut = formatDate('YYYY-MM-DD HH:mm:ss', new Date(timeline.timeOut));
                    var html = convertTimeline(timeline);
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
    onLoadThreadMessage();
});

