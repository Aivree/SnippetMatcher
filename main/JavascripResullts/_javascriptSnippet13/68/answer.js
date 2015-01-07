'use strict';

/* Controllers */

var answerApp = angular.module('answerApp', ['ngSanitize']);

answerApp.controller('answerCtrl', function($scope){
  $scope.init = function(answer){
    $scope.answer = answer;
  }
});
