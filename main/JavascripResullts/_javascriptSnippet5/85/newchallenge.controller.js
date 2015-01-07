'use strict';
angular.module('babulya')
  .controller('NewchallengeCtrl', function($scope, $state, $http, $localStorage, $window) {

    $scope.username = $localStorage.user.name;
    $scope.volunteer = OrmStorage.getStoredObject($state.params.id, 'volunteer');

    $scope.challenge = {
      user: $localStorage.user.id,
      volunteer: $scope.volunteer.id,
      lat: $localStorage.user.id,
      lng: $localStorage.user.id,
      address: null,
      dateTime: 12312,
    };

    if ($window.navigator.geolocation) {
      $window.navigator.geolocation.getCurrentPosition(function(position) {
        $scope.challenge.lat = position.coords.latitude;
        $scope.challenge.lng = position.coords.longitude;
      });

    }

    $scope.call = function() {

      $http({
        method: 'POST',
        url: 'http://velopatrol.in.ua/api/challenge/new',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        transformRequest: function(obj) {
          var str = [];
          for (var p in obj) {
            str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
          }
          return str.join('&');
        },
        data: $scope.challenge
      }).success(function(challenge) {
        console.log(challenge);

      });
    };
  });
