$(function () {
    $("#divisionInfo").css("display", "none");
    $("#usersForm").css("display", "none");
    $("#userSelect").css("display", "none");
    var bindDataForm = function (data) {
        $.each(data, function (key, value) {
            if (key == 'divisionStatus') {
                $('input:radio[name="divisionStatus"]').attr('checked', false);
                $('input:radio[name="divisionStatus"]').filter('[value="' + value + '"]').attr('checked', true);
            } else {
                $('#formDivision').find("input[name='" + key + "']").val(value);
            }
        });
    };

    var callback = function (response) {
        var data = response;
        $('#jsTree').jstree({
            'core': {
                "data": data,
                "check_callback": true
            },
            "checkbox": {
                "keep_selected_style": false
            },
            "plugins": ["contextmenu", "search", "types"],
            'contextmenu': {
                'select_node': true,
                'items': reportMenu
            },
            "types" : {
                "default" : {
                    "icon" : "fa fa-fw fa-bookmark-o"
                }
            }
        });

        function reportMenu(node) {
            return {
                createItem: {
                    "label": MESSAGE_SOURCE.btnAddDivisionJs,
                    "icon": "fa fa-fw fa-plus-square-o",
                    "action": function () {
                        console.log(node);
                        var division = {
                            parentDivisionsId: node.id,
                            parentDivisionName: node.text
                        };
                        jQuery("#formDivision").find("input[type=text], textarea, input[type=email]").val("");
                        $('input:radio[name="divisionStatus"]').attr('checked', false);
                        bindDataForm(division);
                        $("#divisionInfo").css("display", "");
                        $("#usersForm").css("display", "");
                        $("#butUpdateDivision").css("display", "none");
                        $("#butAddDivision").css("display", "");
                        $("#userSelect").hide();
                    },
                    "_class": "class"
                },
                deleteItem: {
                    "label": MESSAGE_SOURCE.btnDeleteDivisionJs,
                    "icon": "fa fa-fw fa-trash-o",
                    "action": function () {
                        if (node.children.length == 0) {
                            console.log("delete");
                            postWs(getUrl("deleteDivision/" + node.id), {}, function (data, result) {
                                if (result == 'success') {
                                    if (data.length == 0) {
                                        location.href = getUrl("divisionList");
                                    } else {
                                        var messageAll = [];
                                        data.forEach(function (message) {
                                            messageAll.push(message.content);
                                        });
                                        BootstrapAlert.alert({
                                            title: "Error!",
                                            message: messageAll,
                                            target: "#alertDeleteDivision"
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    };
    getWs(getUrl('division'), callback);
    $("#s").submit(function (e) {
        e.preventDefault();
        $("#jsTree").jstree(true).search($("#q").val());
    });

    $("#butAddDivision").click(function (e) {
        $("#messageList").empty();
        var division = getDataFromForm(false);
        console.log(division);
        postWs(getUrl('addDivision'), division, function (res, status) {
            if (status == 'success') {
                window.location.href = getUrl('divisionList');
            } else {
                console.log(res.responseJSON);
                res.responseJSON.forEach(function (error) {
                    BootstrapAlert.alert({
                        message: error.content,
                        target: "#messageList"
                    });
                });
            }
        })
    });

    $("#butUpdateDivision").click(function (e) {
        $("#messageList").empty();
        var division = getDataFromForm(true);
        console.log(division);
        $(this).addClass("disabled");
        postWs(getUrl('editDivision'), division, function (res, status) {
            if (status == 'success') {
                window.location.href = getUrl('divisionList');
            } else {
                console.log(res.responseJSON);
                res.responseJSON.forEach(function (error) {
                    BootstrapAlert.alert({
                        message: error.content,
                        target: "#messageList"
                    });
                });
            }
        }).always(function () {
            $(this).removeClass("disabled");
        })
    });

    var getDataFromForm = function (modeEdit) {
        var division = {};
        division.divisionName = $('#formDivision').find("input[name='divisionName']").val();
        division.divisionAddress = $('#formDivision').find("input[name='divisionAddress']").val();
        division.parentDivisionsId = $('#formDivision').find("input[name='parentDivisionsId']").val();
        division.parentDivisionName = $('#formDivision').find("input[name='parentDivisionName']").val();
        division.divisionStatus = $('#formDivision').find("input[name='divisionStatus']:checked").val();
        if (modeEdit) {
            division.id = $('#formDivision').find("input[name='id']").val();
            division.usersId = $("#usersId option:selected").val();
        } else {
            division.loginId = $('#formDivision').find("input[name='loginId']").val();
            division.userEmail = $('#formDivision').find("input[name='userEmail']").val();
            division.firstName = $('#formDivision').find("input[name='firstName']").val();
            division.lastName = $('#formDivision').find("input[name='lastName']").val();
            division.fixedPhone = $('#formDivision').find("input[name='fixedPhone']").val();
            division.mobilePhone = $('#formDivision').find("input[name='mobilePhone']").val();
            division.faxNumber = $('#formDivision').find("input[name='faxNumber']").val();
        }
        division.modeEdit = modeEdit;
        return division;
    }

    function clickDivision() {
        $("#jsTree").on("select_node.jstree", function (event, nodeTree) {
            console.log(nodeTree);
            var node = nodeTree.node;
            $("#divisionInfo").css("display", "");
            $("#usersForm").css("display", "none");
            $("#butUpdateDivision").css("display", "");
            $("#butAddDivision").css("display", "none");
            $("#userSelect").show();
            var division = {};
            getWs(getUrl('division/' + node.id), function (res) {
                division = res;
                bindDataForm(division);
                $("#usersId option[value=" + division.usersId + "]").prop('selected', true);
            });
            getWs(getUrl('division/' + node.id + '/users'), function (res) {
                var userList = res;
                var listUser = '';
                userList.forEach(function (user) {
                    listUser = listUser + '<option value="' + user.id + '">' + user.loginId + '</option>';
                });
                $("#usersId").html(listUser);
                if (division.usersId) {
                    $("#usersId option[value=" + division.usersId + "]").prop('selected', true);
                }
            })
        });
    }

    clickDivision();
});

