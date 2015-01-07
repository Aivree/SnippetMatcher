'use strict';

var q = require('q');
var http = require('q-io/http');
var FormData = require('form-data');

var querystring  = require('querystring');






var request = exports;


function isResponseValid (res) {
    if (Math.floor(res.status / 100) !== 2) {
        return false;
    }
    return true;
}

function processResponse (response) {
    return q.all([response, response.body.read()]).spread(function (response, body) {
        if (!isResponseValid(response)) {
            throw new HttpRequestError(response.status, body);
        }
        return body.toString();
    });
}

request.get = function performGetRequest (url, data, headers) {
    headers = headers || {};
    var queryParamsString = querystring.stringify(data);
    if (queryParamsString) {
        url = url + '?' + queryParamsString;
    }

    var opts = {
        method: 'GET',
        url: url,
        headers: headers,
    };

    return http.request(opts).then(processResponse);
};

request.post = function performPostRequest (url, data, headers) {
    headers = headers || {};
    if (!headers['Content-Type']) {
        headers['Content-Type'] = 'application/x-www-form-urlencoded';
    }

    var opts = {
        method: 'POST',
        url: url,
        body: [querystring.stringify(data)],
        headers: headers,
    }

    return http.request(opts).then(processResponse);
}

request.put = function performPostRequest (url, data, headers) {
    headers = headers || {};
    if (!headers['Content-Type']) {
        headers['Content-Type'] = 'application/x-www-form-urlencoded';
    }

    var opts = {
        method: 'PUT',
        url: url,
        body: [querystring.stringify(data)],
        headers: headers,
    }

    return http.request(opts).then(processResponse);
}

request.delete = function performPostRequest (url, data, headers) {
    headers = headers || {};
    if (!headers['Content-Type']) {
        headers['Content-Type'] = 'application/x-www-form-urlencoded';
    }

    var opts = {
        method: 'DELETE',
        url: url,
        body: [querystring.stringify(data)],
        headers: headers,
    }

    return http.request(opts).then(processResponse);
}

var HttpRequestError = function HttpRequestError (status, body) {
    this.status = status;
    this.body = body;

    this.name = 'HttpRequestError';
    Error.call(this);
    Error.captureStackTrace(this, HttpRequestError);
};
HttpRequestError.prototype.toString = function() {
    return "HttpRequestError " + this.status + " " + this.body;
}
HttpRequestError.prototype.__proto__ = Error.prototype;


request.HttpRequestError = HttpRequestError;
