
/**
 * 表示中モーダルウィンドウ
 */
var wn = '';

/**
 * jQuery 初期表示時処理
 */
$(document).ready(function()
{
	// タブ選択
	$('li.tab-instruction').addClass('selected');

	// orderDt change
	$('#orderDt').change(function() {
		hideInputList();
		getOrderNoList();
	});

	// carNo change
	$('#carNo').change(function() {
		hideInputList();
		getOrderNoList();
	});

	// orderNo change
	$('#orderNo').change(function() {
		hideInputList();
	});

	// 検索
	$('#search').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');
		$(this).parents('form').attr('action', action);
		return true;
	});

	// 登録
	$('#regist').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');

		if (window.confirm('ルート情報を登録します。よろしいですか？'))
		{
			$(this).parents('form').attr('action', action);
			return true;
		}

		return false;
	});

	// 削除
	$('#delete').click(function() {
		var action = $(this).data('action');
		console.log('submit click action=[' + action + ']');

		if (window.confirm('ルート情報を削除します。よろしいですか？'))
		{
			$(this).parents('form').attr('action', action);
			return true;
		}

		return false;
	});

	// 本日日付設定
	$('.set-today').click(function() {
		var dateTarget = '#' + $(this).data('target');
		var date = new Date();
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		var yyyy = toTargetDigits(year, 4);
		var mm = toTargetDigits(month, 2);
		var dd = toTargetDigits(day, 2);
		var today = yyyy + '-' + mm + '-' + dd;
		if ($(dateTarget).val() != today)
		{
			$(dateTarget).val(today).change();
		}

	});

	// ルート確認
	$('#routeConf').click(function() {
		wn = '.' + $(this).data('target');
		var mW = $(wn).find('.modalBody').innerWidth() / 2;
		var mH = $(wn).find('.modalBody').innerHeight() / 2;
		$(wn).find('.modalBody').css({'margin-left':-mW,'margin-top':-mH});
		$(wn).fadeIn(500);
		$('.modalBack').fadeIn(500);
		mapLoad();
	});
	$('.close,.modalBack').click(function(){
		$(wn).fadeOut(500);
	});

	// 二重送信防止
	$('form').on('.submit', function () {
		$(this).find('input:submit').attr('disabled', 'disabled').val('送信中...');
	});

});

/**
 * 一覧非表示
 */
function hideInputList() {
	$("#regist").prop("disabled", true);
	$("#routeConf").prop("disabled", true);
	$('#listInput').hide();
}

/* map */

/** マップ */
var map = null;

/** マーカー */
var markers = [];

/**
 * マップ読み込み処理
 */
function mapLoad()
{
	// マップ初期化処理
	mapInit();

    // マップ情報表示処理
    dispMap();
}

/**
 * マップ初期化処理
 */
function mapInit() {

	// キャンパスの要素を取得する
	var canvas = document.getElementById('route-map-canvas');

	// [canvas]に、[mapOptions]の内容の、地図のインスタンス([map])を作成する
	map = new ZDC.Map(canvas,{
		zoom: 9,
        mapType: ZDC.MAPTYPE_HIGHRES_LV18
    });
	
	/* スケールバーを作成 */
    map.addWidget(new ZDC.ScaleBar());
    map.addWidget(new ZDC.Control());
}

/**
 * マップ情報表示処理
 * @param data
 */
