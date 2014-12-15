/* jshint -W089 */

var activateCtrl = function ($scope, $location, $http, configService, $sce) {
	
	$scope.getPageToken = function() {
		return $location.path().split('/')[2]||'Unknown';
    };
	
	console.log($scope.getPageToken());
	
	$http({
        method  : 'POST',
        url     : configService.API_END_POINT+'/auth/activate/' + $scope.getPageToken(),
        data: {},
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
		//console.log('got here');
		//usernameService.setUsername('empty', 'empty', 'empty');
		
		if (data.status === 'Invalid token provided'){
			$scope.messageInvalid = true;
			$scope.message = $sce.trustAsHtml('Sorry, the token you provided is expired or not valid. <a href="#!/signup">Please sign up</a>');
		} else {
			$scope.messageInvalid = false;
			$scope.message =  $sce.trustAsHtml('Congratulations, <a href="#!/login">please login</a> to start uploading content');
		}
				
		//$location.path('/login');
    });


};

activateCtrl.$inject = ['$scope', '$location', '$http', 'configService', '$sce'];