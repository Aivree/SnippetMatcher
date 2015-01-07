
function getQueryVariable(variable)
{
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return unescape(pair[1]);
		}
	}
	alert('Query Variable ' + variable + ' not found');
}

function getPathVariable(index)
{
	var query = window.location.pathname;
	
	query = query.substring(1);
	
	var vars = query.split("/");

	return vars[index];
}