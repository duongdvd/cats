$(function(){
	$('.datetimepicker').each(function() {
		// 日時はhiddenに切り替え。見た目は日付と時間入力に変更。
		var $dt = $('<input type="hidden">').attr('name', this.name).val(this.value);
		// 見た目を調整するためのclassがあれば指定
		var $d = $('<input type="date">');
		var $t = $('<select></select>').addClass('time-selector');
		$(this).after($dt, $d, ' ', $t);
		// 時刻の方は10分おきのプルダウンなどにする
		var hh = ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'];
		var mm = ['00', '10', '20', '30', '40', '50'];
		for (var i = 0; i < hh.length; ++i) {
			for (var j = 0; j < mm.length; ++j) {
				var $o = $('<option></option>')
							.attr('value', hh[i] + ':' + mm[j] + ':00')
							.text(hh[i] + ':' + mm[j]);
				$t.append($o);
			}
		}
		// 初期値があれば反映
		$d.val(this.value.replace(/^(\d\d\d\d-\d\d-\d\d).*$/, "$1"));
		$t.val(this.value.replace(/^.*(\d\d:\d\d:\d\d).*$/, "$1"));
		// 日付変更時と時間変更時にhidden化した方へ値を反映する処理を仕込んでおく
		function datetimeChange() {
			if ($d.val() === null || $d.val() === '' || $t.val() === null || $t.val() === '') {
				$dt.val(null);
			} else {
				$dt.val($d.val() + ' ' + $t.val());
			}
		}
		$d.on('change', datetimeChange);
		$t.on('change', datetimeChange);
		// オリジナルはさようなら
		$(this).remove();
	});
});


var windowResize = function() {
	var c = document.getElementById("mainContants");
	var h = document.documentElement.clientHeight;
	h = h - 240;
	c.style.height = h + "px";
}

function sendLogout() {
	var f = $('#logoutForm');
	f.submit();
}

/**
 * タイムスタンプより時刻文字列を取得
 * @param ts
 * @returns {String}
 */
function getStrToTimestamp(ts) {
	var d = new Date( ts );
	var year  = d.getFullYear();
	var month = d.getMonth() + 1;
	var day  = d.getDate();
	var hour = ( d.getHours()   < 10 ) ? '0' + d.getHours()   : d.getHours();
	var min  = ( d.getMinutes() < 10 ) ? '0' + d.getMinutes() : d.getMinutes();
	var sec   = ( d.getSeconds() < 10 ) ? '0' + d.getSeconds() : d.getSeconds();
	return year + '/' + month + '/' + day + ' ' + hour + ':' + min + ':' + sec;
}
