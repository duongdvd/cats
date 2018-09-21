
/**
 * 1:車両状況表示
 * 2:ルート表示
 */
var dispMode = 1;

/** マップ */
var map = null;

/** マーカー */
var markers = [];

/** マーカー（チェックポイント） */
var checkPointMarkers = [];

/** 現在マーカー */
var nowMarker = null;

/** ルート */
var directionsDisplays = [];

/** 位置（軌跡） */
var locationPath = null;

/** 描画タイマー */
var timerView;

/** 描画タイマー間隔 */
var interval = 30;

/** 表示中モーダルウィンドウ */
var wn = '';

/** メッセージ受信タイマー */
var msgRecvTimerView;

/** メッセージ受信タイマー間隔 */
var msgRecvInterval = 60;

/**
 * jQuery 初期表示時処理
 */
$(document).ready(function()
{
	// タブ選択
	$('li.tab-view').addClass('selected');

	// ページ読み込み時に実行したい処理
	mapInit();

	// Ajax通信を行い、マップ描画する。
	$.ajax({
		type		: 'GET',
		dataType	: 'json',
		url			: "getMapData",
		success		: function (data) {
						console.log(data);
						successGetMap(data, true);
					},
		error		: function(XMLHttpRequest, textStatus, errorThrown) {
						errorGetMap(XMLHttpRequest, textStatus, errorThrown);
					}
	});
	
	

	// 設定ウィンドウのフローティング
	mapEditFloating();

	// メッセージ履歴のフローティング
	messageHistFloating();

	// タイマー設定
	setTimer();

	// メッセージ受信タイマー設定
	setMsgRecvTimer();

	// タイマーセットクリック時処理
	$('#timerSet').on('click', function() {
		// タイマー設定
		setTimer();
	});

	// モーダルウィンドウ閉じる
	$('.closeMessage,.modalBack').click(function(){
		$(wn).fadeOut(500);
	});

	// モーダルウィンドウ閉じる
	$('.closeRecv,.modalBackRecv').click(function(){
		$(wn).fadeOut(500);
	});

	// 応答メッセージ受信
	// recvMessage();
});

/**
 * 受信メッセージモーダルウィンドウ表示
 * @param data
 */
function displayRecvMessage(data)
{
	if (data && 0 < data.length)
	{
		var seqArray = [];

		wn = '.modalMessageRecv';
		var mW = $(wn).find('.modalBodyRecv').innerWidth() / 2;
		var mH = $(wn).find('.modalBodyRecv').innerHeight() / 2;
		$(wn).find('.modalBodyRecv').css({'margin-left':-mW,'margin-top':-mH});
		$(wn).fadeIn(500);
		$('.modalBackRecv').fadeIn(500);

		// 初期化
		$('table#listMessageRecv tbody *').remove();

		for (var i in data)
		{
			// 追加行スクリプト
			var resMsgDto = data[i];
			var addRow = createAddMessageTag(resMsgDto);

			// 受信メッセージモーダルウィンドウへ表示
			$("table#listMessageRecv tbody").append(addRow);

			seqArray.push(resMsgDto.seq);
		}

		// 表示したメッセージを既読更新
		readMessage(seqArray);
	}
}

/**
 * 送信済メッセージを履歴に表示
 * @param data
 */
function displayHistMessage(data)
{
	if (data && 0 < data.length)
	{
		// 初期化
		$('table#listMessageHist tbody *').remove();

		for (var i in data)
		{
			// 追加行スクリプト
			var resMsgDto = data[i];
			var addRow = createAddMessageTag(resMsgDto);

			// 送信メッセージ履歴ウィンドウへ表示
			$("table#listMessageHist tbody").append(addRow);
		}
	}
}

/**
 * 追加メッセージ行タグ取得
 * @param resMsgDto
 * @returns {String}
 */
function createAddMessageTag(resMsgDto)
{
	var addRow =
		"<tr>" +
			"<td style='display: none;'><input name='messageHistList.seq' type='hidden' value='" + resMsgDto.seq + "'/></td>" +
			"<td>" + resMsgDto.sendDate + "</td>" +
			"<td>" + resMsgDto.carName + "</td>" +
			"<td>" + resMsgDto.response + "</td>" +
			"<td>" + resMsgDto.message + "</td>" +
		"</tr>";

	return addRow;
}

