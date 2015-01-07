'use strict';

angular.module('luceneApp')
  .directive('panel', ['$rootScope', function () {
    return {
      restrict: 'E',
      templateUrl: 'views/partials/panel.html',
      replace: true,
      scope: {
        model: '=',
        fields: '='
      },
      controller: 'PanelCtrl',
      link: function (scope, element, attrs) {
        scope.index = parseInt(attrs.index, 10);
      }
    };
  }]);