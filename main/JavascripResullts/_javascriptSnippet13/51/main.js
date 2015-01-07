'use strict';

angular.module('angularApp')
  .controller('MainCtrl',['$scope', 'FarmerData', function ($scope, FarmerData) {
        $scope.farmers = [];

        $scope.searchfarmer = function(){
            console.log("called farmer");


        }

        $scope.init = function(){
            $scope.farmers = FarmerData.query();
            console.log($scope.farmers);
        };

        $scope.init();
  }]);
