(function(){

	'use strict';

	/* Controllers */


	var CCP_monitorControllers = angular.module('CCP_monitorControllers', []);

	CCP_monitorControllers.controller('TerminalDetailCtrl', ['$scope', '$http',
		function($scope, $http){
			$http({
				method: 'GET',
				url: '/getData'
			}).
				success(function(data, status, headers, config){
					console.log(data);
					$scope.tableHeaders = data.headers;
					$scope.tableRows = data.data;
				}).
				error(function(data, status, headers, config){
					console.log(data);
				});
		}
	]);

	// angular.module('CCP_monitor.controllers', [])
	// .controller('dataCtrl', ['$scope', '$http', function($scope, $http) {

	// 	$http({
	// 		method: 'GET',
	// 		url: '/getData'
	// 	}).
	// 		success(function(data, status, headers, config){
	// 			console.log(data);
	// 			$scope.tableHeaders = data.headers;
	// 			$scope.tableRows = data.data;
	// 		}).
	// 		error(function(data, status, headers, config){
	// 			console.log(data);
	// 		});
	// }]);

})();