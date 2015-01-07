(function() {
'use strict';

var module = angular.module('module-a', []);

module.directive('directiveA', function() { return {
    restrict: 'E',
    templateUrl: 'components/module-a/directive-a.html',
    scope: {},
    controller: function($scope,$http) {
        $http.get('api.json').then( function(promise) {
            angular.extend($scope,promise.data);
        });
    },
}});

}());
