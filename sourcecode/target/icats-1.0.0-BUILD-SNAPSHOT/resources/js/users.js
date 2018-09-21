
/**
 * jQuery 初期表示時処理
 */
$(document).ready(function()
{
    // タブ選択
    $('li.tab-cars').addClass('selected');

    // 検索
    $('#search').click(function () {
        var action = $(this).data('action');
        console.log('submit click action=[' + action + ']');
        $(this).parents('form').attr('action', action);
        $(this).parents('form').submit();
        return true;
    });

    // 登録
    $('#regist').click(function () {
        var action = $(this).data('action');
        console.log('submit click action=[' + action + ']');

        if (window.confirm('車両情報を登録します。よろしいですか？')) {
            $(this).parents('form').attr('action', action);
            //$(this).parents('form').submit();	// サブミットはボタン押下と連動しなければHTML5の検証機能が使えない
            return true;
        }

        return false;
    });
    //
    var valid = false;

    $('#addNew').click(function (e) {
        if (!validInsertloginId || !validInsertPass || !validInsertName || !validInsertDivisionsId || !validInsertUpdateBy || !validInsertCreateBy) {
            e.preventDefault();
            return;
        }
        var action = $(this).data('action');
        console.log('submit click action=[' + action + ']');
        $(this).parents('form').attr('action', action);
        $(this).parents('form').submit();
    });

    //
    $("#edit-user").on('show.bs.modal', function (event, m) {
        $(this).find('[name=id]').val(event.relatedTarget.dataset.id);
        $(this).find('[name=loginId]').val(event.relatedTarget.dataset.loginId);
        $(this).find('[name=name]').val(event.relatedTarget.dataset.name);
        $(this).find('[name=divisionsId]').val(event.relatedTarget.dataset.divisionsId);
        $(this).find('[name=updateBy]').val(event.relatedTarget.dataset.updateBy);
    });

    function writeTxtBox(strVal) {

        var queryTxt = new String();

        if (strVal != "1") {

            queryTxt = strVal + " ";

            document.myForm.txtBox.value += queryTxt;
            document.myForm.selectList.options[0].selected = true;

        } // if

    }// writeTextArea()

    //
    $('.update').click(function (e) {
        if (!validEditName || !validEditLoginId || !validEditDivisionsId || !validEditUpdateBy) {
            e.preventDefault();
            return;
        }
        var action = $(this).data('action');
        console.log('submit click action=[' + action + ']');
        $(this).parents('form').attr('action', action);
        $(this).parents('form').submit();
    });

    //
    $('.delete').click(function () {
        var action = $(this).data('action');
        console.log('submit click action=[' + action + ']');
        if (window.confirm('車両情報を登録します。よろしいですか？')) {
            $(this).parents('form').attr('action', action);
            $(this).parents('form').submit();
            return true;
        }

        return false;
    });

    // 変更処理
    $("input[name^='usersList'][name$='userName']").change(function () {
        changeData(this);
    });

    // 変更フラグ
    $("input[name^='usersList'][name$='changeFlg']").change(function () {
        changeStatus(this);
    });

    // 二重送信防止
    $('form').on('.submit', function () {
        $(this).find('input:submit').attr('disabled', 'disabled').val('送信中...');
    });

    //
    var validInsertloginId = false;

    $('#modal-user input[name=loginId]').on('focusout', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 200) {
            $('.vld-user').text("1-200文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertloginId = false;
            return;
        }

        var filler = /^((?=[a-zA-Z0-9\!#\$%&'\(\)\*\+\-\.\/:;<=>\?@\[\]\^_`{\|}\~]).)*$/;

        if (!filler.test(userName)) {
            $('.vld-user').text("CONTAINS CHARACTERS NOT ALLOWED");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertloginId = false;
            return;
        }


        $('.vld-user').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validInsertloginId = true;
    });

    //
    var validInsertPass = false;

    $('#modal-user input[name=passwd]').on('focusout', function () {
        var userName = $(this).val();
        if (userName.length < 8 || userName.length > 200) {
            $('.vld-pass').text("8-200文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertPass = false;
            return;
        }

        var filler = /^((?=[a-zA-Z0-9\!#\$%&'\(\)\*\+\-\.\/:;<=>\?@\[\]\^_`{\|}\~]).)*$/;

        if (!filler.test(userName)) {
            $('.vld-pass').text("CONTAINS CHARACTERS NOT ALLOWED");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertPass = false;
            return;
        }


        $('.vld-pass').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validInsertPass = true;
    });

    //
    var validInsertName = false;

    $('#modal-user input[name=name]').on('focusout', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 200) {
            $('.vld-name').text("1-200文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertName = false;
            return;
        }

        $('.vld-name').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validInsertName = true;
    });

    //
    var validInsertDivisionsId = false;

    $('#modal-user input[name=divisionsId]').on('focusout', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 10) {
            $('.vld-division').text("1-10文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertDivisionsId = false;
            return;
        }

        var filler = /^[0-9]+$/;

        if (!filler.test(userName)) {
            $('.vld-division').text("CONTAINS CHARACTERS NOT ALLOWED (MUST INPUT NUMBER)");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertDivisionsId = false;
            return;
        }

        $('.vld-division').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validInsertDivisionsId = true;
    });

    //
    var validInsertUpdateBy = false;

    $('#modal-user input[name=updateBy]').on('focusout', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 10) {
            $('.vld-update-by').text("1-10文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertUpdateBy = false;
            return;
        }

        var filler = /^[0-9]+$/;

        if (!filler.test(userName)) {
            $('.vld-update-by').text("CONTAINS CHARACTERS NOT ALLOWED (MUST INPUT NUMBER)");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertUpdateBy = false;
            return;
        }

        $('.vld-update-by').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validInsertUpdateBy = true;
    });

    //
    var validInsertCreateBy = false;

    $('#modal-user input[name=createBy]').on('focusout', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 10) {
            $('.vld-create-by').text("1-10文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertCreateBy = false;
            return;
        }

        var filler = /^[0-9]+$/;

        if (!filler.test(userName)) {
            $('.vld-create-by').text("CONTAINS CHARACTERS NOT ALLOWED (MUST INPUT NUMBER)");
            $(this).closest('div.form-group').addClass('has-error');
            validInsertCreateBy = false;
            return;
        }

        $('.vld-create-by').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validInsertCreateBy = true;
    });

    //
    var validEditLoginId = true;

    $('.edit input[name*=loginId]').on('input', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 200) {
            $('.vld-user').text("1-200文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            c;
            return;
        }

        var filler = /^((?=[a-zA-Z0-9\!#\$%&'\(\)\*\+\-\.\/:;<=>\?@\[\]\^_`{\|}\~]).)*$/;

        if (!filler.test(userName)) {
            $('.vld-user').text("CONTAINS CHARACTERS NOT ALLOWED");
            $(this).closest('div.form-group').addClass('has-error');
            validEditLoginId = false;
            return;
        }


        $('.vld-user').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validEditLoginId = true;
    });

    //
    var validEditName = true;

    $('.edit input[name*=name]').on('input', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 200) {
            $('.vld-name').text("1-200文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validEditName = false;
            return;
        }

        $('.vld-name').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validEditName = true;
    });
    //
    var validEditDivisionsId = true;

    $('.edit input[name*=divisionsId]').on('input', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 10) {
            $('.vld-division').text("1-10文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validEditDivisionsId = false;
            return;
        }

        var filler = /^[0-9]+$/;

        if (!filler.test(userName)) {
            $('.vld-division').text("CONTAINS CHARACTERS NOT ALLOWED (MUST INPUT NUMBER)");
            $(this).closest('div.form-group').addClass('has-error');
            validEditDivisionsId = false;
            return;
        }

        $('.vld-division').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validEditDivisionsId = true;
    });

    //
    var validEditUpdateBy = true;

    $('.edit input[name*=updateBy]').on('input', function () {
        var userName = $(this).val();
        if (userName.length < 1 || userName.length > 10) {
            $('.vld-update-by').text("1-10文字　1byte文字　可変長!");
            $(this).closest('div.form-group').addClass('has-error');
            validEditUpdateBy = false;
            return;
        }

        var filler = /^[0-9]+$/;

        if (!filler.test(userName)) {
            $('.vld-update-by').text("CONTAINS CHARACTERS NOT ALLOWED (MUST INPUT NUMBER)");
            $(this).closest('div.form-group').addClass('has-error');
            validEditUpdateBy = false;
            return;
        }

        $('.vld-update-by').text("");
        $(this).closest('div.form-group').removeClass('has-error');
        validEditUpdateBy = true;
    });


});

/**
 * データ変更処理
 * @param $sender
 */
function changeData(sender)
{
    var name = $(sender).attr("name");
    var changeFlgName = "[name='" + name.substring(0, name.lastIndexOf(".") + 1) + "changeFlg']";
    var value = $(changeFlgName).val();
    if (value == "2") {
        $(changeFlgName).val("3");
    }
}

/**
 * ステータス変更処理
 * @param sender
 */
function changeStatus(sender)
{
    var name = $(sender).attr("name");
    var changeFlgName = "[name='" + name.substring(0, name.lastIndexOf(".") + 1) + "changeFlg']";
    var value = $(changeFlgName).val();
    if (value == "5") {
        var $curTr = $(sender).closest('tr')[0];
        if ($curTr) {
            $curTr.remove();
            var newNo = $("#listCount").val();
            $("#listCount").val(--newNo);
        }
    }
}

