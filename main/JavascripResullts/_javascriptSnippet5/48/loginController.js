angular.module("wazza")
    .constant("authUrl", "http://localhost:8090/wazza/authenticate")
    .controller("authenticationCtrl", function ($scope, $http, $location, $resource, authUrl) {

        $scope.username = "";
        $scope.password = "";

        $scope.connected = false;

        $scope.authenticate = function () {

            return $http({
                method: 'POST',
                url: authUrl,
                transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: ({username:$scope.username,password:$scope.password}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).
                success(function (data, status, headers, config) {
                    // this callback will be called asynchronously
                    // when the response is available
                    $scope.connected = true;
                }).
                error(function (data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                    $scope.connected = false;
                });
        };
    });
