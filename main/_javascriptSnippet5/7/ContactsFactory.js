indexApp.factory('ContactsFactory', function($http){
    var factory = {};           
    
    factory.create = function(data){
        var promise = $http({
            url:'./Functions/Contacts/create.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                var str = [];
                for(var p in obj)
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                return str.join("&");
            },
            data : { data : JSON.stringify(data) } 
        })

        return promise;
    };

    factory.fetch = function(){
        var promise = $http({
            url:'./Functions/Contacts/fetch.php',
            method: 'POST'
        })

        return promise;  
    }

    return factory;
});