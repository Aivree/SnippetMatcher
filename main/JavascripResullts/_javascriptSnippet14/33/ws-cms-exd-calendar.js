'use strict';
(function() {
	var app = angular.module('cmsCalendar', []);
	
	app.directive('wsCalendar', function() {
		return {
			restrict: 'EA',
			controller: 'cmsCalendarCtrl',
			templateUrl: "templates/ws-cms-ext-calendar.html"
		};
	});
	app.controller('cmsCalendarCtrl', ['$attrs', '$scope', function ($attrs, $scope) {
		$scope.calTitle=$attrs.title;
		$scope.calDisplayTemplate = $attrs.displayTemplate;
	}]);
})();