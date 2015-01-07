app.directive('listview', function() {
	return {
		restrict: 'E',
		scope: {
			data: '=',
			header: '=',
			img: '@',
			text: '@',
			secondary: '@'
		},
		replace: true,
		templateUrl: 'app/common/partials/listview.html',
		controller: function($scope, $element, $attrs) {

		}
	};
});
