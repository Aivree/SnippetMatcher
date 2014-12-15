/**
 * Created by nicolas on 08/05/14.
 */
angular.module('login', []).controller('LoginCtrl',['$scope', '$http', 'loggedUser', function($scope, $http, loggedUser){


    $scope.login = function(username, password) {

        $http({
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            url: '/loginservice',
            transformRequest: function(obj) {
                var str = [];
                for(var p in obj)
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            data: {username: username, password: password, redirect: 'false'}
        } ).success(function(data, status, headers, config) {
           loggedUser.username = username;
        });

    }
}]);