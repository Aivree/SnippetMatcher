'use strict';

app.factory('apiService', function($http, $q) {
    return {
        getUser: function() {
            var d = $q.defer();
            $http({method: 'GET', url: '/user'}).
                success(function(data, status, headers, config) {
                    d.resolve(data);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        },
        postLogout: function () {
            var d = $q.defer();
            $http({method: 'POST', url: '/logout'}).
                success(function(data, status, headers, config) {
                    d.resolve(data);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        },
        getEntries: function(page) {
            var d = $q.defer();
            $http({method: 'GET', url: '/entries/page:' + page}).
                success(function(data, status, headers, config) {
                    d.resolve(data);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        },
        getEntry: function(id) {
            var d = $q.defer();
            $http({method: 'GET', url: '/entries/' + id}).
                success(function(data, status, headers, config) {
                    d.resolve(data.entry);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        },
        postEntry: function (content) {
            var d = $q.defer();
            var data = {
                content: content
            }
            $http({method: 'POST', url: '/entries', data: data}).
                success(function(data, status, headers, config) {
                    d.resolve(data);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        },
        updateEntry: function (id, content) {
            var d = $q.defer();
            var data = {
                content: content
            }
            $http({method: 'PUT', url: '/entries/' + id, data: data}).
                success(function(data, status, headers, config) {
                    d.resolve(data);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        },
        deleteEntry: function (id) {
            var d = $q.defer();
            $http({method: 'DELETE', url: '/entries/' + id}).
                success(function(data, status, headers, config) {
                    d.resolve(data);
                }).
                error(function(data, status, headers, config) {
                    d.reject();
                });
            return d.promise;
        }
   }
});