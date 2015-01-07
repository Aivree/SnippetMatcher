var app = angular.module("app", []);

app.controller("Login", function($scope, $http, $location, transformRequestAsFormPost) {

	$scope.cfdump = "";

	var request = $http({
		method : "post",
		url : "http://localhost:8080/interview/j_spring_security_check",
		transformRequest : transformRequestAsFormPost,
		data : {
			username : "library@epam.com",
			password : "123456",
			redirect : ""
		}
	});

	request.success(function(html) {
		window.location = "/interview/orders/allOrders";
		$scope.cfdump = html;
	});
}

);

app.factory(
				"transformRequestAsFormPost",
				function() {

					function transformRequest(data, getHeaders) {

						var headers = getHeaders();
						headers["Content-type"] = "application/x-www-form-urlencoded; charset=utf-8";
						return (serializeData(data));

					}

					return (transformRequest);
					function serializeData(data) {

						if (!angular.isObject(data)) {
							return ((data == null) ? "" : data.toString());
						}

						var buffer = [];

						for ( var name in data) {
							if (!data.hasOwnProperty(name)) {
								continue;
							}

							var value = data[name];

							buffer.push(encodeURIComponent(name)
									+ "="
									+ encodeURIComponent((value == null) ? ""
											: value));

						}

						var source = buffer.join("&").replace(/%20/g, "+");

						return (source);

					}
				});

app.value("$sanitize", function(html) {
	return (html);
});