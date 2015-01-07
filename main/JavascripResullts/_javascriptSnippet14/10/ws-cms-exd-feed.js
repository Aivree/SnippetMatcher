'use strict';
(function() {
	var app = angular.module('cmsFeedViewer', []);
	
	app.directive('wsCfgFeedViewer', function() {
		return {
			restrict: 'EA',
			controller: 'cmsFeedCtrl',
			templateUrl: "templates/ws-cms-ext-feed.html"
		};
	});
	app.controller('cmsFeedCtrl', ['$attrs', '$scope', function ($attrs, $scope) {
		$scope.feedId = $attrs.id;
		$scope.feedMaximumResults = $attrs.maximumResults;
		$scope.feedDisplayTemplate = $attrs.displayTemplate;
	}]);
})();