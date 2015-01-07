angular.module("istim").controller("ItemsSellController", ['$scope', '$http', '$routeParams', function(scope, http, routeParams){

	scope.api_url = "http://istimitems-novo-istim-item.istimitems-novo.jit.su/useritem/sell/";
	if (!scope.session.sessionUser)
		scope.session.sessionUser = { id: '533ac1bda1659a5f58cda371' }

	http({
		method: 'POST',
		url: scope.api_url,
		headers: {'Content-Type': 'application/x-www-form-urlencoded'},
		transformRequest: function(obj) {
			var str = [];
			for(var p in obj)
			str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
			return str.join("&");
		},
		data: { user_id: scope.session.sessionUser.id, useritem_id: routeParams.id }
	}).success(function(data) {
		scope.message = data;
	})
	.error(function(data) {
		console.log('ERROR::' + data);
	});

}]);
