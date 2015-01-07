angular.module('tpo', ['ui']);

function RolesCtrl($scope) {

	$scope.$root.getRoles = function(){
		var config = {
			method:'GET',
			url:'/request',
			headers:{resource:'/roles'}
		};
		$http(config).
			success(function(data, status, headers, config) {
				$scope.pages = data;
			}).
			error(function(data, status, headers, config) {
			});
	};

}
