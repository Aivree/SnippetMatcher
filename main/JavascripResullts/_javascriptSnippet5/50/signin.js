/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var signin = angular.module("signin", ["flow", "ngCookies", "flowServices"]);

signin.controller("loginCtrl", function ($scope, $http, $cookies, flowMessageService, userSessionService) {
    $scope.login = function (user) {

        var request = $http({
            method: "post",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: {username: user.username, password: user.password},
            transformRequest: function (obj) {
                var str = [];
                for (var p in obj)
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            url: "http://localhost:9080/flow-services-1.0-SNAPSHOT/services/login_service/auth"
        });


        request.success(function (data, status, headers, config) {
            if (data) {
                $cookies.authorization = data.bs64auth;
            }
            window.location = "home.html";
        });

        request.error(function (data) {
            if (data) {
                flowMessageService.warning("signinMessage", data.msg, 2000);
            } else {
                flowMessageService.warning("signinMessage", "Invalid username or password", 2000);
            }
            flowMessageService.open();
        });

    };


});

