'use strict';

angular.module('bidApp')
  .directive('bidFilter', function() {
    return {
      templateUrl: 'views/bid-filter.html',
      controller: 'FilterCtrl',
      restrict: 'E',
      scope: {data:'=data'}//,
//      link: function postLink(scope, element, attrs) {
//        scope.$watch(attrs.data, function(value) {
//          scope.data = value;
//        });
//      }
    };
  })
;
