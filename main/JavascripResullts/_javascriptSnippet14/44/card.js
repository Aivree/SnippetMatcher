'use strict';

angular.module('agileApp')
  .directive('card', function () {
    return {
      templateUrl: 'views/card.html',
      restrict: 'EA',
      replace: true,
      scope: true,
      controller: ['$scope', '$parse', '$attrs', function ($scope, $parse, $attrs) {
        $scope.reorder = function ($event, delta) {
          var $el = angular.element($event.target),
            model = $parse($attrs.card)($el.scope());

            $scope.$emit('card.reorder', model, delta);
        };
      }],
      link: function ($scope, $element, $attrs) {
      }
    };
  });
