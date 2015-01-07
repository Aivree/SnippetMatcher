(function () {
    'use strict';
    var appModule = angular.module('mainApp');
    appModule.controller('loginController', ['$scope', '$location', 'authentication', function ($scope, $location, authentication) {

        $scope.LoginModel = {};

        $scope.init = function () {
            $scope.User = angular.copy($scope.LoginModel);
        };

        $scope.login = function () {
            authentication.authenticate($scope.LoginModel);
        };

        $scope.cancel = function () {
            $scope.reset();
            $location.path('/');
        };

        $scope.reset = function () {
            $scope.LoginModel = {};
        };

        $scope.init();

    }]);

}());