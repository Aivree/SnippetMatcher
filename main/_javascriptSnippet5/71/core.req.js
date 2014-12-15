//==============================================
// core - req
//==============================================

(function() {
    'use strict';

    angular.module('app.core').factory('req', ['$http', '$q', function($http, $q) {

        return {
            get: makeGetRequest,
            post: makePostRequest,
            update: makePutRequest,
            delete: makeDeleteRequest,
            request: makeRequest,
            all: all
        };



        //==============================================



        function makeGetRequest(url, query) {
            return function(newQuery) {
                return makeRequest('get', url, null, newQuery || query, null);
            }
        }


        function makePostRequest(url, data, multipart) {
            return function(newData) {
                return makeRequest('post', url, newData || data, null, multipart);
            }
        }


        function makePutRequest(url, data, multipart) {
            return function(newData) {
                return makeRequest('put', url, newData || data, null, multipart);
            }
        }


        function makeDeleteRequest(url, data, multipart) {
            return function(newData) {
                return makeRequest('delete', url, newData || data, null, multipart);
            }
        }


        function makeRequest(method, url, data, query, multipart) {

            //create query string
            var queryString = (query) ? '?' + encodeURIString(query) : '';

            //configure request
            var config = {
                method: method,
                url: url + queryString,
                data: data,
                withCredentials: true
            };

            //add request transform function
            if (multipart) {

                //don't serialise data
                config.transformRequest = angular.identity;

                //multipart data
                config.headers = {
                    'Content-Type': undefined
                };

            } else {

                //url encode data
                config.transformRequest = encodeURIString;

                //set urlencoded
                config.headers = {
                    'Content-Type': "application/x-www-form-urlencoded; charset=utf-8"
                };
            }

            //make request
            return $http(config);
        }


        function all(promises) {
            return $q.all(promises);
        }



        //==============================================



        function encodeURIString(obj) {

            var str = [];
            for (var p in obj) {
                if (obj.hasOwnProperty(p)) {

                    var value = obj[p];

                    //stringify if value is array or object
                    if ((value instanceof Array) || (value instanceof Object)) {
                        value = JSON.stringify(value);
                    } else {
                        value = encodeURIComponent(value);
                    }

                    str.push(encodeURIComponent(p) + "=" + value);
                }
            }

            //return encoded string
            return str.join("&");
        }
    }]);

})();
