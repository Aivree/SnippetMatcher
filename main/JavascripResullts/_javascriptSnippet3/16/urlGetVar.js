function urlGetVar( key ) {
  var q = window.location.search.substring(1), val;
  if(q.indexOf(key) >= 0) {
    val = decodeURI(q.substr(q.indexOf(key)+key.length+1).split('&')[0]);
  } 
  return val;
}

var $_GET = (function () {
    var vars = window.location.search.substring(1).split('&'),
        _GET = {},
        pair, i;
    for (i = 0; i < vars.length; i++) {
        pair = vars[i].split('=');
        _GET[pair[0]] = decodeURI(pair[1]);
    }
    return _GET;
}());
