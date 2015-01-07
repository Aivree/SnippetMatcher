'use strict';

angular.module('eventsAppApp')
    .directive('feedmodule', function() {
        return {
            templateUrl: 'views/partials/feedmodule.html',
            replace: true,
            transclude: true,
            restrict: 'A',
            link: function($scope, $element, $attrs) {
                $scope.url = $attrs.url;
            },
            controller: function($scope, $element, $attrs) {
                $scope.url = $attrs.url;
            }
        };
    });