'use strict';

var app = angular.module('app', []);

app.directive('whoiam', function() {
	return {
		restrict : 'E',
		transclude : true,
		templateUrl : 'marquee.html',
		link: function($scope, element, attrs) {
			$scope.name = 'Niccolo';
		}
	};
}); 

angular.module('app')
.controller('appController', function ($scope) {

});