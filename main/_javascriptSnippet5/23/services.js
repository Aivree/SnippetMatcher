(function (angular, appName) {
    'use strict';

    var safeApply = function (scope) {
        scope.$evalAsync();
    };

    var services = angular.module(appName+'.Services', []);

    services.factory('$httpphp', ['$http',
        function ($http) {
            return function $httpphp (url, method, data) {
                if (!method) {
                    method = "GET";
                }
                if (!data) {
                    data = {};
                }
                return $http({
                    url: url,
                    method: method,
                    data: data,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                    transformRequest: function (obj) {
                        var str = [];
                        for (var key in obj) {
                            if (obj[key] instanceof Array) {
                                for (var idx in obj[key]) {
                                    var subObj = obj[key][idx];
                                    for (var subKey in subObj) {
                                        str.push(encodeURIComponent(key) + "[" + idx + "][" + encodeURIComponent(subKey) + "]=" + encodeURIComponent(subObj[subKey]));
                                    }
                                }
                            }
                            else {
                                str.push(encodeURIComponent(key) + "=" + encodeURIComponent(obj[key]));
                            }
                        }
                        return str.join("&");
                    }
                });
            };
        }]);

})(angular, 'main');