/**
 * ルート情報取得
 * @param marker
 * @param carNo
 * @param routeNo
 * @returns {Boolean}
 */
function getRoute(marker, carNo, routeNo)
{
	var data = {
		'carNo' : carNo,
		'routeNo' : routeNo
	};

	if (marker == null) {
		marker = nowMarker;
	}

	// Ajax通信を行い、メッセージを送信する。
	$.ajax({
		type		: 'GET',
		dataType	: 'json',
		url			: "getRoute",
		data		: data,
		success		: function (data) {
						console.log(data);
						nowMarker = marker;
						dispRoute(marker, carNo, data);
					},
		error		: function(XMLHttpRequest, textStatus, errorThrown) {
						console.log(XMLHttpRequest);
						console.log(textStatus);
						console.log(errorThrown);
						alert("ルート情報取得に失敗しました。");
					}
	});

	return false;
}

/**
 * ルート情報表示処理
 * @param marker
 * @param carNo
 * @param data
 */
function dispRoute(marker, carNo, data)
{
	// タイマーストップ
	clearInterval(timerView);

	// マーカー削除
	markers.forEach(function(deleteMarker, index)
	{
		if (deleteMarker != marker) {
//			deleteMarker.setMap(null);
			map.removeWidget(deleteMarker);
		}
	});

	// ルート削除
	directionsDisplays.forEach(function(directionsDisplay, index) {
//		directionsDisplay.setMap(null);
		map.removeWidget(directionsDisplay);
	});

	// 選択中リンク解除
	$('a.searching').removeClass('searching');

	// 順序配列
	var startDetailNo = 999;
	var finishDetailNo = 0;
	var routeIndexArray = [];
	for (var i = 0; i < data.length ; i++)
	{
		// 選択中リンク設定
		if (i == 0)
		{
			var orderNo = data[i]['orderNo'].toString();
			$('a.order-no-link[data-orderno="' + orderNo + '"]').addClass('searching');
		}

		var orderValue = data[i]['detailNo'];
		routeIndexArray.push({"index" : i, "orderValue" : orderValue});

		// 最大値
		if (finishDetailNo < orderValue) {
			finishDetailNo = orderValue;
		}

		// 最小値
		if (orderValue < startDetailNo) {
			startDetailNo = orderValue;
		}
	}

	// 順序配列をソート
	routeIndexArray.sort(
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

	// ルート初期化
	var routeArray = [];
	var startPoint = null;
	var endPoint = null;
	for (var i = 0; i < routeIndexArray.length ; i++)
	{
		var index = routeIndexArray[i]['index'];
		var point = new ZDC.LatLon(data[index]['lat'], data[index]['lng']);
		if (startPoint == null) {
			startPoint = point;
		}
		endPoint = point;
		var finishFlg = data[index]['finishFlg'];

		// マーカー作成
		if (startDetailNo == finishDetailNo && data[index]['detailNo'] == startDetailNo) {

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
					data[index]['name'] + '</div>');
		} else if (data[index]['detailNo'] == startDetailNo) {

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
					data[index]['name'] + '</div>');
		} else if (data[index]['detailNo'] == finishDetailNo) {
			
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
				data[index]['name'] + '</div>');
		} else {
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
	        marker.setTitle(String(data[index]['detailNo']));
			
			// メッセージ付加
			attachMessageCar(marker, point,
				'<div class="message">訪問順：' + data[index]['detailNo'] + '<br/>' +
					data[index]['name'] + '<br/>' +
					data[index]['info'] + '</div>');
		}

		// ルートの始点と終点を保存
		routeArray.push({"start" : startPoint, "end" : endPoint});

		startPoint = endPoint;
		markers.push(marker);
	}

	// ルート表示
	for (var i in routeArray)
	{
		displayRoute(routeArray[i]['start'], routeArray[i]['end']);
	}
}

/**
 * 位置情報表示処理
 * @param carNo
 * @param data
 */
