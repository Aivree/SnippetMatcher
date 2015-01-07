'use strict';

/* Directives */


angular.module('dbDD.directives', [])
        .directive('appVersion', ['version', function(version) {
                  return function(scope, elm, attrs) {
                       elm.text(version);
                  };
             }])
        .directive('physical', function() {
                  return{
                       restrict: 'E',
                       templateUrl: 'templates/details/physical.html',
                       controller: 'charController',
                       link: function(scope, element, attrs, controller) {
                       }
                  };
             })
        .directive('race', function() {
                  return{
                       restrict: 'E',
                       templateUrl: 'templates/details/race.html',
                       controller: 'charController',
                       link: function(scope, element, attrs, controller) {
                       }
                  };
             });
