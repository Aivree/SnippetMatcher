$(function() {

    function getUrlParameter(paramName) {
        var searchString = window.location.search.substring(1);
        var params = searchString.split("&");

        for ( var i = 0; i < params.length; i++) {
            var val = params[i].split("=");
            if (val[0] == paramName) {
                return unescape(val[1]);
            }
        }
        return null;
    }

    $('#btnAllow').click(function() {
        $.postJSON('allow' + window.location.search, {}, function(data, textStatus, jqXHR) {
            if (jqXHR.status == 200) {
                window.location = data.location;
            } else {
                alert(textStatus);
            }
        });
    });

    $('#btnDeny').click(function() {
        window.location = getUrlParameter('redirect_uri') + '?error=access_denied';
    });

});