var app = angular.module('myApp', []);
app.controller('appCtrl', ['$scope', function ($scope) {
	$scope.model = {
		title : 'this is title',
		content : 'this is content'
	}
}])

app.directive('zippy', [function () {
	return {
		restrict: 'E',
		transclude : true,
		scope : {
			title : "@",
		},
		templateUrl : 'zippy.html',
		link: function (scope, iElement, iAttrs) {
			scope.isContentVisible = false;

			scope.toggleContent = function(){
				scope.isContentVisible = !scope.isContentVisible;
			}
		}
	};
}])