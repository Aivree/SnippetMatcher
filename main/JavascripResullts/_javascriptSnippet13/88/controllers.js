'use strict';

/* Controllers */

angular.module('myApp.controllers', []).
    controller('MyCtrl1', ["$scope", "BaseControllerFactory", 
        function($scope, BaseControllerFactory) {
            angular.extend(this, BaseControllerFactory.init($scope));
        }])
        .controller('MyCtrl2', [function() {

        }]);