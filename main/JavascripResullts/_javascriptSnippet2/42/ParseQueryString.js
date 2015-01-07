/**
 * http://stackoverflow.com/questions/901115/get-query-string-values-in-javascript
 *
 * @return {object}
 */
var ParseQueryString = function () {
  var query = {};
  (function () {
      var match,
          pl     = /\+/g,  // Regex for replacing addition symbol with a space
          search = /([^&=]+)=?([^&]*)/g,
          decode = function (s) { return decodeURIComponent(s.replace(pl, " ")) },
          query_string  = window.location.search.substring(1)
  
      while (match = search.exec(query_string))
         query[decode(match[1])] = decode(match[2])
  })()
  return query
}
