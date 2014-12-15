function loginController($scope, $rootScope, $http, $cookieStore, $location) {
    $scope.username = "";
    $scope.password = "";

    $scope.login = function() {
        var authdata = btoa($scope.username + ':' + $scope.password);
        $http.get("api/v1/login/", {headers: {"Authorization": "Basic " + authdata}}).
            success(function(data, status, headers, config) {
                $http({
                    method: "POST",
                    url: "api/v1/o/token/",
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    // Modify the `transformRequest` to url-encode the data (instead of passing in the request body)
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {
                        client_id: data.results[0].client_id,
                        client_secret: data.results[0].client_secret,
                        grant_type: "password",
                        username: $scope.username,
                        password: $scope.password
                    }
                }).success(function(data, status, headers, config) {
                    $cookieStore.put("userCookie", data.access_token);
                    $rootScope.setAuthToken(data.access_token);
                    $location.path("/");
                }).error(function(data, status, headers, config) {
                    console.log(data);
                });
            });
    };
}