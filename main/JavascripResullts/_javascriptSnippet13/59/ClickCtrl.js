(function() {
	"use strict";

	angular.module('app', [])
	.controller('ClickCtrl', function($scope) {
		
		$scope.init = function() {
			$scope.count = 0;
		};
		
		$scope.increment = function() {
			$scope.count++;
		};
	});
})();