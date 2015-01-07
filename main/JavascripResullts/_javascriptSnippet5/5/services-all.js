/**
 * Created by peekmo on 3/1/14.
 */

app.factory('proxy', ['$http', function ($http) {
    return {
        post: function (path, data, callback) {
            $http({
                method: 'POST',
                url: path,
                data: data,
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            })
                .success(function (data) {
                    callback(data);
                })
                .error(function (data) {
                    callback(data);
                });
        },

        get: function(path, callback) {
            $http.get(path)
                .success(function(data) {
                    callback(data);
                })
                .error(function(data) {
                    callback(data);
                });
        }
    }
}]);