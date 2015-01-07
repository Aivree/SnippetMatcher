var app = angular.module('nickiApp');
  
app.directive('ntResume', function() {
    return {
      restrict: 'E',
      scope: {
        category: '=',
      },
      templateUrl: 'resumeDirective.html'
    };
  });