/*!
 * BootstrapAlert v1.0
 * Copyright MantaCode Sp. z o.o.
 * Licensed under MIT
 * requires: jQuery, Bootstrap
 */
(function (root, factory) {
    'use strict';
    if (typeof define === "function" && define.amd) {
        define("bootstrapAlert", ["jquery"], function (a0) {
            return root["BootstrapAlert"] = factory(a0);
        });
    } else if (typeof exports === "object") {
        module.exports = factory(require("jquery"));
    } else {
        root["BootstrapAlert"] = factory(jQuery);
    }
})(this, function ($) {
    'use strict';

    function createBootstrapAlert() {
        var BootstrapAlert = {};

        function showAlert(cssClass, userConfig) {
            var config = {
                autoHide: false,
                hideTimeout: 3000,
                dissmissible: true,
                innerClass: 'bootstrap-alert-message',
                title: '',
                message: [],
                target: 'body'
            };
            $.extend(config, userConfig);

            if ($.isArray(config.message)) {
                config.message.forEach(function (value) {
                    createAlert(config, cssClass, value);
                })
            } else {
                createAlert(config, cssClass, config.message);
            }

        }
        function createAlert(config, cssClass, content) {
            var contentStyle = [].join('');

            var alertContent = document.createElement('div');
            alertContent.className = 'alert ' + config.innerClass + ' ' + cssClass;
            alertContent.setAttribute('role', 'alert');
            alertContent.style = contentStyle;
            if (config.dissmissible) {
                alertContent.innerHTML += [
                    '<button type="button" class="close" data-dismiss="alert">',
                    '<span aria-hidden="true">&times;</span>',
                    '</button>'
                ].join('');
            }
            alertContent.innerHTML += '<strong>' + config.title + '</strong> ' + content;
            $(config.target).append(alertContent);
        }

        BootstrapAlert.alert = function (config) {
            showAlert('alert-danger', config);
        };

        BootstrapAlert.info = function (config) {
            showAlert('alert-info', config);
        };

        BootstrapAlert.warning = function (config) {
            showAlert('alert-warning', config);
        };

        BootstrapAlert.success = function (config) {
            showAlert('alert-success', config);
        };

        return BootstrapAlert;
    }

    return createBootstrapAlert();
});
