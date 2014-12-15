
(function (window, undefined) {

    var _sendRequest = function (url, requestMethod, requestHeaders, data) {
        var httpRequest = new XMLHttpRequest();
        var promise = new Promise();

        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === 4) {
                if (httpRequest.status === 200) {
                    promise.fulfill(httpRequest.responseText);
                }
                else {
                    promise.reject({
                        "statusText": httpRequest.statusText, 
                        "status":httpRequest.status
                    });
                }
            }
        };

        httpRequest.open(requestMethod, url, true);

        if (requestHeaders) {
            for (var headerName in requestHeaders) {
                if (requestHeaders.hasOwnProperty(headerName)) {
                    httpRequest.setRequestHeader(headerName, requestHeaders[headerName]);
                }
            }
        }
        httpRequest.send(data);

        return promise;
    };

    var ajax = {

        get: function (url, requestHeaders) {
            return _sendRequest(url, "GET", requestHeaders);
        },

        put: function (url, requestHeaders, data) {
            return _sendRequest(url, "PUT", requestHeaders, data);
        },

        post: function (url, requestHeaders, data) {
            return _sendRequest(url, "POST", requestHeaders, data);
        },

        delete_: function (url) {
            return _sendRequest(url, "DELETE");
        }
    };

    window.ajax = ajax;

} (window));
