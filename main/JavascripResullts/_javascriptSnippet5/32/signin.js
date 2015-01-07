'usr strict';

(function() {
	var app = angular.module('login', []);

	app.controller('LoginController', ['$http','$scope' , function($http, $scope) {
		$scope.user = {};
		$scope.state = {};
		$scope.submit = function() {
			$http({
				method: 'POST',
				url: '/users/login',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				transformRequest: function(obj) {
					var str = [];
					for(var p in obj)
						str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
					return str.join("&");
				},
				data: { user : JSON.stringify($scope.user) }
			})
			.success( function(result) {
				if (result.success) {
					location.href="/support/userinfo";
				} else {
					$scope.state = result;
				}
			})
			.error( function(result) {
				$scope.state = result;
			});
		}
	}]);

})();
