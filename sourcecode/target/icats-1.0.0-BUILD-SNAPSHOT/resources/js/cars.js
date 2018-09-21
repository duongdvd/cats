
/**
 * jQuery 初期表示時処理
 */
$(document).ready(function()
{
	// タブ選択
	$('li.tab-cars').addClass('selected');

	// 検索
	$('#search').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		$(this).parents('form').submit();
		return true;
	});

	// 登録
	$('#regist').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');

		if (window.confirm('車両情報を登録します。よろしいですか？'))
		{
			$(this).parents('form').attr('action', action);
			//$(this).parents('form').submit();	// サブミットはボタン押下と連動しなければHTML5の検証機能が使えない
			return true;
		}

		return false;
	});

	// 新規行追加
	$('#addNew').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		$(this).parents('form').submit();
	});

	// 変更処理
	$("input[name^='carList'][name$='carName']").change(function() {
		changeData(this);
	});

	// 変更フラグ
	$("input[name^='carList'][name$='changeFlg']").change(function() {
		changeStatus(this);
	});

	// 二重送信防止
	$('form').on('.submit', function () {
		$(this).find('input:submit').attr('disabled', 'disabled').val('送信中...');
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
	if (value == "2")
	{
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
	if (value == "5")
	{
		var $curTr = $(sender).closest('tr')[0];
		if ($curTr)
		{
			$curTr.remove();
			var newNo = $("#listCount").val();
			$("#listCount").val(--newNo);
		}
	}
}

