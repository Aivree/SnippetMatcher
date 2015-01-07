'use strict';

angular.module('digitalwnycomApp')
  .directive('siteNavItem', function () {
    return {
      templateUrl: 'views/directives/site/siteNavItem.html',
      restrict: 'E',
      scope: {},
      transclude: true,
      controller: ['$scope', '$element', '$attrs', '$location', function($scope, $element, $attrs, $location) {
        $scope.navigate = function() {
          $location.path($attrs.url);
        };
        $scope.isActive = function() {
          if ($location.path().indexOf($attrs.url) === 0) {
            return 'btn-primary';
          }
        };
      }]
    };
  });
