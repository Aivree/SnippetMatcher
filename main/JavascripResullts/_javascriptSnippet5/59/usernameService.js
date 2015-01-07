/* jshint -W089 */

app.service('usernameService', ['$cookies', '$http', 'configService', function($cookies, $http, configService) {

    var username = '';

    return{
        username: function() { return $cookies.username; },
		id: function() { return $cookies.id; },
		token: function() { return $cookies.token; },
		isActive: function() {
			console.log(this.token());
			var active = $http({
				method  : 'POST',
				url     : configService.API_END_POINT+'auth/active',
				data: {'token': this.token()},
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
				return data.active;
            });
			//console.log(active);
			return active;
		},
		setUsername: function(newUsername, id, token) { $cookies.username = newUsername; $cookies.id = id; $cookies.token = token; }
    };
   
}]);