'use strict';
(function() {
	var app = angular.module('cmsMenu', []);
	
	app.directive('wsMenuHeaderSearchContent', function() {
		return {
			restrict: 'EA',
			controller: 'cmsMenuCtrl',
			templateUrl: "templates/ws-cms-ext-menu.html"
		};
	});
	app.controller('cmsMenuCtrl',['$attrs', '$scope', function ($attrs, $scope) {
		$scope.menuId = $attrs.id;
		$scope.menuTitle = $attrs.mhscTitle;
		$scope.menuLogo = $attrs.mhscLogo;
		$scope.menuSearch = $attrs.mhscSearch;
		$scope.menuContent = $attrs.mhscContent;
		$scope.menuFeedLayout = $attrs.mhscFeedLayout;
		$scope.menuFeedKey = $attrs.mhscFeedKey;
		$scope.menuFeedPagination = $attrs.mhscFeedPagination;
		$scope.menuFeedLinksPerPage = $attrs.mhscFeedLinksPerPage;
		$scope.menuCompact = $attrs.megaCompact;
	}]);
})();