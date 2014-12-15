define(function() {
    var URL_PARAMS=(function(){var a={};var b=window.location.search.substring(1);var c=b.split("&");for(var d=0;d<c.length;d++){var e=c[d].split("=");if(typeof a[e[0]]==="undefined"){a[e[0]]=e[1]}else if(typeof a[e[0]]==="string"){var f=[a[e[0]],e[1]];a[e[0]]=f}else{a[e[0]].push(e[1])}}return a})();

    return URL_PARAMS;
});
