
/**
 * jQuery 初期表示時処理
 */
$(document).ready(function()
{
	// タブ選択
	$('li.tab-house').addClass('selected');

	// 登録
	$('#regist').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');

		if (window.confirm('自社情報を登録します。よろしいですか？'))
		{
			$(this).parents('form').attr('action', action);
			//$(this).parents('form').submit();
			return true;
		}

		return false;
	});

	// 二重送信防止
	$('form').on('.submit', function () {
		$(this).find('input:submit').attr('disabled', 'disabled').val('送信中...');
	});

});

/**
 * 座標取得
 */
function getLatLng()
{
	var name = $("[name]").attr("name");
	var $address = $("[name='house.address']");
	var $lat = $("[name='house.lat']");
	var $lng = $("[name='house.lng']");

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
                    $lat.val(floatFormat(latlng.lat(), 7));
					$lng.val(floatFormat(latlng.lng(), 7));
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
//						$lat.val(floatFormat(latlng.lat(), 7));
//						$lng.val(floatFormat(latlng.lng(), 7));
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

