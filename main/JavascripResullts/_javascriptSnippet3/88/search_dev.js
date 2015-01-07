$('document').ready(function(){
	console.log("search_dev loaded");

	var parameters = getParams();

	var search = parameters["search"];
	var availability = parameters["availability"];
	availability = (availability && availability.indexOf("+") > -1) ? availability.replace("+", " ") : availability;
	var location = parameters["location"];

	console.log("search:", search, "availability:", availability, "location:", location);

	$("#search").val(search);
	$("#availability").val(availability);
	$("#location").val(location);

	function getParams(){
		var searchStr = window.location.search.substring(1);
		var params = searchStr.split("&");
		var hash = {};

		for (var i = 0; i < params.length; i++) {
			var value = params[i].split("=");
			hash[unescape(value[0])] = unescape(value[1]);
		}

		return hash;
	}
});