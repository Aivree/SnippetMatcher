'use strict';
(function() {
	var app = angular.module('cmsFeedGroup', []);
	
	app.directive('wsMcNewsViewer', function() {
		return {
			restrict: 'EA',
			controller: 'cmsFeedGroupCtrl',
			templateUrl: "templates/ws-cms-ext-group.html"
		};
	});
	app.controller('cmsFeedGroupCtrl',['$attrs', '$scope', function ($attrs, $scope) {
		$scope.groupId = $attrs.id;
		$scope.groupMaximumResults = $attrs.maximumResults;
		$scope.groupShowFeatured = $attrs.showFeatured;
		$scope.groupIncludeSiteId = $attrs.includeSiteId;
		$scope.groupFeaturedDisplayTemplate = $attrs.featuredDisplayTemplate;
		$scope.groupFeedDisplayTemplate = $attrs.feedDisplayTemplate;
		$scope.groupFilters = JSON.parse(decodeURIComponent($attrs.feedGroups));
	}]);
})();