var app = angular.module('sortableTable', [])
  .directive('sortable', function() {
    return {
      scope: true,
      controller: function($scope, $element, $attrs) {
        $scope.predicate = '';
        $scope.reverse = false;
        $scope.sortOn = function(predicate) {
          if ($scope.predicate == predicate) {
            $scope.reverse = !$scope.reverse;
          } else {
            $scope.reverse = false;
            $scope.predicate = predicate;
          }
        }
      }
    }
  })
  .directive('sorter', function() {
  return {
    scope: true,
    controller: function($scope, $element, $attrs) {
      $scope.sortOnMe = function() {
        $scope.$parent.sortOn($scope.predicate);
      }
    },
    templateUrl: '/views/core/sortableRow.html',
    link: function($scope, $element, $attrs) {
      $scope.name = $scope.$eval("'" + $attrs.sorterName + "'");
      $scope.predicate = $scope.$eval("'" + $attrs.sorterPredicate + "'");
      if ($scope.$eval("'" + $attrs.sorterDefault + "'") == true) {
        $scope.sortOnMe();
        console.log("sorting on " + $scope.name);
      }
    }
  }
});
