define([
  'angular',
  'app',
  'lodash'
], function (angular) {
  'use strict';

  var module = angular.module('tmpst.controllers');

  module.controller('DashCtrl', function ($scope) {
    $scope.init = function () {
      $scope.dashboard = {
        current: {
          style: 'dark',
          title: 'Dashboard'
        }
      };
    };

    $scope.init();
  });
});
