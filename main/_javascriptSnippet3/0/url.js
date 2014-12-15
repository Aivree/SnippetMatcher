///
//	diesel.util.url
///

diesel.util.url = {
	"args":{},
	"hash":window.location.hash,
	"protocol":window.location.protocol,
	"host":window.location.host,
	"path":window.location.pathname,
	"read":function(){
		var equals = null, data, search =window.location.search.split('&');
		for(var i=0 ; i < search.length;i++){
			data= search[i];
			equals = search[i].indexOf('=');
			diesel.util.url.args[data.substring(0, equals)] = data.substring(equals + 1, data.substring(equals+1));
		}

	}

};