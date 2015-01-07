app.directive('tabs', [function ($location) {
	return {
		restrict: 'E',
		controller: "TabController",
		templateUrl : "app/partials/_tabs.html",
		link: function (scope, iElement, iAttrs) {
		
		}

	};
}])