function dispMap()
{
	// マーカー削除
	markers.forEach(function(marker, index)
	{
//		marker.setMap(null);
		map.removeWidget(marker);
	});

	// ズーム
//	map.setZoom(15);

	// 中心位置
	var latlng = new ZDC.LatLon($("input[name='house.lat']").val(), $("input[name='house.lng']").val());
	map.moveLatLon(latlng);

	// ルート情報
	var $clientNoList = $("input[name^='routeList'][name$='clientNo']");
	var $nameList = $("input[name^='routeList'][name$='name']");
	var $latList = $("input[name^='routeList'][name$='lat']");
	var $lngList = $("input[name^='routeList'][name$='lng']");
	var $infoList = $("input[name^='routeList'][name$='info']");
	var $detailNoList = $("input[name^='routeList'][name$='detailNo']");
	var $finishFlgList = $("input[name^='routeList'][name$='finishFlg']");
	var len = $clientNoList.length;

	// 順序配列
	var startDetailNo = 999;
	var finishDetailNo = 0;
	var orderIndexArray = [];
	for (var i = 0; i < len ; i++)
	{
		var orderValue = $detailNoList.eq(i).val();
		if (orderValue) {
			orderIndexArray.push({"index" : i, "orderValue" : orderValue});

			// 最大値
			if (finishDetailNo < orderValue) {
				finishDetailNo = orderValue;
			}

			// 最小値
			if (orderValue < startDetailNo) {
				startDetailNo = orderValue;
			}
		}
	}

	// 順序配列をソート
	orderIndexArray.sort(
		function (a, b)
		{
			if (a['orderValue'] < b['orderValue'])
			{
				return -1;
			}
			if (a['orderValue'] > b['orderValue'])
			{
				return 1;
			}
			return 0;
		}
	);

	var routeArray = [];
	var startPoint = latlng;
	var endPoint = latlng;
	var minLat = latlng.lat;
	var minLng = latlng.lon;
	var maxLat = latlng.lat;
	var maxLng = latlng.lon;
	for (var i = 0; i < orderIndexArray.length ; i++)
	{
		var index = orderIndexArray[i]['index'];
		var point = new ZDC.LatLon($latList.eq(index).val(), $lngList.eq(index).val());
		endPoint = point;
		var finishFlg = $finishFlgList.get(index).checked;

		// マーカー作成
		if (startDetailNo == finishDetailNo && $detailNoList.eq(index).val() == startDetailNo) {
			// １箇所しかない場合
//			var marker = new google.maps.Marker({
//				position:	point,
//				map:		map,
//				icon: {
//					fillColor: "#000000",                // 塗り潰し色
//					fillOpacity: 0.8,                    // 塗り潰し透過率
//					path: google.maps.SymbolPath.CIRCLE, // 円を指定
//					scale: 16,                           // 円のサイズ
//					strokeColor: "#000000",              // 枠の色
//					strokeWeight: 1.0                    // 枠の透過率
//				},
//				label: {
//					text: '発着',                         // ラベル文字
//					color: '#FFFFFF',                    // 文字の色
//					fontSize: '12px'                     // 文字のサイズ
//				}
//			});
			/* マーカを作成 */
			var marker = new ZDC.Marker(
				point,{
	                color: ZDC.MARKER_COLOR_ID_BLUE_S,
	                number: ZDC.MARKER_NUMBER_ID_STAR_S
	            }
	        );

	        /* マーカを追加 */
	        map.addWidget(marker);
	        
	        /* マウスオーバー時に表示するタイトルを追加 */
	        marker.setTitle('発着');
			
			// メッセージ付加
			attachMessageCar(marker, point,
				'<div class="message">発着点<br/>' +
				$nameList.eq(index).val() + '</div>');
		} else if ($detailNoList.eq(index).val() == startDetailNo) {
			// 出発点
//			var marker = new google.maps.Marker({
//				position:	point,
//				map:		map,
//				icon: {
//					fillColor: "#000000",                // 塗り潰し色
//					fillOpacity: 0.8,                    // 塗り潰し透過率
//					path: google.maps.SymbolPath.CIRCLE, // 円を指定
//					scale: 16,                           // 円のサイズ
//					strokeColor: "#000000",              // 枠の色
//					strokeWeight: 1.0                    // 枠の透過率
//				},
//				label: {
//					text: '出発',                         // ラベル文字
//					color: '#FFFFFF',                    // 文字の色
//					fontSize: '12px'                     // 文字のサイズ
//				}
//			});
			
			/* マーカを作成 */
			var marker = new ZDC.Marker(
				point,{
	                color: ZDC.MARKER_COLOR_ID_BLUE_S,
	                number: ZDC.MARKER_NUMBER_ID_STAR_S
	            }
	        );

	        /* マーカを追加 */
	        map.addWidget(marker);
	        
	        /* マウスオーバー時に表示するタイトルを追加 */
	        marker.setTitle('出発');
			
			// メッセージ付加
			attachMessageCar(marker, point,
				'<div class="message">出発<br/>' +
				$nameList.eq(index).val() + '</div>');
		} else if ($detailNoList.eq(index).val() == finishDetailNo) {
			// 到着点
//			var marker = new google.maps.Marker({
//				position:	point,
//				map:		map,
//				icon: {
//					fillColor: "#000000",                // 塗り潰し色
//					fillOpacity: 0.8,                    // 塗り潰し透過率
//					path: google.maps.SymbolPath.CIRCLE, // 円を指定
//					scale: 16,                           // 円のサイズ
//					strokeColor: "#000000",              // 枠の色
//					strokeWeight: 1.0                    // 枠の透過率
//				},
//				label: {
//					text: '到着',                         // ラベル文字
//					color: '#FFFFFF',                    // 文字の色
//					fontSize: '12px'                     // 文字のサイズ
//				}
//			});
			
			/* マーカを作成 */
			var marker = new ZDC.Marker(
				point,{
	                color: ZDC.MARKER_COLOR_ID_BLUE_S,
	                number: ZDC.MARKER_NUMBER_ID_STAR_S
	            }
	        );

	        /* マーカを追加 */
	        map.addWidget(marker);
	        
	        /* マウスオーバー時に表示するタイトルを追加 */
	        marker.setTitle('到着');
			
			// メッセージ付加
			attachMessageCar(marker, point,
				'<div class="message">到着<br/>' +
				$nameList.eq(index).val() + '</div>');
		} else {
			// 目的地
//			var marker = new google.maps.Marker({
//				position:	point,
//				map:		map,
//				icon: {
//					fillColor: "#0000FF",                // 塗り潰し色
//					fillOpacity: 0.8,                    // 塗り潰し透過率
//					path: google.maps.SymbolPath.CIRCLE, // 円を指定
//					scale: 13,                           // 円のサイズ
//					strokeColor: "#0000FF",              // 枠の色
//					strokeWeight: 1.0                    // 枠の透過率
//				},
//				label: {
//					text: $detailNoList.eq(index).val(), // ラベル文字
//					color: '#FFFFFF',                    // 文字の色
//					fontSize: '12px'                     // 文字のサイズ
//				}
//			});
			
			/* マーカを作成 */
			var marker = new ZDC.Marker(
				point,{
	                color: ZDC.MARKER_COLOR_ID_BLUE_S,
	                number: ZDC.MARKER_NUMBER_ID_STAR_S
	            }
	        );

	        /* マーカを追加 */
	        map.addWidget(marker);
	        
	        /* マウスオーバー時に表示するタイトルを追加 */
	        marker.setTitle($detailNoList.eq(index).val());
			
		}

		// メッセージ付加
		attachMessageCar(marker, point,
			'<div class="message">訪問順：' + $detailNoList.eq(index).val() + '<br/>' +
			$nameList.eq(index).val() + '<br/>' +
			$infoList.eq(index).val() + '</div>');
		
		// 地図表示領域をマーカー位置に合わせて拡大します。
		if (marker.getLatLon().lat < minLat)
		{
			minLat = marker.getLatLon().lat;
		}
		if (marker.getLatLon().lon < minLng)
		{
			minLng = marker.getLatLon().lon;
		}
		if (maxLat < marker.getLatLon().lat)
		{
			maxLat = marker.getLatLon().lat;
		}
		if (maxLng < marker.getLatLon().lon)
		{
			maxLng = marker.getLatLon().lon;
		}

		// ルートの始点と終点を保存
		if (0 < i) {
			routeArray.push({"start" : startPoint, "end" : endPoint});
		}

		startPoint = endPoint;
		markers.push(marker);
	}

	// ルート表示
	for (var i in routeArray)
	{
		displayRoute(routeArray[i]['start'], routeArray[i]['end']);
	}

	// 地図表示領域の変更を反映
//	if (minLat != maxLat || minLng != maxLng)
//	{
//		var minPoint = new ZDC.LatLon(minLat, minLng);
//		var maxPoint = new ZDC.LatLon(maxLat, maxLng);
//		var latlngBounds = new google.maps.LatLngBounds(minPoint, maxPoint);
//		map.fitBounds(latlngBounds);
//	}
}

