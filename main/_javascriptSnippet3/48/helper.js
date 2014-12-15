KANAPLA = KANAPLA || {};

KANAPLA.getURLParameter = function (name) {
  var query_str = window.location.search.substring(1);
  var params = query_str.split('&');
  var param_pair = [];

  for (var i=0; i < params.length; i++) {
    param_pair = query_str[i].split('=');
    if (param_pair[0] == name) {
      return param_pair[1];
    }
  }
};

