
/**
 * jQuery 初期表示時処理
 */
$(document).ready(function()
{
	// タブ選択
	$('li.tab-clients').addClass('selected');

	// 検索
	$('#search').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		//$(this).parents('form').attr('enctype', "application/x-www-form-urlencoded");
		$(this).parents('form').submit();
	});

	// エクスポート
	$('#export').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		//$(this).parents('form').attr('enctype', "application/x-www-form-urlencoded");
		$(this).parents('form').submit();
	});

	// インポート
	$('#import').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		//$(this).parents('form').attr('enctype', "multipart/form-data");
		$(this).parents('form').submit();
	});

	// 登録
	$('#regist').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');

		if (window.confirm('顧客情報を登録します。よろしいですか？'))
		{
			$(this).parents('form').attr('action', action);
			//$(this).parents('form').attr('enctype', "application/x-www-form-urlencoded");
			//$(this).parents('form').submit();
			return true;
		}

		return false;
	});

	// モーダルウィンドウ閉じる
	$('.close,.modalBack').click(function(){
		$(wn).fadeOut(500);
	});

	// 新規行追加
	$('#addNew').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		$(this).parents('form').submit();
	});

	// 変更処理
	$("input[name^='clientList'][name$='name']").change(function() {
		changeData(this);
	});
	$("input[name^='clientList'][name$='address']").change(function() {
		changeData(this);
	});
	$("input[name^='clientList'][name$='lat']").change(function() {
		changeData(this);
	});
	$("input[name^='clientList'][name$='lng']").change(function() {
		changeData(this);
	});

	// 変更フラグ
	$("input[name^='clientList'][name$='changeFlg']").change(function() {
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

/**
 * CSVインポートモーダルウィンドウ表示
 */
function csvImportModal(sender) {
	wn = '.' + $(sender).data('target');
	var mW = $(wn).find('.modalBody').innerWidth() / 2;
	var mH = $(wn).find('.modalBody').innerHeight() / 2;
	$(wn).find('.modalBody').css({'margin-left':-mW,'margin-top':-mH});
	$(wn).fadeIn(500);
	$('.modalBack').fadeIn(500);
}

/**
 * 座標取得
 * @param sender
 * @param bulk
 */
function getLatLng(sender, bulk)
{
	var name = $(sender).attr("name");
	var $address = $("[name='" + name.substring(0, name.lastIndexOf(".") + 1) + "address']");
	var $lat = $("[name='" + name.substring(0, name.lastIndexOf(".") + 1) + "lat']");
	var $lng = $("[name='" + name.substring(0, name.lastIndexOf(".") + 1) + "lng']");

	if ($address.val())
	{
		ZDC.Search.getByZmaps('address/word', {
			word: $address.val()
//	        searchType: 0
	    },function(status, res) {
            if (status.code == '200') {
                /* 取得成功 */
            	if (res.item.length === 0) {
                    alert("座標が見つかりませんでした。");
                } else {
                	var results = res.item;
                    
                    var latlng = results[0].point;
    				var latValue = floatFormat(latlng.lat, 7);
    				var lngValue = floatFormat(latlng.lon, 7);
    				if ($lat.val() != latValue || $lng.val() != lngValue)
    				{
    					$lat.val(floatFormat(latlng.lat, 7));
    					$lng.val(floatFormat(latlng.lon, 7));
    					changeData(sender);
    				}
                }
            } else {
                /* 取得失敗 */
                alert("座標の取得に失敗しました。");
            }
        });
//		var geocoder = new google.maps.Geocoder();
//		geocoder.geocode({address : $address.val()}, function(results, status) {
//			if (status == google.maps.GeocoderStatus.OK)
//			{
//				var bounds = new google.maps.LatLngBounds();
//				for (var i in results)
//				{
//					if (i == 0)
//					{
//						var latlng = results[i].geometry.location;
//						var latValue = floatFormat(latlng.lat(), 7);
//						var lngValue = floatFormat(latlng.lng(), 7);
//						if ($lat.val() != latValue || lng.val() != lngValue)
//						{
//							$lat.val(floatFormat(latlng.lat(), 7));
//							$lng.val(floatFormat(latlng.lng(), 7));
//							changeData(sender);
//						}
//					}
//				}
//			}
//			else if (status == google.maps.GeocoderStatus.ZERO_RESULTS)
//			{
//				if (!bulk)
//				{
//					alert("座標が見つかりませんでした。");
//				}
//			}
//			else
//			{
//				alert("座標の取得に失敗しました。");
//			}
//		});
	}
	else
	{
		if (!bulk)
		{
			alert("住所を入力してください。");
		}
	}
}

/**
 * 小数点num位までを残して四捨五入
 * @param value
 * @param num
 * @returns {Number}
 */
function floatFormat(value, num)
{
	var result = value;
	if ($.isNumeric(value))
	{
		value = Number(value);
		var _pow = Math.pow(10, num);
		result = Math.round(value * _pow) / _pow;
	}

	return result;
}

