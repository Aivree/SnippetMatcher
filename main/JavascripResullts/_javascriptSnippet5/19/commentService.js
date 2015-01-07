/* jshint -W089 */

app.service('commentService', ['$http', '$location', 'configService',  function($http, $location, configService) {

		return {

			// save a comment (pass in comment data)
			save : function(comment, currentId, userToken) {
				console.log('token to ship' + userToken);
				return $http({
					method: 'POST',
					url: configService.API_END_POINT+ 'comment/add',
					data: {'comment': comment, 'userToken': userToken, 'objectId': currentId},
					transformRequest: function(obj) {
						var str = [];
						for(var p in obj) {
							str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
						}
						return str.join('&');
					},
					headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  // set the headers so angular passing info as form
				});
			}
		};

	}]);