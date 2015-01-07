'use strict';

/* Controllers */
angular.module('app')
.controller('AppCtrl',
['$rootScope', '$scope', '$location', 'SessionService', function($rootScope, $scope, $location, SessionService ) {
	// if( $location.path() != '/404' )
	// {
		//$rootScope.cloud = true;
		gotcha_sky.init();
		gotcha_index_anime.init();	
	// }
}]);
// Controller Home
angular.module('app')
.controller('HomeCtrl',
['$rootScope', function($rootScope) {
	
}]);
angular.module('app')
.controller('ContactCtrl',
['$scope', '$http', '$location', function($scope, $http, $location) {
	$scope.mail = { name: '', email: '', phone: '', subject: '', message: '' }; 
	$scope.send = function(mail){
		console.log(mail);
	}
}]);
angular.module('app')
.controller('AboutCtrl',
['$scope', '$http', '$location', function($scope, $http, $location) {
	
}]);
angular.module('app')
.controller('404Ctrl',
['$scope', '$http', '$location', function($scope, $http, $location) {
	
}]);
