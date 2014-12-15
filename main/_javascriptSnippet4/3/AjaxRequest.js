app.factory('ajaxRequest', ['$http', function ($http) {
    return {
        get: function (url) {
            return $http.get(url);
        },
        deleteItem: function (url) {
            return $http.delete(url);
        },
        post: function (url, serverData) {
            return $http({
                url: url,
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                data: serverData
            });
        },
        put: function (url, serverData) {
            return $http({
                url: url,
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                data: JSON.stringify(serverData)
            });
        },
    };
}]);