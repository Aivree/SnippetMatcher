angular.module('CalculatorApp').directive('screen', function(){
  return {
    restrict: 'E',
    require: '^calculator',
    templateUrl: 'app/screen/screen.template.html',
    scope: {},
    controller: function($scope, memory) {
      $scope.memory = memory;
    },
    link: function(scope, element, attrs, CalculatorController) {

    }
  };
});