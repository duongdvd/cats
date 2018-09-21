$(document).ready(function () {
    var loadNotificationAndMessage = function () {
        getWs(getUrl('notifications'), function (res, result) {
            if (result == 'success') {
                $('#countNotification').text(res.length);
                $('#titleNotification').text(MESSAGE_SOURCE.textYouHave.format([res.length]));
                var notificationHtml = '';
                res.forEach(function (notification) {
                    if (notification.type == 'SAFETY_CONFIRM') {
                        notificationHtml = notificationHtml + '<li><a href="' + getUrl('notification/readDetail/' + notification.id + '/' + notification.type) + '"><i' +
                            ' class="fa fa-truck' +
                            ' text-aqua"></i>'
                            + notification.message + '</a></ul></li>';
                    } else {
                        notificationHtml = notificationHtml + '<li><a href="' + getUrl('notification/readDetail/' + notification.id + '/' + notification.type) + '"><i class="fa' +
                            ' fa-exclamation-triangle text-aqua"></i>' + notification.message + '</a></ul></li>';
                    }
                });
                $('#listNotification').html(notificationHtml);
            }
        });
        getWs(getUrl('messages'), function (res, result) {
            if (result == 'success') {
                $('#countMessage').text(res.length);
                $('#titleMessage').text(MESSAGE_SOURCE.textYouHave.format([res.length]));
                var messageHtml = '';
                res.forEach(function (message) {
                    var date = new Date(message.time);
                    var hours = date.getHours();
                    var minutes = "0" + date.getMinutes();
                    var seconds = "0" + date.getSeconds();
                    var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
                    messageHtml = messageHtml + '<li><a href="' + getUrl('notification/readDetail/' + message.id + '/MESSAGE') + '"><h4>' + message.deviceName + '<small><i' +
                        ' class="fa fa-clock-o"></i>' +
                        formattedTime + '</small></h4><p>' + message.message + '</p></a></li>';
                });
                $('#listMessage').html(messageHtml);
            }
        });

    };
    if (userLoginRole == 4) {
        loadNotificationAndMessage();
        window.setInterval(loadNotificationAndMessage, configNotification);
    }

});