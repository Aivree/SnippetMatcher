'use strict';

angular.module('karcsiApp')
  .controller('NavbarCtrl', function ($scope, simpleLogin) {
    $scope.item = '';
    $scope.auth = simpleLogin.init();
    
    $scope.logout = function(){
      $scope.auth.$logout();
    };
  });
