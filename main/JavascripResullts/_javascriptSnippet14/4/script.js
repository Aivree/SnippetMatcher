var app = angular.module('myApp', []);
app.directive('slider', function() {
  return {
    restrict: 'AEC',
    templateUrl: 'slider.html',
    controller: ['$scope', '$attrs',
      function($scope, $attrs) {
        $scope.left = $attrs.lmr.split(",")[0];
        $scope.middle = $attrs.lmr.split(",")[1];
        $scope.right = $attrs.lmr.split(",")[2];
        $scope.selectedBubble = 1;

        
       

        $scope.clickBubble = function(selected) {
          $scope.selectedBubble = selected;
          

        };

        $scope.isSelected = function(num) {
          return $scope.selectedBubble === num;

        };


      }
    ]
  };
});