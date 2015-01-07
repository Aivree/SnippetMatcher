angular.module('treasureTrolls')
  .directive('trollsDirective', function () {
    return {
      restrict: 'E',
      scope: {
        trollInfo: '=info'
      },
      templateUrl: 'modules/trolls/trolls.html'
    }
  });