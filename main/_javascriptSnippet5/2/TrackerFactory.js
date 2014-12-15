indexApp.factory('trackerFactory', function($http){
    var factory = {};           
    
    factory.getfields = function(){
        var promise = $http({
            url:'./Functions/Form/getFields.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                var str = [];
                for(var p in obj)
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            data : {archived : 'f' }
        })

        return promise;
    };

    factory.insertlogs = function(data){

        var promise = $http({
            url:'./Functions/Tracker/insert.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                var str = [];
                for(var p in obj)
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            data : {data : JSON.stringify(data) }
        })

        return promise;  
    }

    return factory;
});