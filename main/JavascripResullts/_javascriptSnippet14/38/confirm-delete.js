'use strict';

angular.module('te.teAdminApp')
  .directive('teConfirmDelete', function () {
    return {
      templateUrl: 'scripts/common/directives/confirm-delete/confirm-delete.html',
      restrict: 'E',
      scope:true,
      controller:'ConfirmDeleteController'
    };
  }).controller('ConfirmDeleteController', function($scope, $log,$parse,$element, $attrs) {
    $scope.state = 'delete';
    $scope.label = $attrs.label;
    $scope.size = $attrs.size;


    /**
     * When Yes is Chosen this will be called
     */
    $scope.onConfirm = function() {
      $scope.state = 'delete';
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
      $scope.state='delete';
      //call the function passed in
      if ($attrs.cancel) {
        var cancel = $parse($attrs.cancel);
        $scope.$eval(cancel);
      }
    };
  });
