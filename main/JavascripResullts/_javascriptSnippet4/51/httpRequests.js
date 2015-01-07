/// <reference path="../libs/jquery-2.1.1.js" />
/*global document: false */

var httpRequest = (function () {
    "use strict";

    // Makes ajax query
    function ajaxRequests(url, type, dataType, data, headers) {
        var defer, $ajax, customHeaders;

        customHeaders = headers || {};
        defer = new $.Deferred();

        $ajax = $.ajax({
            url: url,
            type: type,
            data: data,
            dataType: dataType,
            headers: customHeaders,
            success: defer.resolve,
            error: defer.reject
        });

        return $ajax;
    }

    // Fetch JSON object from the server
    function getJson(url, dataType, data) {
        return ajaxRequests(url, 'GET', dataType, data);
    }

    // Post JSON object to the server
    function postJson(url, dataType, data, header) {
        return ajaxRequests(url, 'POST', dataType, data, header);
    }

    // Custom method request
    function customMethodRequest(url, method, dataType, data, headers) {
        return ajaxRequests(url, method, dataType, data, headers);
    }

    return {
        getJson: getJson,
        postJson: postJson,
        customMethodRequest: customMethodRequest
    };
}());