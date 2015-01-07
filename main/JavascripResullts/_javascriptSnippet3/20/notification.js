
$(document).ready(function(){

	var result = "";
	var params = window.location.search.substring(1).split('&');
	for (var j = 0; j < params.length; j++) {
		var param = params[j].split('=');
        if ("type" == param[0]) {
        	type = param[1];
        } else if("result" == param[0]) {
        	result = param[1];
        }
	}
	if (result.length == 0) {
		result = "default";
	}
    $('#'+result).css("display", "block");

});
  
  