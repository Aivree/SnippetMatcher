angular.module('templateDirective', [])
  .controller('Controller', ['$scope', function($scope) {
    $scope.name = 'John Templeton';
  }])
  .directive('templateDirective', function() {
    return {
      templateUrl: 'templates/cd2_template.html'
    };
  });