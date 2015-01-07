(function () {
    'use strict';
    var appModule = angular.module('mainApp');
    appModule.controller('dashboardController', ['$scope',
        function dashboardController($scope) {
            $scope.Message = "";

            $scope.init = function () {
                $scope.Message = "Welcome To Aprimo Utility Application!";
            };

            $scope.init();

        }]);

}());