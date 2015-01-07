'use strict';

/**
 * @ngdoc function
 * @name angularInitApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the angularInitApp
 */
angular.module('angularInitApp')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
