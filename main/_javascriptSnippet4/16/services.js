'use strict';

/* Services */

angular.module('cryptzone.services', [])
    .factory('api', function ($rootScope, $http, $window) {
      /**
       * HTTP service providing access the Cryptzone backend API.
       */
      var apiBase = 'api' /* base api uri */,
          headers = {Authorization: 'Bearer ' + ($window.sessionStorage.token || $window.localStorage.token)};

      return {
        users: {
          list: function () {
            return $http({method: 'GET', url: apiBase + '/users', headers: headers});
          },
          create: function (user) {
            return $http({method: 'POST', url: apiBase + '/users', data: user, headers: headers});
          },
          delete: function (id) {
            return $http({method: 'DELETE', url: apiBase + '/users/' + id, headers: headers});
          }
        }
      };
    });