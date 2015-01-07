indexApp.factory('graphFactory', function($http){
    var factory = {};           
    
    factory.getGraph = function(datefrom,dateto,field){
        var code_arr = { 
                            datefrom : datefrom,
                            dateto : dateto,
                            field : field
                        };

        var promise = $http({
            url:'./Functions/Graph/getReport.php',
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