'use strict';

angular.module('te.teAdminApp')
  .directive('teInlineConfirm', function () {
    return {
      templateUrl: 'scripts/common/directives/inline-confirm/inline-confirm.html',
      restrict: 'E',
      scope:true,
      controller:'InlineConfirmController'
    };
  }).controller('InlineConfirmController', function($scope, $log,$parse,$element, $attrs) {
    $scope.state = 'prompt';
    $scope.label = $attrs.label;
    $scope.size = $attrs.size;

    /**
     * When Yes is Chosen this will be called
     */
    $scope.onConfirm = function() {
      $scope.state = 'prompt';
      //call the function passed in in attrs
      if ($attrs.confirm) {
        var confirm = $parse($attrs.confirm);
        $scope.$eval(confirm);
      }
    };

    /**
     * onCancel Action Handler
     */
    $scope.onCancel = function() {
      $scope.state='prompt';
      //call the function passed in
      if ($attrs.cancel) {
        var cancel = $parse($attrs.cancel);
        $scope.$eval(cancel);
      }
    };
  });
