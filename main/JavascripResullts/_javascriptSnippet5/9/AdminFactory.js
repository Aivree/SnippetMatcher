indexApp.factory('adminFactory', function($http){
    var factory = {};           
    
    factory.getfields = function(archived){
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
            data : {archived : archived}
        })

        return promise;
    };

    factory.getAllfields = function(){
        var promise = $http({
            url:'./Functions/Form/getAllFields.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })

        return promise;
    };

    factory.getGraphfields = function(){
        var promise = $http({
            url:'./Functions/Form/getGraphFields.php',
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })

        return promise;
    };

    factory.newfield = function(data){
        var code_arr = {
            field : data.field,
            type : data.type
        };

        var promise = $http({
            url:'./Functions/Form/newField.php',
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
    }

    factory.archivefield = function(data){
        var code_arr = {
            field : data.field,
            archived : data.archived
        };

        var promise = $http({
            url:'./Functions/Form/archiveField.php',
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
    }

    factory.renamefield = function(data){
        var code_arr = {
            field : data.field,
            name : data.name
        };

        var promise = $http({
            url:'./Functions/Form/renameField.php',
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
    } 

    return factory;
});