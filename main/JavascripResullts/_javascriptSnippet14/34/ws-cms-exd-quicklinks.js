'use strict';
(function() {
	var app = angular.module('cmsQuicklinks', []);
	
	app.directive('wsQuicklinks', function() {
		return {
			restrict: 'EA',
			controller: 'cmsQuicklinksCtrl',
			templateUrl: "templates/ws-cms-ext-quicklinks.html"
		};
	});
	app.controller('cmsQuicklinksCtrl', ['$attrs', '$scope', function ($attrs, $scope) {	
		$scope.quickId = $attrs.id;
		$scope.quickLinks = JSON.parse(decodeURIComponent($attrs.links));
	}]);
})();