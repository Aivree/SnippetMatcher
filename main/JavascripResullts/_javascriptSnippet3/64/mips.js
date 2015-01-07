function getURLParam(param) {
	var pageUrl = window.location.search.substring(1);
	var sURLVariables = pageUrl.split('&');

	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == param) 
			return sParameterName[1];
	}

	return null;
}