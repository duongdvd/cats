function getWs(url, callback) {
	$.ajax({
		url: url,
		dataType: 'json',
		type: "GET",
		success: function(data, textStatus, xhr) {
            return callback(data, "success");
		},
		error: function(xhr, textStatus, errorThrown) {
            return callback(xhr, "error");
		}
	});
}

function postWs(url, data, callback) {
	return $.ajax({
		url: url,
		async: true,
		dataType: 'json',
		type: "POST",
		cache: true,
		data: JSON.stringify(data),
		contentType: "application/json; charset=utf-8",
		success: function(data, textStatus, xhr) {
			return callback(data, "success");
		},
		error: function(xhr, textStatus, errorThrown) {
            return callback(xhr, "error");
		}
	});
}

function getUrl(url) {
    return window.baseUrl + "/" + url;
}