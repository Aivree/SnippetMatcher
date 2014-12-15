'use strict';
angular.module('twmobile.services').factory('AppointmentService', function($http, RootService) {
  return {
    all: function() {
    return $http({
        url: window.localStorage.host + '/appointments',
        headers: RootService.headers(JSON.parse(window.localStorage.user)),
        method: 'GET'
      });
    },
    get: function(id) {
    return $http({
        url: window.localStorage.host + '/appointments/' + id,
        headers: RootService.headers(JSON.parse(window.localStorage.user)),
        method: 'GET'
      });
    },
    post: function(object) {
    return $http({
        url: window.localStorage.host + '/appointments',
        headers: RootService.headers(JSON.parse(window.localStorage.user)),
        method: 'POST',
        data: object
      });
    },
    search: function(query) {
    return $http({
        url: window.localStorage.host + '/appointments?' + query,
        headers: RootService.headers(JSON.parse(window.localStorage.user)),
        method: 'GET'
      });
    }
  };
});