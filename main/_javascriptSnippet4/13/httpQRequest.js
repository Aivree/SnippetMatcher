'use strict';

var httpQRequest = angular.module('httpQRequest', []);

httpQRequest.factory("httQ", function functionName($http, $q) {
    var defaultContentType = {'Content-Type': 'application/json'};

    function makeRequest(requestUrl, requestMethod, requestData, requestHeaders) {
        if (requestHeaders === undefined){
            requestHeaders = defaultContentType;
        }

        var deferred = $q.defer();

        $http({
            method: requestMethod,
            url: requestUrl,
            data: requestData,
            headers: requestHeaders
        })
            .success(function (response) {
                deferred.resolve(response);
            }).error(function (err) {
                deferred.reject(err);
            });

        return deferred.promise;
    }

    function makeGetRequest(url, headersObject) {
        return makeRequest(url, "GET", "", headersObject);
    }

    function makePostRequest(url, data, headersObject) {
        return makeRequest(url, "POST", data, headersObject);
    }

    function makePutRequest(url, data, headersObject) {
        return makeRequest(url, "PUT", data, headersObject);
    }

    function makeDeleteRequest(url, headersObject) {
        return makeRequest(url, "DELETE", "", headersObject);
    }

    return {
        get: makeGetRequest,
        post: makePostRequest,
        put: makePutRequest,
        delete: makeDeleteRequest
    };
});

