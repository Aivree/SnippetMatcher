angular.module('app').directive('appDirective', function() {
	return {
		restrict: 'E',
		replace: true,
		scope: {

		},
		templateUrl: 'directive/app-directive/app-directive.html',
		link: function(scope, element, attrs, fn) {


		}
	};
});
