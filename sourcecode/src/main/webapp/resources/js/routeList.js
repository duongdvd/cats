var changeStatusRoute = function (routeId) {
    postWs(getUrl("route/change/status/" + routeId), {}, function (res, result) {
        if (result == 'success') {
            if (res.length == 0) {
                window.location.href = getUrl("routeList");
            } else {
                var messageAll = [];
                res.forEach(function (message) {
                    messageAll.push(message.content);
                    console.log(message);
                });
                BootstrapAlert.alert({
                    title: "Error!",
                    message: messageAll,
                    target: "#alertRoute"
                });

            }
        }
    });
};