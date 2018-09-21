var Password = {
    _pattern:
        /[a-zA-Z0-9_\!\#\$\%\&\'\(\)\*\+\-\./\:\;\<\=\>\?\@\[\]\^\_\`\{\|\}\~]/,
    _getRandomByte: function () {
        // http://caniuse.com/#feat=getrandomvalues
        if (window.crypto && window.crypto.getRandomValues) {
            var result = new Uint8Array(1);
            window.crypto.getRandomValues(result);
            return result[0];
        }
        else if (window.msCrypto && window.msCrypto.getRandomValues) {
            var result = new Uint8Array(1);
            window.msCrypto.getRandomValues(result);
            return result[0];
        }
        else {
            return Math.floor(Math.random() * 256);
        }
    },
    generate: function (length) {
        return Array.apply(null, {'length': length})
            .map(function () {
                var result;
                while (true) {
                    result = String.fromCharCode(this._getRandomByte());
                    if (this._pattern.test(result)) {
                        return result;
                    }
                }
            }, this)
            .join('');
    }

};
var clipboard =
    {
        data: '',
        intercept: false,
        hook: function (evt) {
            if (clipboard.intercept) {
                evt.preventDefault();
                evt.clipboardData.setData('text/plain', clipboard.data);
                clipboard.intercept = false;
                clipboard.data = '';
            }
        }
    };
window.addEventListener('copy', clipboard.hook);
var generatePassword = function (devicesId) {
    $('#alertMessage').html('');
    var passwordGen = Password.generate(8);
    // Math.random().toString(36);
    $('input[name="password"]').val(passwordGen);
    $('input[name="devicesId"]').val(devicesId);
};
var resetPassword = function () {
    $('#alertMessage').html('');
    var data = {
        deviceId: $('input[name="devicesId"]').val(),
        password: $('input[name="password"]').val()
    };
    clipboard.data = $('input[name="password"]').val();
    if (window.clipboardData) {
        window.clipboardData.setData('Text', clipboard.data);
    }
    else {
        clipboard.intercept = true;
        document.execCommand('copy');
    }
    postWs(getUrl("device/resetPasswordDevices"),
        data,
        function (res, result) {
            if (result == 'success') {
                if (res.length == 0) {
                    $("#dialogResetPassword").modal("hide");
                } else {
                    var messageAll = [];
                    res.forEach(function (message) {
                        // $("#test").alert();
                        messageAll.push(message.content);
                        console.log(message);
                    });
                    BootstrapAlert.alert({
                        title: "Error!",
                        message: messageAll,
                        target: "#alertMessage"
                    });

                }
            }
        });
};
var changeStatusDevice = function (devicesId) {
    postWs(getUrl("device/change/status/" + devicesId), {}, function (res, result) {
        if (result == 'success') {
            if (res.length == 0) {
                window.location.href = getUrl("deviceList");
            } else {
                var messageAll = [];
                res.forEach(function (message) {
                    messageAll.push(message.content);
                    console.log(message);
                });
                BootstrapAlert.alert({
                    title: "Error!",
                    message: messageAll,
                    target: "#alertDevice"
                });

            }
        }
    });
};