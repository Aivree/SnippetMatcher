'use strict';

/**
 * @ngdoc function
 * @name angularInitApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the angularInitApp
 */
angular.module('angularInitApp')
  .controller('AboutCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
