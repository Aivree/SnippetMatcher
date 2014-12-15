
function isNullOrWhitespace(input) {

    if (input == null) return true;

    return input.replace(/\s/g, '').length < 1;
}

var urlParams;
(window.onpopstate = function () {
    var match,
        pl = /\+/g,  // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
        query = window.location.search.substring(1);

    urlParams = {};
    while (match = search.exec(query))
        urlParams[decode(match[1])] = decode(match[2]);
})();

$(function () {
    if ( !isNullOrWhitespace(urlParams['q']) ) {
        $('#q').val(urlParams['q']);
    }
});