/**
 * ルート表示
 * @param start
 * @param end
 */
function displayRoute(start, end)
{
	// ルート表示設定
//	var rendererOptions = {
//		draggable : false,
//		preserveViewport : true,
//		suppressMarkers : true,
//		suppressInfoWindows : true
//	}
//
//	// ルートを表示するマップを設定
//	var directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
//	var directionsService = new google.maps.DirectionsService();
//
//	// ルート間 ポイント設定
//	var request = {
//		origin : start,
//		destination : end,
//		travelMode : google.maps.DirectionsTravelMode.DRIVING,
//		unitSystem : google.maps.DirectionsUnitSystem.METRIC,
//		optimizeWaypoints : true,
//		avoidHighways : true,
//		avoidTolls : true
//	};
//	directionsService.route(request, function(response, status)
//	{
//		if (status == google.maps.DirectionsStatus.OK)
//		{
//			directionsDisplay.setDirections(response);
//		}
//	});
//
//	directionsDisplay.setMap(map);

    /* 歩行者ルート探索を実行 */
    ZDC.Search.getByZmaps('route3/drive', {
        from: start.lat + ',' + start.lon,
        to: end.lat + ',' + end.lon,
        searchType: 0
    },function(status, res) {
        if (status.code == '200') {
            /* 取得成功 */
        	writeRoute(res);
        } else {
            /* 取得失敗 */
            alert(status.text);
        }
    });

}

