var base = "http://inbusinessco.com/mangle/"
angular.module('starter.services', []).service('PostService', function($q, $http, $ionicPopup, $state) {
    $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
    return {
        post: function(controller, action, xsrf) {
            var defer = $q.defer();
            $http({
                method: 'POST',
                url: base + controller + '/' + action + '.json',
                transformRequest: function(obj) {
                    var str = [];
                    for (var p in obj) str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: xsrf
            }).success(function(data) {
                defer.resolve(data);
            }).error(function() {
                defer.resolve({
                    _code: 0,
                    _message: 'Error en la conexion por favor verifique su conexion a internet'
                });
                var alertPopup = $ionicPopup.alert({
                    title: 'Network Status',
                    template: 'Error en la conexion por favor verifique su conexion a internet'
                });
                alertPopup.then(function() {
                    $state.go('app.internet');
                });
            });
            return defer.promise;
        }
    }
}).service('GetService', function($q, $http, $ionicPopup, $state) {
    return {
        get: function(controller, action, xsrf) {
            var defer = $q.defer();
            var data = "?"
            for (key in xsrf) {
                data += key + "=" + xsrf[key] + "&"
            }
            $http({
                method: 'GET',
                url: base + controller + '/' + action + '.json' + data,
            }).success(function(data) {
                defer.resolve(data);
            }).error(function(data, status, headers, config) {
                defer.resolve({
                    _code: 0,
                    _message: 'Error en la conexion por favor verifique su conexion a internet'
                });
                var alertPopup = $ionicPopup.alert({
                    title: 'Network Status',
                    template: 'Error en la conexion por favor verifique su conexion a internet'
                });
                alertPopup.then(function() {
                    $state.go('app.internet');
                });
            });
            return defer.promise;
        }
    }
}).service('testService', function($q, $http, $ionicPopup, $state) {
    $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
    return {
        post: function(xsrf) {
            var defer = $q.defer();
            $http({
                method: 'POST',
                url: 'http://localhost:3000/location/sucursal.json',
                transformRequest: function(obj) {
                    var str = [];
                    for (var p in obj) str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: xsrf
            }).success(function(data) {
                defer.resolve(data);
            }).error(function() {
                defer.resolve({
                    _code: 0,
                    _message: 'Error en la conexion por favor verifique su conexion a internet'
                });
                var alertPopup = $ionicPopup.alert({
                    title: 'Network Status',
                    template: 'Error en la conexion por favor verifique su conexion a internet'
                });
                alertPopup.then(function() {
                    $state.go('app.internet');
                });
            });
            return defer.promise;
        }
    }
})