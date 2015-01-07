'use strict';

/**
 * @ngdoc function
 * @name angularWebposApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the angularWebposApp
 */
angular.module('angularWebposApp')
    .controller('HomeCtrl', function ($scope) {
        Item.init_date();
        $scope.total_goods=count.get_total_cart();

    });