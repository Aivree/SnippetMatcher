/**
 * Created by fausto on 11/24/14.
 */
'use strict';

angular.module('Services').service('ApiHttpSrv', function($http, ConfigurationSrv, md5) {
  var _createHttpConfig,
      _createHttpRequest,
      _createApiHttp,
      _createApiHttpUrl;

  _createHttpConfig = function(type, url, data, extraHeaders, headersToRemove) {
    var defaultHeaders,
        headers;

    defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    headers = angular.extend(defaultHeaders, extraHeaders);
    if(headersToRemove) {
      _.each(headersToRemove, function(headerToRemove) {
        delete $http.defaults.headers.common[headerToRemove];
        delete headers[headerToRemove];
      });
    }

    return {
      method: type,
      url: url,
      data: data,
      cache: false,
      headers: headers
    };
  };
  _createHttpRequest = function(type, url, params,data, extraHeaders, headersToRemove) {
    var httpConfig,
        publicKey,
        privateKey,
        finalParams,
        timeStamp,
        md5Hash;

    publicKey = ConfigurationSrv.getPublicKey();
    privateKey = ConfigurationSrv.getPrivateKey();

    finalParams = '?' + 'apikey=' + publicKey;
    if(privateKey) {
      timeStamp = new Date();
      timeStamp = timeStamp.valueOf();
      md5Hash = md5.createHash(timeStamp + privateKey + publicKey);
      finalParams = finalParams + '&ts=' + timeStamp + '&hash=' + md5Hash;
    }
    if(params) {
      params = _.isString(params)? params: $.param(params);
      finalParams = finalParams + '&' + params;
    }

    url = url + finalParams;
    httpConfig = _createHttpConfig(type, url, data, extraHeaders, headersToRemove);

    return $http(httpConfig);
  };

  //nameOrUrlInd indicates if nameOrUrl param is the name of an endpoint to get via ConfigurationSrv, or if it is a url
  //true for name, false for url
  _createApiHttp = function(type, name, params, data, extraHeaders, headersToRemove) {
    var url;
    url = ConfigurationSrv.getApiUrl(name);

    return _createHttpRequest(type, url, params, data, extraHeaders, headersToRemove);
  };

  _createApiHttpUrl = function(type, url, params, data, extraHeaders, headersToRemove) {
    return _createHttpRequest(type, url, params,data, extraHeaders, headersToRemove);
  };

  return {
    createApiHttp: _createApiHttp,
    createApiHttpUrl: _createApiHttpUrl
  };
});
