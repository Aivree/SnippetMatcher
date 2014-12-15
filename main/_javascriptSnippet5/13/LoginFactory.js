indexApp.factory('loginFactory', function($http){
    var factory = {};           
    
    factory.userlogin = function(login){
        var code_arr = { 
                            username : login.username,
                            password : login.password
                        };

        var promise = $http({
            url:'./Functions/Auth/Login.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                var str = [];
                for(var p in obj)
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            data : code_arr
        })

        return promise;
    };

    return factory;
});