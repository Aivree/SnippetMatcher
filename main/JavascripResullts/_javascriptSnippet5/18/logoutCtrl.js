/* jshint -W089 */

var logoutCtrl = function ($scope, $location, $http, configService, usernameService) {
    console.log(usernameService.token());
	console.log($scope.token);
	$http({
        method  : 'POST',
        url     : configService.API_END_POINT+'auth/logout',
        data: {'token': usernameService.token()},
        transformRequest: function(obj) {
            var str = [];
            for(var p in obj) {
                str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
            }
            return str.join('&');
        },
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  // set the headers so angular passing info as form data (not request payload)
    })
    .success(function(data) {
		console.log('got here');
		usernameService.setUsername('empty', 'empty', 'empty');
				
		$location.path('/login');
    });

};

logoutCtrl.$inject = ['$scope', '$location', '$http', 'configService', 'usernameService'];