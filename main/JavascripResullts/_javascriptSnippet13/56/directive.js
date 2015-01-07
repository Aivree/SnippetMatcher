'use strict';

/**
 * Data Bind Controller
 */

angular.module('lcboApp.controllers').
    controller('DirectiveCtrl', ['$scope', function($scope) {

        $scope.config = {
            title: 'Directive Example'
        }

        $scope.init = function() {
        }
    }]);