'usr strict';

$(function() {
    $( "#datepicker" ).datepicker({
      changeMonth: true,
      changeYear: true,
      yearRange: "1970:2014",
      defaultDate : "-20y"
    });
  });


(function() {
	var app = angular.module('signup', []);

	app.controller('SignUp', ['$http','$scope' , function($http, $scope) {
		$scope.user = {};
		$scope.state = {};
		$scope.birthday = "";
		$scope.submit = function() {
			$http({
				method: 'POST',
				url: '/users/signup',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				transformRequest: function(obj) {
					var str = [];
					for(var p in obj)
						str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
					return str.join("&");
				},
				data: { user : JSON.stringify($scope.user), birthday: $scope.birthday }
			})
			.success( function(result) {
				if (result.success) {
					location.href="/userinfo.jsp";
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
