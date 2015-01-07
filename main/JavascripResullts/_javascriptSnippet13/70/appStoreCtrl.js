angular.module('prApp').controller("AppStoreCtrl", ['$scope', '$location',
function ($scope, $location) {
	$scope.request = false;

	init();
	
	function init() {
		
		NProgress.done();
	}
	
	
}]);