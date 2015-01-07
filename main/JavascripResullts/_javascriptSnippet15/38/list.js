angular.module('dreamApp')
	.directive('list', function () {
		return {
			restrict: 'E',
			scope: {
			},
			templateUrl: 'components/view/list.html'
		};
	})
;