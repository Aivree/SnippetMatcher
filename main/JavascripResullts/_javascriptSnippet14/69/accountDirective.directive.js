'use strict';

angular.module('rippleDashApp')
  .directive('accountDirective', function () {
    return {
      templateUrl: 'app/accountDirective/accountDirective.html',
      restrict: 'EA',
      scope: {},
      controller: function($scope, $element, $attrs, $transclude, $http) {
        $scope.walletid = $attrs.wallet;
        $scope.title = "Pietje";

        $http.get("/api/customers/" + $scope.walletid).success(function(customerlist) {
          $scope.customers = customerlist;
          if (!$scope.currentcustomer) {
            $scope.account = {
              name : $scope.customers[0].name
            };
          };
        });

        // $scope.account = {
        //   name: "Rob",
        //   balance: 100
        // };
      }
    };
  });
