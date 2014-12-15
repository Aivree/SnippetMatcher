//<![CDATA[
var qStr = (function() {
var qSet = {};
var query = window.location.search.substring(1);
var pairs = query.split('&');
for (var i=0; i<pairs.length; i++){
var pair = pairs[i].split('=');
qSet[pair[0]] = pair[1];}
return qSet;})();
//]]>