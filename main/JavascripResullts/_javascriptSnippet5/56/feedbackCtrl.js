/* jshint -W089 */

var feedbackCtrl = function ($scope, $location, $http, configService, $sce) {
	
	$scope.processForm = function(isValid){
		if (isValid){
			console.log($scope.feedback);
			var postData = {
				feedback: $scope.feedback
			};
			$http({
				method  : 'POST',
				url     : configService.API_END_POINT+'/feedback/add/',
				data: postData,
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
		
				$scope.message =  $sce.trustAsHtml('Thanks for the feedback, we hope you continue using our site :)');
				$scope.submittedError = false;
				//$location.path('/login');
			});
		} else{
			//console.log('error');
			$scope.submittedError = true;

		}
	};
};

feedbackCtrl.$inject = ['$scope', '$location', '$http', 'configService', '$sce'];