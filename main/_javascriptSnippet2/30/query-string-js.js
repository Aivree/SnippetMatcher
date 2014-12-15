;
(function() {
  var _urlParams = {};
  var match;
  var pl = /\+/g;
  var search = /([^&=]+)=?([^&]*)/g;
  var decode = function(s) {
      return decodeURIComponent(s.replace(pl, " "));
    };
  var query = window.location.search.substring(1);

  while (match = search.exec(query)) {
    _urlParams[decode(match[1])] = decode(match[2]);
  }

  var _HELPERS = window.__get_project_namespace__(['Helpers']);
  _HELPERS.QueryString = {
    getValue: function(key) {
      return _urlParams[key] || null;
    }
  }
})();