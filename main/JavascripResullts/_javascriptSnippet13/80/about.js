'use strict';

/**
 * @ngdoc function
 * @name initApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the initApp
 */
angular.module('tasksApp')
  .controller('AboutCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
