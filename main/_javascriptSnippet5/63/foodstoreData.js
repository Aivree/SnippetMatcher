'use strict';

//DATA PERSISTER
foodstoreApp.factory('foodstoreData', function ($http, $q) {
    //var baseUrl = 'https://api.github.com/users/Plamen5kov';
    //var deferred = $q.defer();
    //var baseUrl = 'http://foodplace.apphb.com/api/'
    var baseUrl = 'http://localhost:9086/'

    return {
        getCategories: getCategories,
        register: register,
        login: login,
        getProductsByCategory: getProductsByCategory,
    };

    function login(user) {
        var deferred = $q.defer();

        var body = {
            'grant_type': 'password',
            'username': user.email,
            'password': user.password,
        };

        $http.post(
            baseUrl + 'Token',
            body, {
                transformRequest: function (body) {
                    var str = [];
                    for (var p in body)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(body[p]));
                    return str.join("&");
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }
            )
            .success(function (data) {
                deferred.resolve(data);
            })
            .error(function (data, status, headers, config) {
                console.log('-------login error');
                console.log('data: ' + data);
                console.log('status: ' + status);
                deferred.reject();
            });

        return deferred.promise;
    }

    function register(registerData) {
        var deferred = $q.defer();

        var body = {
            'Email': registerData.email,
            Password: registerData.password,
            ConfirmPassword: registerData.confirmPassword
        };

        $http.post(
            baseUrl + 'api/Account/Register',
            body, {
                transformRequest: function (body) {
                    var str = [];
                    for (var p in body)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(body[p]));
                    return str.join("&");
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }
            )
            .success(function (data) {
                deferred.resolve(data);
            })
            .error(function (data, status, headers, config) {
                console.log('-------register error');
                console.log('data: ' + data);
                console.log('status: ' + status);
                deferred.reject();
            });

        return deferred.promise;
    }

    //function register(registerData) {
    //    var deferred = $q.defer();

    //    console.log(registerData);
    //    var body = {
    //        'Email': registerData.email,
    //        Password: registerData.password,
    //        ConfirmPassword: registerData.confirmPassword
    //    };

    //    $http({
    //        method: 'POST',
    //        url: baseUrl + 'api/Account/Register',
    //        data: body
    //    })
    //        .success(function (data, status, headers, config) {
    //            deferred.resolve(data);
    //        })
    //        .error(function (data, status, headers, config) {
    //            console.log('register error');
    //            deferred.reject();
    //        });

    //    return deferred.promise;
    //}

    function getCategories() {
        var deferred = $q.defer();
        var accesToken = localStorage.getItem('Bearer');
        
        var header = 'Bearer ' + accesToken;
        console.log(header);

        $http({
                  method: "GET",
                  url: baseUrl + 'api/Categories/GetCategories'
              })
            .success(function (data, status, headers, config) {
                deferred.resolve(data);
            })
            .error(function (data, status, headers, config) {
                console.log('error');
                deferred.reject();
            });

        return deferred.promise;
    }

    function getProductsByCategory(categoryId) {
        var deferred = $q.defer();

        $http({ method: "GET", url: baseUrl + 'api/Products/GetByCategory?categoryId=' + categoryId })
            .success(function (data, status, headers, config) {
                deferred.resolve(data);
            })
            .error(function (data, status, headers, config) {
                console.log('error');
                deferred.reject();
            });

        return deferred.promise;
    }
})