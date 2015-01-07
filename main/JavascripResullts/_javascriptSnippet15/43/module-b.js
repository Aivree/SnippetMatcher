(function() {
'use strict';

var module = angular.module('module-b', []);

module.directive('directiveB', function() { return {
    restrict: 'E',
    templateUrl: 'components/module-b/directive-b.html',
    scope: {},
    controller: function($scope,$http) {
        $http.get('api.json').then( function(promise) {
            angular.extend($scope,promise.data);
        });
    },
}});

}());
