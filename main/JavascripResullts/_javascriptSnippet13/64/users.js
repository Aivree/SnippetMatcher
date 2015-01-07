'use strict';

angular.module('advancedjsApp')
  .controller('UsersCtrl', function ($scope, $log, $location, socket) {
    
    $scope.users = [];
        
    $scope.localScopeUsers = function() {
      $scope.users = $scope.cacheObject.users;
    };
    
    $scope._init_ = function() {
      $scope.localScopeUsers();
    };
    
    $scope._init_();

  });
  
  