function dispLocation(carNo, data)
{
	// 位置
	var locationArray = [];
	for (var i = 0; i < data['locations'].length ; i++)
	{
		var location = data['locations'][i];
		var point = new ZDC.LatLon(location['locationLat'], location['locationLng']);

		// 位置の始点と終点を保存
		locationArray.push(point);
	}

	// 位置表示
	displayLocation(locationArray);

	// チェックポイント
	for (var i = 0; i < data['checkPoints'].length ; i++)
	{
		var checkPoint = data['checkPoints'][i];
		var point = new ZDC.LatLon(checkPoint['locationLat'], checkPoint['locationLng']);

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
        marker.setTitle(String(i + 1));
		
		// メッセージ付加
		attachMessageCar(marker, point,
			'<div class="message">時刻：' + getStrToTimestamp(location['locationDt']) + '</div>');
		checkPointMarkers.push(marker);
	}
}

/**
 * 位置情報取得
 * @param carNo
 * @returns {Boolean}
 */
function getLocation(carNo)
{
	var data = {
			'carNo' : carNo
	};

	// Ajax通信を行い、メッセージを送信する。
	$.ajax({
		type		: 'GET',
		dataType	: 'json',
		url			: "getLocation",
		data		: data,
		success		: function (data) {
						console.log(data);
						dispLocation(carNo, data);
					},
		error		: function(XMLHttpRequest, textStatus, errorThrown) {
						console.log(XMLHttpRequest);
						console.log(textStatus);
						console.log(errorThrown);
						alert("位置情報取得に失敗しました。");
					}
	});

	return false;
}

/**
 * ルート表示
 * @param start
 * @param end
 */
function displayRoute(start, end)
{
	
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
        directionsDisplays.push(pl);
        map.addWidget(pl);
    }
    /* 地点が全て表示できる縮尺レベルを取得 */
    var adjust = map.getAdjustZoom(latlons);
    map.moveLatLon(adjust.latlon);
    map.setZoom(adjust.zoom);
	
}

/**
 * 位置表示
 * @param locationArray
 */
function displayLocation(locationArray)
{
	// Polyline オブジェクト
	locationPath = new ZDC.Polyline(locationArray, {
//		path: locationArray,    // ポリラインの配列
		strokeColor: '#FF0000', // 色（#RRGGBB形式）
//		strokeOpacity: 1.0,     // 透明度 0.0～1.0（デフォルト）
		strokeWeight: 2         // 太さ（単位ピクセル）
	});
	
	map.addWidget(locationPath);
//	locationPath.setMap(map);
}

/**
 * 送信メッセージ送信
 */
function sendExcute()
{
	var carNo = $("#sendCarNo").val();
	var sendMessage = $("#sendMessage").val();
	var data = {'carNo' : carNo, 'sendMessage' : sendMessage};

	if (!sendMessage)
	{
		alert("メッセージは必須です。");
		return false;
	}

	// Ajax通信を行い、メッセージを送信する。
	$.ajax({
		type		: 'GET',
		dataType	: 'json',
		url			: "sendMessage",
		data		: data,
		success		: function (data) {
						console.log(data);
						displayHistMessage(data);
						$(wn).fadeOut(500);
					},
		error		: function(XMLHttpRequest, textStatus, errorThrown) {
						console.log(XMLHttpRequest);
						console.log(textStatus);
						console.log(errorThrown);
						alert("メッセージの送信に失敗しました。");
					}
	});

	return false;
}

/**
 * マップ初期化処理
 */