function writeRoute(res) {
    if (res.route === null) {
        alert('ルートが見つかりません');
    }

    var line_property = {
            '高速道路': {strokeColor: '#3000ff', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '都市高速道路': {strokeColor: '#008E00', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '国道': {strokeColor: '#007777', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '主要地方道': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '都道府県道': {strokeColor: '#008800', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '一般道路(幹線)': {strokeColor: '#000088', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '一般道路(その他)': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '導入路': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            '細街路(主要)': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'},
            'フェリー航路': {},
            '道路外': {strokeColor: '#880000', strokeWeight: 5, lineOpacity: 0.5, lineStyle: 'solid'}
        };

    var link = res.route.link;

    var latlons = [];

    for (var i=0, j=link.length; i<j; i++) {

        var opt = line_property[link[i].roadType];
        var pllatlons =[];

        for (var k=0, l=link[i].line.latlon.length; k<l; k+=2) {
            pllatlons.push(new ZDC.LatLon(link[i].line.latlon[k],link[i].line.latlon[k+1]));

            /* getAdjustZoom用に全地点の緯度経度を取得 */
            latlons.push(new ZDC.LatLon(link[i].line.latlon[k],link[i].line.latlon[k+1]));

        }
        var pl = new ZDC.Polyline(pllatlons, opt);
        map.addWidget(pl);
    }
    /* 地点が全て表示できる縮尺レベルを取得 */
    var adjust = map.getAdjustZoom(latlons);
    map.moveLatLon(adjust.latlon);
    map.setZoom(adjust.zoom);
};

/**
 * メッセージ付加
 * @param marker
 * @param msg
 */
function attachMessage(marker, msg) {
	google.maps.event.addListener(marker, 'click', function(event) {
		new google.maps.InfoWindow({
			content: msg
		}).open(marker.getMap(), marker);
	});
}

/**
 * メッセージ付加
 * @param marker
 * @param msg
 */
function attachMessageCar(marker,latlon ,htmlContents) {
//		if (dispMode == 2) {
			/* 吹き出しを作成 */
		    var msg = new ZDC.MsgInfo(
		    	latlon,
		        {offset: ZDC.Pixel(0, -18)}
		    );
		    /* 吹き出しを追加 */
		    map.addWidget(msg);
	
			ZDC.bind(
					marker,
	                ZDC.MARKER_CLICK,
	                {latlon: latlon, htmlContents: htmlContents, info: msg},
	                showInfo
	            );
//		}
}

function showInfo() {
    this.info.moveLatLon(this.latlon);
    this.info.setHtml(this.htmlContents);
    this.info.open();
};

/**
 * 指定桁数にゼロ埋め
 * @param num
 * @param digits
 * @returns {String}
 */
function toTargetDigits(num, digits) {
	num += '';
	while (num.length < digits) {
		num = '0' + num;
	}
	return num;
  }

/**
 * ルート番号リスト取得
 */
function getOrderNoList()
{
	var orderDt = $("#orderDt").val();
	var carNo = $("#carNo").val();

	if (orderDt && carNo)
	{
		// 送信データ
		var requestParam = {'orderDt' : orderDt, 'carNo' : carNo};

		// Ajax通信を行い、ルート番号リストを受信する。
		$.ajax({
			type		: 'GET',
			dataType	: 'json',
			url			: "getOrderNoList",
			data		: requestParam,
			success		: function (data) {
							displayOrderNoList(data);
						},
			error		: function(XMLHttpRequest, textStatus, errorThrown) {
							console.log(XMLHttpRequest);
							console.log(textStatus);
							console.log(errorThrown);
						}
		});
	}
}

/**
 * ルート番号リストを画面に反映
 * @param data
 */
function displayOrderNoList(data)
{
	$("#orderNo > option").remove();

	if (data && 0 < data.length)
	{
		for (var i in data)
		{
			var keyValue = data[i];
			for (var key in keyValue)
			{
				// ルート番号追加
				$('#orderNo').append($('<option>').html(keyValue[key]).val(key));
			}
		}
	}
}

