app.directive('listitem', function() {
	return {
		restrict: 'E',
		scope: {
			item: '=',
			header: '=',
			img: '@',
			text: '@',
			secondary: '@'
		},
		replace: true,
		templateUrl: 'app/common/partials/listitem.html',
		controller: function($scope, $element, $attrs) {

		}
	};
});