function mapInit() {
	// キャンパスの要素を取得する
	var canvas = document.getElementById('map-canvas');
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
 * 設定ウィンドウのフローティング
 */
function mapEditFloating() {
	var leftDiff;
	var topDiff;
	var doc = $(document);
	var floatbox = $("#mapEdit");
	var handle = $("#mapEdit .editHandle");

	// ドラッグ中
	function moving(event) {
		floatbox.css("left", (event.pageX - leftDiff) + "px")
			.css("top", (event.pageY - topDiff) + "px")
			.css("opacity", 0.7);
	}

	// ドラッグ終了時
	function dragEnd() {
		doc.off("mousemove mouseup");
		floatbox.css("opacity", 1.0);
	}

	// マウスダウン時
	function mouseDown(event) {
		leftDiff = event.pageX - floatbox.offset().left;
		topDiff = event.pageY - floatbox.offset().top;
		doc.on("mousemove", moving)
			.on("mouseup", dragEnd);
	}

	// ハンドル部分に対するイベント設定
	handle.on("mousedown", mouseDown);
}

/**
 * メッセージ履歴のフローティング
 */
function messageHistFloating() {
	var leftDiff;
	var topDiff;
	var doc = $(document);
	var floatbox = $("#messageHist");
	var handle = $("#messageHist .messageHandle,.messageContents");

	// ドラッグ中
	function moving(event) {
		floatbox.css("left", (event.pageX - leftDiff) + "px")
			.css("top", (event.pageY - topDiff) + "px")
			.css("opacity", 0.7);
	}

	// ドラッグ終了時
	function dragEnd() {
		doc.off("mousemove mouseup");
		floatbox.css("opacity", 1.0);
	}

	// マウスダウン時
	function mouseDown(event) {
		leftDiff = event.pageX - floatbox.offset().left;
		topDiff = event.pageY - floatbox.offset().top;
		doc.on("mousemove", moving)
			.on("mouseup", dragEnd);
	}

	// ハンドル部分に対するイベント設定
	handle.on("mousedown", mouseDown);

	// リサイズ可能とする(必要：jquery.funcResizeBox.js)
	if ($("#messageHist").is(':visible')) {
		$("#messageHist").funcResizeBox({
			minWidth: 10,       // リサイズ可能な最少の幅(px)
			minHeight: 10,      // リサイズ可能な最少の高さ(px)
			maxWidth: 10000,    // リサイズ可能な最大の幅(px)
			maxHeight: 10000,   // リサイズ可能な最大の高さ(px)
			mouseRange: 10,     // リサイズイベントを取得する範囲(px)
			isWidthResize:true, // 水平方向のリサイズのON/OFF
			isHeightResize:true // 垂直方向のリサイズのON/OFF
		});
	}
}

/**
 * マップ情報取得成功時処理
 * @param data
 * @param initDispFlg
 */
function successGetMap(data, initDispFlg)
{
	if (initDispFlg)
	{
		// 中心位
//		var latlon = new ZDC.LatLon(data.centerLat, data.centerLng);
//		map.moveLatLon(latlon);

		// キャンパスの要素を取得する
		var canvas = document.getElementById('map-canvas');
		// [canvas]に、[mapOptions]の内容の、地図のインスタンス([map])を作成する

		var carInfo = data.carData[1];
		if (carInfo.locationLat == null){
			var latlon = new ZDC.LatLon(carInfo.locationLat, carInfo.locationLng);
			map = new ZDC.Map(canvas,{
				latlon: latlon,
				zoom: 9,
		        mapType: ZDC.MAPTYPE_HIGHRES_LV18
		    });
		}else{
			map = new ZDC.Map(canvas,{
				zoom: 9,
		        mapType: ZDC.MAPTYPE_HIGHRES_LV18
		    });
		}
		
		/* スケールバーを作成 */
	    map.addWidget(new ZDC.ScaleBar());
	    map.addWidget(new ZDC.Control());
		
	}

	// アイコン作成
//	var image = {
//		url : './resources/img/car64.png',
//		scaledSize : new ZDC.WH(45,25)
//	}

    // マーカー削除
	markers.forEach(function(marker, index) {
		map.removeWidget(marker);
	});

	// チェックポイントマーカー削除
	checkPointMarkers.forEach(function(marker, index) {
		map.removeWidget(marker);
//		marker.setMap(null);
	});

	// ルート削除
	directionsDisplays.forEach(function(directionsDisplay, index) {
//		directionsDisplay.setMap(null);
		map.removeWidget(directionsDisplay);
	});

	// 位置（軌跡）削除
	if (locationPath != null) {
//		locationPath.setMap(null);
		map.removeWidget(locationPath);
	}

	// 表示領域を生成します。
	//var bounds = new google.maps.LatLngBounds();

	for (var carNo in data.carData)
	{
		// 車両情報
		var carInfo = data.carData[carNo];
		var latlon = new ZDC.LatLon(carInfo.locationLat, carInfo.locationLng);
		map.moveLatLon(latlon);
		// マーカー作成
		var marker = new ZDC.Marker(latlon,{
			custom: {
				base:{
					src: './resources/img/' + carInfo.image
				}
			}
		});

		map.addWidget(marker);
		
		// マーカーイベント
		addListenerMarkerClick(marker, carNo);

		//show infor car
		attachInfoCar(marker, latlon,
				'<div class="message">Name Car：' + carInfo.nameCar +
				'<br/>Address：' + carInfo.address +
				'<br/>Driver Name：' + carInfo.driverName +
				'<br/><input type="button" id="btnClose" class="btn-long sendMessage" value="Close"/></div>');
		markers.push(marker);
	}

	// タイマー以外の処理
	if (initDispFlg)
	{
		// 地図表示領域の変更を反映します。
		//map.fitBounds (bounds);
	}
}

//show info
function attachInfoCar(marker, latlon, htmlContents) {
	if (dispMode = 2) {
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
	}
}

$('#btnClose').click(function () {
	alert(12);
	$(this).closest('.ui-dialog-content').dialog('close');
});
/**
 * メッセージ付加
 * @param marker
 * @param msg
 */
function attachMessageCar(marker,latlon ,htmlContents) {
		if (dispMode = 2) {
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
		}
}

function showInfo() {
	this.info.moveLatLon(this.latlon);
	this.info.setHtml(this.htmlContents);
	this.info.open();
};

/**
 * マーカークリックイベント設定
 * @param marker
 * @param carNo
 */
function addListenerMarkerClick(marker, carNo) {
	ZDC.addListener(
		marker
		,ZDC.MARKER_CLICK
		,function(event) {
			console.log(carNo);
			if (dispMode == 1) {
				// ルート情報表示
				dispMode = 2;
				getRoute(marker, carNo, null);

				// 位置情報表示
				getLocation(carNo);
			} else {
				// 車両状況表示
				dispMode = 1;

				/**
				 * Ajax通信を行い、マップ描画する。
				 */
				$.ajax({
					type		: 'GET',
					dataType	: 'json',
					url			: "getMapData",
					success		: function (data) {
									console.log(data);
									successGetMap(data, false);
								},
					error		: function(XMLHttpRequest, textStatus, errorThrown) {
									errorGetMap(XMLHttpRequest, textStatus, errorThrown);
								}
				});

				setTimer();
			}
		}
	);
}

/**
 * マップ情報取得失敗時処理
 * @param XMLHttpRequest
 * @param textStatus
 * @param errorThrown
 */
function errorGetMap(XMLHttpRequest, textStatus, errorThrown) {
	console.log("error:" + XMLHttpRequest);
	console.log("status:" + textStatus);
	console.log("errorThrown:" + errorThrown);
}

/**
 * メッセージ付加
 * @param marker
 * @param msg
 */
function attachMessage(marker, msg) {
	google.maps.event.addListener(marker, 'click', function(event) {
		if (dispMode == 2) {
			new google.maps.InfoWindow({
				content: msg
			}).open(marker.getMap(), marker);
		}
	});
}

/**
 * タイマー設定
 */
function setTimer() {
	if ($('input[name="timerOnOff"]:checked').val() === '1') {
		// ON
		var tempInterval = $('input[name="interval"]').val();
		if (isNaN(tempInterval)) {
			alert("intervalに数値以外が入力されています。");
		} else {
			// タイマーON
			interval = tempInterval;
			timerView = setInterval(function()
			{
				/**
				 * Ajax通信を行い、マップ描画する。
				 */
				$.ajax({
					type		: 'GET',
					dataType	: 'json',
					url			: "getMapData",
					success		: function (data) {
									console.log(data);
									successGetMap(data, false);
								},
					error		: function(XMLHttpRequest, textStatus, errorThrown) {
									errorGetMap(XMLHttpRequest, textStatus, errorThrown);
								}
				});
			}, interval * 1000);
		}
	} else {
		// OFF
		clearInterval(timerView);
	}
}

/**
 * メッセージ受信タイマー設定
 */
function setMsgRecvTimer()
{
	// タイマー
	msgRecvTimerView = setInterval(function()
	{
		// 応答メッセージ受信
		// recvMessage();
	}, msgRecvInterval * 1000);
}

