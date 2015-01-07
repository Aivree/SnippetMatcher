'use strict';

/* Controllers */

angular.module('odeck.controllers', [])
  .controller('MyCtrl1', ['$scope', function($scope) {

  }])
  .controller('loginController',  ['$scope', '$sails',function($scope,$sails) {
      console.log('we are on the login page');
       $scope.$on('$viewContentLoaded', function () 
                  {
                      
                      Login.init();
                      console.log($sails);
       
           });

  }])
  .controller('MyCtrl2', ['$scope', function($scope) {

  }]);
  //var app = angular.module('AngularSailsApp', ['angularSails.base']);
