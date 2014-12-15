indexApp.factory('reportFactory', function($http){
    var factory = {};           
    
    factory.getAuthors = function(){
        var promise = $http({
            url:'./Functions/Users/getUsers.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                var str = [];
                for(var p in obj)
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            data : { visibility : 'true' }
        })

        return promise;
    };

    factory.getReport = function(datefrom,dateto,author){
        var code_arr = { 
                            datefrom : datefrom,
                            dateto : dateto,
                            author : author
                        };

        var promise = $http({
            url:'./Functions/Report/getReport.php',
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

    factory.deletecall = function(data){
        var code_arr = { 
                            pk : data[0],
                        };

        var promise = $http({
            url:'./Functions/Report/delete.php',
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

    factory.currentuser = function(){
        var promise = $http({
            url:'./Functions/Users/getloggeduser.php',
            method: 'POST'
        })

        return promise;
    };

    return factory;
});