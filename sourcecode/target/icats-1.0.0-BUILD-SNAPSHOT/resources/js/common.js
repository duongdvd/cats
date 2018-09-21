var METHOD_GET = "GET";
var METHOD_POST = "POST";

jQuery.fn.extend({
    disabled: function() {
        return this.each(function() {
            switch ($(this).prop("tagName").toLowerCase()) {
                case "a":
                    $(this).addClass("disabled");
                    break;
                default:
                    $(this).prop("disabled", true);
            }
        });
    },
    enabled: function () {
        return this.each(function() {
            switch ($(this).prop("tagName").toLowerCase()) {
                case "a":
                    $(this).removeClass("disabled");
                    break;
                default:
                    $(this).prop("disabled", false);
            }
        });
    },
    check: function () {
        return this.each(function() {
            $(this).prop("checked", true);
        });
    },
    uncheck: function () {
        return this.each(function() {
            $(this).prop("checked", false);
        });
    }
});
jQuery.extend({
    submitForm: function(url, data, method) {
        $form = $("<form>", {
            id: "_customForm",
            method: method || METHOD_GET,
            action: url
        });
        $.each(data, function (name, val) {
            if (Array.isArray(val)) {
                // for bind list in spring controller
                $.each(val, function (i, v) {
                    $form.append($("<input>", {
                        name: name + "[" + i + "]",
                        value: v
                    }));
                });
            } else {
                $form.append($("<input>", {
                    name: name,
                    value: val
                }));
            }
        });
        $("body").append($form);
        $form.submit();
        $("body").remove("#_customForm");
    }

});


$(document).ready(function() {
    /** reset filter */
    $(".reset_filter").on("click", function () {
        var $filterForm = $(this).data("filter_form")? $($(this).data("filter_form")) : $(this).closest('form');
        $filterForm.find("input[type=text], input[type=date], textarea").not(':hidden').val("").change();
        $filterForm.find("select").prop("selectedIndex", 0).change();
    });
    //prevent double click
    $(".btn-submit").one("click", function(event) {
        event.preventDefault();
        $(this).disabled();
        $(this).closest("form").submit();
    });

    /**
     * select/deselect all item in table
     */
    $(".table-select-all").change(function () {
        $(this).closest("table").find("tbody .table-item-select").prop("checked", this.checked);
    });
    $(".table-item-select").change(function () {
        var $table = $(this).closest("table");
        var allItem = $table.find("tbody .table-item-select");
        var isSelectAll = true;
        $.each(allItem, function () {
            if (!this.checked) {
                isSelectAll = false;
                return;
            }
        });
        $table.find(".table-select-all").prop("checked", isSelectAll);
    });
});

function toggleFullScreen(id, hFullDefault, hNormalDefault) {
    var elem = document.getElementById(id);
    // ## The below if statement seems to work better ## if ((document.fullScreenElement && document.fullScreenElement !== null) || (document.msfullscreenElement && document.msfullscreenElement !== null) || (!document.mozFullScreen && !document.webkitIsFullScreen)) {
    if ((document.fullScreenElement !== undefined && document.fullScreenElement === null) || (document.msFullscreenElement !== undefined && document.msFullscreenElement === null) || (document.mozFullScreen !== undefined && !document.mozFullScreen) || (document.webkitIsFullScreen !== undefined && !document.webkitIsFullScreen)) {
        if (elem.requestFullScreen) {
            elem.requestFullScreen();
        } else if (elem.mozRequestFullScreen) {
            elem.mozRequestFullScreen();
        } else if (elem.webkitRequestFullScreen) {
            elem.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
        } else if (elem.msRequestFullscreen) {
            elem.msRequestFullscreen();
        }

        $("#" + id).height(hFullDefault);
    } else {
        if (document.cancelFullScreen) {
            document.cancelFullScreen();
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if (document.webkitCancelFullScreen) {
            document.webkitCancelFullScreen();
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen();
        }
        $("#" + id).height(hNormalDefault);
    }
}

function displayChosenIcon($fileInput, $imgDisplay) {
    if ($fileInput.files && $fileInput.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $imgDisplay.attr('src', e.target.result);
        };
        reader.readAsDataURL($fileInput.files[0]);
    }

    $imgDisplay.show();
}


