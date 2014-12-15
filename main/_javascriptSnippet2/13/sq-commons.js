// file: sq-commons.js
function SQ() {}

SQ.prototype.getParameters = function() {
	
	var params = {}, e;
	var regex = /([^&=]+)=([^&]*)/g;
	var queryString = location.search.substring(1);
	while (e = regex.exec(queryString)) {
	    params[decodeURIComponent(e[1])] = decodeURIComponent(e[2]);
	}	
}

SQ.prototype.updateParameters = function(callback) {
	
	var params = {}, e;
	var regex = /([^&=]+)=([^&]*)/g;
	var queryString = location.search.substring(1);
	while (e = regex.exec(queryString)) {
	    params[decodeURIComponent(e[1])] = decodeURIComponent(e[2]);
	}	
	callback(params);
	location.search = $.param(params);

}
	
SQ.prototype.updateHash = function(callback) {
	
	var map = {}, e;
	var hash = window.location.hash.replace("#","");	
	var regex = /([^\/:]+):([^/]*)/g;
	while (e = regex.exec(hash)) {
	    map[decodeURIComponent(e[1])] = decodeURIComponent(e[2]);
	}	
	callback(map);
	hash="";
	for (key in map) {
		hash += "/"+key+":"+map[key];
	}
	window.location.hash = hash;
}

SQ.prototype.hash = function() {
	
	var map = {}, e;
	var hash = window.location.hash.replace("#","");	
	var regex = /([^\/:]+):([^/]*)/g;
	while (e = regex.exec(hash)) {
	    map[decodeURIComponent(e[1])] = decodeURIComponent(e[2]);
	}	
	return map;
}